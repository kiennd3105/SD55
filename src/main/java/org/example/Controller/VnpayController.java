package org.example.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.dto.ResponsePayment;
import org.example.dto.hoadon.ChiTietItem;
import org.example.dto.hoadon.HoaDonRespon;
import org.example.dto.hoadon.ThanhToanRequest;
import org.example.entity.*;
import org.example.repository.*;
import org.example.vnpay.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/vnpay")
@CrossOrigin
public class VnpayController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private KhachHangRepo khachHangRepo;

    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private HoaDonRepo hoaDonRepo;

    @Autowired
    private CTGioHangRepo ctGioHangRepo;

    @Autowired
    private HDCTRepo hdctRepo;

    @PostMapping("/urlpayment")
    public ResponseEntity<?> getUrlPayment(@RequestBody ThanhToanRequest req, HttpServletRequest request, HttpSession session){
        // 0️⃣ Validate request
        if (req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Danh sách sản phẩm trống");
        }

        KhachHang kh = khachHangRepo.findById(req.getIdKH())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        List<String> spKhongHopLe = new ArrayList<>();

        for (ChiTietItem item : req.getItems()) {
            System.out.println("ITEM NHẬN ĐƯỢC: idCTSP = " + item.getIdCTSP());
            if (item.getIdCTSP() == null || item.getIdCTSP().isBlank()) {
                return ResponseEntity.badRequest()
                        .body("Thiếu idCTSP trong item thanh toán");
            }

            SanPhamChiTiet ctsp = sanPhamChiTietRepo.findById(item.getIdCTSP())
                    .orElseThrow(() -> new RuntimeException("CTSP không tồn tại: " + item.getIdCTSP()));

            int soLuongMua = Integer.parseInt(item.getSoLuong());

            // trạng thái bán
            if (ctsp.getTrangThai() != 1) {
                spKhongHopLe.add(ctsp.getSanPham().getTen() + " không còn bán");
                continue;
            }

            int tonKho = Integer.parseInt(ctsp.getSoLuong());
            if (soLuongMua > tonKho) {
                spKhongHopLe.add(
                        ctsp.getSanPham().getTen() + " (còn " + tonKho + ")"
                );
            }
        }

        if (!spKhongHopLe.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Sản phẩm không hợp lệ: " + String.join(", ", spKhongHopLe));
        }

        if (req.getIdVoucher() != null) {
            Voucher voucher = voucherRepository.findById(req.getIdVoucher())
                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
            int soLuong = Integer.parseInt(voucher.getSoLuong());
            if (soLuong <= 0) {
                return ResponseEntity.badRequest().body("Voucher đã hết");
            }
        }


        Integer totalAmount = req.getThanhTien();
        String orderId = String.valueOf(System.currentTimeMillis());
        String scheme = request.getScheme();      // http hoặc https
        String serverName = request.getServerName(); // domain hoặc IP
        int serverPort = request.getServerPort();    // 8084

        String baseUrl = scheme + "://" + serverName + ":" + serverPort;
        String vnpayUrl = vnPayService.createOrder(totalAmount, orderId, baseUrl+"/vnpay/check-user-payment");
        ResponsePayment responsePayment = new ResponsePayment(vnpayUrl,orderId,null);
        session.setAttribute("cartSession", req);
        return ResponseEntity.ok(responsePayment);
    }

    @GetMapping("/check-user-payment")
    public String checkPayment(HttpServletRequest request, HttpSession session){
        ThanhToanRequest req = (ThanhToanRequest) session.getAttribute("cartSession");
        int check = vnPayService.orderReturn(request);
        if(check == 1){
            // 2️⃣ Tạo hóa đơn
            KhachHang kh = khachHangRepo.findById(req.getIdKH())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

            HoaDon hd = new HoaDon();
            hd.setMa("HD" + System.currentTimeMillis() % 100000000);
            hd.setKhachHang(kh);
            hd.setTrangThai(2);      // online
            hd.setLoaiHoaDon(1);     // online
            hd.setPhuongThucThanhToan("Thanh toán Vnpay");
            hd.setDiaChi(req.getDiaChi());
            hd.setPhiVanChuyen(req.getPhiVanChuyen());
            hd.setTongTien(req.getTongTien());
            hd.setGiamGia(req.getGiamGia());
            hd.setThanhTien(req.getThanhTien());
            hd.setNgayDatHang(LocalDateTime.now());

            if (req.getIdVoucher() != null) {
                Voucher voucher = voucherRepository.findById(req.getIdVoucher())
                        .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
                int soLuong = Integer.parseInt(voucher.getSoLuong());
                voucher.setSoLuong(String.valueOf(soLuong - 1));
                voucherRepository.save(voucher);
                hd.setVoucher(voucher);
            }

            hoaDonRepo.save(hd);

            // 4️⃣ Lưu HDCT + trừ kho
            for (ChiTietItem item : req.getItems()) {

                SanPhamChiTiet ctsp = sanPhamChiTietRepo.findById(item.getIdCTSP()).get();

                int soLuongMua = Integer.parseInt(item.getSoLuong());
                int tonKho = Integer.parseInt(ctsp.getSoLuong());

                ctsp.setSoLuong(String.valueOf(tonKho - soLuongMua));
                sanPhamChiTietRepo.save(ctsp);

                HDCT hdct = new HDCT();
                hdct.setHoaDon(hd);
                hdct.setSanPhamChiTiet(ctsp);
                hdct.setGiaBan(item.getGiaBan());
                hdct.setSoLuong(item.getSoLuong());

                hdctRepo.save(hdct);
                ctGioHangRepo.deleteBySpCtAndKh(item.getIdCTSP(), kh.getId());
            }
            session.removeAttribute("cartSession");

            return "redirect:/user/layout-user.html#!/hoa-don";
        }
        else{
            session.removeAttribute("cartSession");
            return "redirect:/user/layout-user.html#!/hoa-don";
        }
    }


    @PostMapping("/tao-link-tai-quay")
    public ResponseEntity<?> linkThanhToanTaiQuay(@RequestParam String idHoaDon, HttpServletRequest request, HttpSession session) {
        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn không ở trạng thái chờ thanh toán");
        }

        Integer tongTien = hdctRepo.tinhTongTien(idHoaDon);
        if (tongTien == null || tongTien <= 0) {
            throw new RuntimeException("Hóa đơn chưa có sản phẩm");
        }

        int tienGiam = 0;
        Voucher voucher = hoaDon.getVoucher();
        if (voucher != null) {
            if (voucher.getTrangThai() != 1) {
                throw new RuntimeException("Voucher không còn hiệu lực");
            }

            int dieuKien = voucher.getDieuKienApDung() != null
                    ? Integer.parseInt(voucher.getDieuKienApDung())
                    : 0;

            int giaTriGiam = voucher.getGiaTriGiam() != null
                    ? Integer.parseInt(voucher.getGiaTriGiam())
                    : 0;

            Integer giamMax = (voucher.getGiamMax() != null && !voucher.getGiamMax().isEmpty())
                    ? Integer.parseInt(voucher.getGiamMax())
                    : null;

            if (tongTien < dieuKien) {
                hoaDon.setVoucher(null);
                hoaDonRepo.save(hoaDon);
            }

            if (voucher.getLoai() == 0) {
                tienGiam = giaTriGiam;
            }
            else {
                tienGiam = tongTien * giaTriGiam / 100;
                if (giamMax != null) {
                    tienGiam = Math.min(tienGiam, giamMax);
                }
            }

            tienGiam = Math.min(tienGiam, tongTien);

            int soLuongVC = Integer.parseInt(voucher.getSoLuong());
            if (soLuongVC <= 0) {
                throw new RuntimeException("Voucher đã hết lượt");
            }
        }

        Integer totalAmount = tongTien - tienGiam;
        String orderId = idHoaDon;
        String scheme = request.getScheme();      // http hoặc https
        String serverName = request.getServerName(); // domain hoặc IP
        int serverPort = request.getServerPort();    // 8084

        String baseUrl = scheme + "://" + serverName + ":" + serverPort;
        String vnpayUrl = vnPayService.createOrder(totalAmount, orderId, baseUrl+"/vnpay/check-bantaiquay-payment");
        ResponsePayment responsePayment = new ResponsePayment(vnpayUrl,orderId,null);
        return ResponseEntity.ok(responsePayment);
    }

    @GetMapping("/check-bantaiquay-payment")
    public String checkPaymentTaiQuay(HttpServletRequest request, @RequestParam("vnp_OrderInfo") String idHoaDon){
        int check = vnPayService.orderReturn(request);
        if(check == 1){
            HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
            if (hoaDon.getTrangThai() != 0) {
                throw new RuntimeException("Hóa đơn không ở trạng thái chờ thanh toán");
            }
            Integer tongTien = hdctRepo.tinhTongTien(idHoaDon);
            if (tongTien == null || tongTien <= 0) {
                throw new RuntimeException("Hóa đơn chưa có sản phẩm");
            }
            hoaDon.setTongTien(tongTien);
            int tienGiam = 0;
            Voucher voucher = hoaDon.getVoucher();
            if (voucher != null) {
                if (voucher.getTrangThai() != 1) {
                    throw new RuntimeException("Voucher không còn hiệu lực");
                }

                int dieuKien = voucher.getDieuKienApDung() != null
                        ? Integer.parseInt(voucher.getDieuKienApDung())
                        : 0;

                int giaTriGiam = voucher.getGiaTriGiam() != null
                        ? Integer.parseInt(voucher.getGiaTriGiam())
                        : 0;

                Integer giamMax = (voucher.getGiamMax() != null && !voucher.getGiamMax().isEmpty())
                        ? Integer.parseInt(voucher.getGiamMax())
                        : null;

                if (tongTien < dieuKien) {
                    hoaDon.setVoucher(null);
                    hoaDonRepo.save(hoaDon);
                }

                if (voucher.getLoai() == 0) {
                    tienGiam = giaTriGiam;
                }
                else {
                    tienGiam = tongTien * giaTriGiam / 100;
                    if (giamMax != null) {
                        tienGiam = Math.min(tienGiam, giamMax);
                    }
                }

                tienGiam = Math.min(tienGiam, tongTien);

                int soLuongVC = Integer.parseInt(voucher.getSoLuong());
                if (soLuongVC <= 0) {
                    throw new RuntimeException("Voucher đã hết lượt");
                }
                voucher.setSoLuong(String.valueOf(soLuongVC - 1));
                voucherRepository.save(voucher);
            }

            hoaDon.setGiamGia(tienGiam);
            hoaDon.setThanhTien(tongTien - tienGiam);
            hoaDon.setTrangThai(1);
            hoaDon.setNgayDatHang(LocalDateTime.now());
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDonRepo.save(hoaDon);

            return "redirect:/ban_tai_quay/layout.html#!/hoadon";
        }
        else{
            return "redirect:/ban_tai_quay/layout.html#!/taiquay";
        }
    }
}
