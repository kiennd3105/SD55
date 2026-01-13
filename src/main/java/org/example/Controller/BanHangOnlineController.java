package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.giohang.CTGHRespon;
import org.example.dto.hoadon.ChiTietItem;
import org.example.dto.hoadon.ThanhToanRequest;
import org.example.dto.hoadon.VoucherRespon;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gio-hang")
@RequiredArgsConstructor
public class BanHangOnlineController {
    @Autowired
    GioHangRepo gioHangRepo;
    @Autowired
    CTGioHangRepo ctGioHangRepo;
    @Autowired
    SanPhamChiTietRepo sanPhamChiTietRepo;
    @Autowired
    KhachHangRepo khachHangRepo;
    @Autowired
    HoaDonRepo hoaDonRepo;
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    HDCTRepo hdctRepo;

    @GetMapping("/{idKH}")
    public List<CTGHRespon> getCart(@PathVariable String idKH) {

        KhachHang kh = khachHangRepo.findById(idKH)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Khách hàng không tồn tại"
                ));

        GioHang gh = gioHangRepo.findByKhachHang_Id(idKH)
                .orElseGet(() -> {
                    GioHang g = new GioHang();
                    g.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
                    g.setKhachHang(kh);
                    return gioHangRepo.save(g);
                });

        return ctGioHangRepo.findByGioHang(gh)
                .stream()
                .map(CTGioHang::toResponse)
                .toList();
    }

    @GetMapping("/hoa-don/voucher-ap-dung/{idHoaDon}")
    public List<VoucherRespon> voucherApDung(@PathVariable String idHoaDon) {

        HoaDon hd = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        Integer tongTien = hdctRepo.tinhTongTien(idHoaDon);
        if (tongTien == null) tongTien = 0;

        return voucherRepository.findVoucherApDung(tongTien)
                .stream()
                .map(Voucher::toResponse)
                .toList();
    }

    @PostMapping("/add")
    public void addToCart(@RequestParam String idKH,
                          @RequestParam String idCTSP,
                          @RequestParam int soLuong) {

        KhachHang kh = khachHangRepo.findById(idKH)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khách hàng không tồn tại"));

        GioHang gh = gioHangRepo.findByKhachHang_Id(idKH)
                .orElseGet(() -> {
                    GioHang g = new GioHang();
                    g.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
                    g.setKhachHang(kh);
                    return gioHangRepo.save(g);
                });

        CTGioHang ctgh = ctGioHangRepo
                .findByGioHang_IdAndSanPhamChiTiet_Id(gh.getId(), idCTSP)
                .orElseGet(() -> {
                    CTGioHang newCt = new CTGioHang();
                    newCt.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
                    newCt.setGioHang(gh);
                    newCt.setSanPhamChiTiet(sanPhamChiTietRepo.findById(idCTSP).orElseThrow());
                    newCt.setSoLuong("0");
                    newCt.setGia(newCt.getSanPhamChiTiet().getGia());
                    newCt.setNgayTao(LocalDateTime.now().toString());
                    return newCt;
                });

        int sl = Integer.parseInt(ctgh.getSoLuong());
        ctgh.setSoLuong(String.valueOf(sl + soLuong));

        ctGioHangRepo.save(ctgh);
    }


    @PutMapping("/update/{idCTGH}")
    public void updateQuantity(
            @PathVariable String idCTGH,
            @RequestParam int soLuong
    ) {
        CTGioHang ctgh = ctGioHangRepo.findById(idCTGH).orElseThrow();
        ctgh.setSoLuong(String.valueOf(soLuong));
        ctGioHangRepo.save(ctgh);
    }

    @DeleteMapping("/remove/{idCTGH}")
    public void removeItem(@PathVariable String idCTGH) {
        ctGioHangRepo.deleteById(idCTGH);
    }

    @DeleteMapping("/clear/{idKH}")
    public void clearCart(@PathVariable String idKH) {

        GioHang gh = gioHangRepo.findByKhachHang_Id(idKH).orElseThrow();
        ctGioHangRepo.deleteAll(ctGioHangRepo.findByGioHang(gh));
    }

    @GetMapping("/hoa-don/voucher-ap-dung/{tongTien}")
    public List<VoucherRespon> voucherApDung(@PathVariable Integer tongTien) {
        return voucherRepository.findVoucherApDung(tongTien)
                .stream()
                .map(Voucher::toResponse)
                .toList();
    }

    @GetMapping("/voucher/active")
    public List<VoucherRespon> getActiveVouchers() {
        LocalDateTime now = LocalDateTime.now();

        return voucherRepository.findByTrangThai(1)
                .stream()
                .filter(v -> v.getNgayBatDau().isBefore(now) && v.getNgayKetThuc().isAfter(now))
                .map(Voucher::toResponse)
                .toList();
    }


    @PostMapping("/online")
    public ResponseEntity<?> thanhToanOnline(@RequestBody ThanhToanRequest req) {

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

        // 2️⃣ Tạo hóa đơn
        HoaDon hd = new HoaDon();
        hd.setMa("HD" + System.currentTimeMillis() % 100000000);
        hd.setKhachHang(kh);
        hd.setTrangThai(2);      // online
        hd.setLoaiHoaDon(1);     // online
        hd.setDiaChi(req.getDiaChi());
        hd.setPhiVanChuyen(req.getPhiVanChuyen());
        hd.setTongTien(req.getTongTien());
        hd.setGiamGia(req.getGiamGia());
        hd.setThanhTien(req.getThanhTien());
        hd.setNgayDatHang(LocalDateTime.now());

        // 3️⃣ Voucher
        if (req.getIdVoucher() != null) {
            Voucher voucher = voucherRepository.findById(req.getIdVoucher())
                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

            int soLuong = Integer.parseInt(voucher.getSoLuong());
            if (soLuong <= 0) {
                return ResponseEntity.badRequest().body("Voucher đã hết");
            }

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
        }
        GioHang gh = gioHangRepo.findByKhachHang_Id(req.getIdKH())
                .orElseThrow();

        ctGioHangRepo.deleteAll(
                ctGioHangRepo.findByGioHang(gh)
        );
        return ResponseEntity.ok(hd);
    }

}
