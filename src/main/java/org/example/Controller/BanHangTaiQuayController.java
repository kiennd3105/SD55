package org.example.Controller;

import org.example.dto.CTSP.SanPhamChiTietRespon;
import org.example.dto.hoadon.HoaDonChiTietRespon;
import org.example.dto.hoadon.HoaDonRespon;
import org.example.dto.hoadon.VoucherRespon;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("ban-hang")
public class BanHangTaiQuayController {
    @Autowired
    HoaDonRepo hoaDonRepo;
    @Autowired
    HDCTRepo hdctRepo;
    @Autowired
    KhachHangRepo khachHangRepo;
    @Autowired
    NhanVienRepo nhanVienRepo;
    @Autowired
    VoucherRepository voucherRepository ;
    @Autowired
    SanPhamChiTietRepo sanPhamChiTietRepo;

    @GetMapping("/dang-ban")
    public List<SanPhamChiTietRespon> getDangBan() {
        return sanPhamChiTietRepo.findAllDangBan()
                .stream()
                .map(SanPhamChiTiet::toResponse)
                .toList();
    }

    @GetMapping("/hoa-don/tai-quay")
    public List<HoaDonRespon> getHoaDonTaiQuay() {
        return hoaDonRepo.findByLoaiHoaDonAndTrangThai(0, 0)
                .stream()
                .map(HoaDon::toResponse)
                .toList();
    }

    @GetMapping("/hoa-don/detail/{idHD}")
    public List<HoaDonChiTietRespon> detailHoaDon(@PathVariable String idHD) {

        return hdctRepo.findByHoaDonId(idHD)
                .stream()
                .map(HDCT::toResponse)
                .toList();
    }
    @GetMapping("/hoa-don/detail-info/{id}")
    public HoaDon getInfo(@PathVariable String id) {
        return hoaDonRepo.findById(id).orElse(null);
    }
    private NhanVien getNhanVienMacDinh() {
        return nhanVienRepo.findById("NV_MAC_DINH")
                .orElseThrow(() -> new RuntimeException("Chưa có nhân viên mặc định"));
    }

    @PostMapping("/hoa-don/add")
    public HoaDon addHoaDon() {


        HoaDon hd = new HoaDon();
        hd.setId(UUID.randomUUID().toString().substring(0,8).toUpperCase());
        hd.setLoaiHoaDon(0);
        hd.setTrangThai(0);
        hd.setKhachHang(null);
        hd.setNhanVien(null);
        hd.setTongTien(0);
        hd.setGiamGia(0);
        hd.setThanhTien(0);
        hd.setPhiVanChuyen(0);

        HoaDon hoaDonSaved = hoaDonRepo.save(hd);
        hoaDonSaved.setMa("HD" + hoaDonSaved.getId());
        hoaDonRepo.save(hoaDonSaved);

        HDCT hdct = new HDCT();
        hdct.setHoaDon(hoaDonSaved);
        hdct.setSanPhamChiTiet(null);
        hdct.setSoLuong("0");
        hdct.setGiaBan(null);
        hdctRepo.save(hdct);

        return hoaDonSaved;
    }

    @PostMapping("/hoa-don/them-san-pham")
    public HoaDonChiTietRespon themSanPhamVaoHoaDon(
            @RequestParam String idHoaDon,
            @RequestParam String idSPCT,
            @RequestParam int soLuong
    ) {

        // 1. Lấy hóa đơn
        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        // 2. Lấy SPCT
        SanPhamChiTiet spct = sanPhamChiTietRepo.findById(idSPCT)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        int tonKho = Integer.parseInt(spct.getSoLuong());
        if (tonKho <= soLuong) {
            throw new RuntimeException("Sản phẩm trong kho không đủ");
        }



        // 3. Kiểm tra đã có HDCT chưa
        HDCT hdct = hdctRepo
                .findByHoaDonIdAndSanPhamChiTietId(idHoaDon, idSPCT)
                .orElse(null);

        if (hdct != null) {
            int slCu = Integer.parseInt(hdct.getSoLuong());
            hdct.setSoLuong(String.valueOf(slCu + soLuong));
        } else {
            // ➕ Tạo mới HDCT
            hdct = new HDCT();
            hdct.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            hdct.setHoaDon(hoaDon);
            hdct.setSanPhamChiTiet(spct);
            hdct.setSoLuong("1");
            hdct.setGiaBan(spct.getGia());
        }

        // 4. Trừ tồn kho
        spct.setSoLuong(String.valueOf(tonKho - soLuong));

        // 5. Save
        sanPhamChiTietRepo.save(spct);
        hdctRepo.save(hdct);

        return hdct.toResponse();
    }
    @PostMapping("/hoa-don/cap-nhat-so-luong")
    public HoaDonChiTietRespon capNhatSoLuong(
            @RequestParam String idHDCT,
            @RequestParam int soLuongMoi
    ) {

        HDCT hdct = hdctRepo.findById(idHDCT)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CTHD"));

        SanPhamChiTiet spct = hdct.getSanPhamChiTiet();

        int slCu = Integer.parseInt(hdct.getSoLuong());
        int tonKho = Integer.parseInt(spct.getSoLuong());

        int chenhLech = soLuongMoi - slCu;

        if (chenhLech > 0 && tonKho < chenhLech) {
            throw new RuntimeException("Không đủ tồn kho");
        }

        hdct.setSoLuong(String.valueOf(soLuongMoi));
        spct.setSoLuong(String.valueOf(tonKho - chenhLech));

        sanPhamChiTietRepo.save(spct);
        hdctRepo.save(hdct);

        return hdct.toResponse();
    }

    @DeleteMapping("/hoa-don/xoa-san-pham/{idHDCT}")
    public void xoaSanPhamKhoiHoaDon(@PathVariable String idHDCT) {
        HDCT hdct = hdctRepo.findById(idHDCT)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CTHD"));
        SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
        if (spct != null) {
            int slBan = Integer.parseInt(hdct.getSoLuong());
            int tonKho = Integer.parseInt(spct.getSoLuong());
            spct.setSoLuong(String.valueOf(tonKho + slBan));
            sanPhamChiTietRepo.save(spct);
        }
        hdctRepo.delete(hdct);
    }
    @DeleteMapping("/hoa-don/xoa/{idHoaDon}")
    @Transactional
    public void xoaHoaDon(@PathVariable String idHoaDon) {

        // 1. Lấy hóa đơn
        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        // 2. Lấy danh sách chi tiết hóa đơn
        List<HDCT> dsHDCT = hdctRepo.findByHoaDonId(idHoaDon);

        for (HDCT hdct : dsHDCT) {

            SanPhamChiTiet spct = hdct.getSanPhamChiTiet();
            if (spct != null) {

                int slBan = Integer.parseInt(hdct.getSoLuong());
                int tonKho = Integer.parseInt(spct.getSoLuong());

                // 👉 Hoàn kho
                spct.setSoLuong(String.valueOf(tonKho + slBan));
                sanPhamChiTietRepo.save(spct);
            }
        }

        // 3. Xóa chi tiết hóa đơn
        hdctRepo.deleteAll(dsHDCT);

        // 4. Xóa hóa đơn
        hoaDonRepo.delete(hoaDon);
    }
    @PostMapping("/hoa-don/them-khach-hang")
    public HoaDonRespon themKhachHangVaoHoaDon(
            @RequestParam String idHoaDon,
            @RequestParam String idKhachHang
    ) {
        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn đã thanh toán");
        }
        KhachHang kh = khachHangRepo.findById(idKhachHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        hoaDon.setKhachHang(kh);
        hoaDonRepo.save(hoaDon);
        return hoaDon.toResponse();
    }
    @PostMapping("/hoa-don/xoa-khach-hang")
    public HoaDonRespon xoaKhachHangKhoiHoaDon(
            @RequestParam String idHoaDon
    ) {
        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn đã thanh toán");
        }

        hoaDon.setKhachHang(null);
        hoaDonRepo.save(hoaDon);

        return hoaDon.toResponse();
    }
    @PostMapping("/khach-hang/add")
    public KhachHang addKhachHang(@RequestBody KhachHang kh) {
        kh.setId(UUID.randomUUID().toString().substring(0,8).toUpperCase());
        return khachHangRepo.save(kh);
    }


    @GetMapping("/hoa-don/voucher-ap-dung/{idHoaDon}")
    public List<VoucherRespon> voucherApDung(@PathVariable String idHoaDon) {

        HoaDon hd = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        int tongTien = hdctRepo.tinhTongTien(idHoaDon);

        return voucherRepository.findVoucherApDung(tongTien)
                .stream()
                .map(Voucher::toResponse)
                .toList();
    }


    @PostMapping("/hoa-don/ap-dung-voucher")
    public HoaDonRespon apDungVoucher(
            @RequestParam String idHoaDon,
            @RequestParam String idVoucher
    ) {

        // 1️⃣ Lấy hóa đơn
        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn đã thanh toán, không thể áp dụng voucher");
        }

        // 2️⃣ Lấy voucher
        Voucher voucher = voucherRepository.findById(idVoucher)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));

        // 3️⃣ Check trạng thái
        if (voucher.getTrangThai() != 1) {
            throw new RuntimeException("Voucher không còn hiệu lực");
        }

        // 4️⃣ Check số lượng
        int soLuongVoucher = voucher.getSoLuong() != null
                ? Integer.parseInt(voucher.getSoLuong())
                : 0;

        if (soLuongVoucher <= 0) {
            throw new RuntimeException("Voucher đã hết lượt sử dụng");
        }

        // 5️⃣ Check thời gian
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getNgayBatDau()) || now.isAfter(voucher.getNgayKetThuc())) {
            throw new RuntimeException("Voucher đã hết hạn");
        }

        // 6️⃣ Tính tổng tiền hóa đơn
        int tongTien = hdctRepo.tinhTongTien(idHoaDon);

        int dieuKienApDung = voucher.getDieuKienApDung() != null
                ? Integer.parseInt(voucher.getDieuKienApDung())
                : 0;

        if (tongTien < dieuKienApDung) {
            throw new RuntimeException("Không đủ điều kiện áp dụng voucher");
        }

        // 7️⃣ Tính tiền giảm
        int loai = voucher.getLoai() != null ? voucher.getLoai() : 0;
        int giaTriGiam = voucher.getGiaTriGiam() != null
                ? Integer.parseInt(voucher.getGiaTriGiam())
                : 0;

        Integer giamMax = (voucher.getGiamMax() != null && !voucher.getGiamMax().isEmpty())
                ? Integer.parseInt(voucher.getGiamMax())
                : null;

        int tienGiam;
        if (loai == 0) {
            tienGiam = giaTriGiam;
        } else {
            tienGiam = tongTien * giaTriGiam / 100;
            if (giamMax != null) {
                tienGiam = Math.min(tienGiam, giamMax);
            }
        }

        // Không cho giảm quá tổng tiền
        tienGiam = Math.min(tienGiam, tongTien);

        // 8️⃣ Gán voucher + tiền
        hoaDon.setVoucher(voucher);
        hoaDon.setTongTien(tongTien);
        hoaDon.setGiamGia(tienGiam);
        hoaDon.setThanhTien(tongTien - tienGiam);
        hoaDon.setNgaySua(LocalDateTime.now());

        hoaDonRepo.save(hoaDon);

        // 9️⃣ Trừ số lượng voucher
        voucher.setSoLuong(String.valueOf(soLuongVoucher - 1));
        voucherRepository.save(voucher);

        return hoaDon.toResponse();
    }

    @PostMapping("/hoa-don/thanh-toan")
    public HoaDonRespon thanhToan(@RequestParam String idHoaDon) {

        // 1. Lấy hóa đơn
        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        // 2. Kiểm tra trạng thái
        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn không ở trạng thái chờ thanh toán");
        }

        // 3. Tính tổng tiền từ HDCT
        Integer tongTien = hdctRepo.tinhTongTien(idHoaDon);
        if (tongTien == null || tongTien <= 0) {
            throw new RuntimeException("Hóa đơn chưa có sản phẩm");
        }

        hoaDon.setTongTien(tongTien);

        // 4. Xử lý voucher (nếu có)
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
                throw new RuntimeException("Không đủ điều kiện áp dụng voucher");
            }

            if (voucher.getLoai() == 0) {
                tienGiam = giaTriGiam;
            } else {
                tienGiam = tongTien * giaTriGiam / 100;
                if (giamMax != null) {
                    tienGiam = Math.min(tienGiam, giamMax);
                }
            }

            tienGiam = Math.min(tienGiam, tongTien);

            // trừ số lượng voucher
            int soLuongVC = Integer.parseInt(voucher.getSoLuong());
            if (soLuongVC <= 0) {
                throw new RuntimeException("Voucher đã hết lượt");
            }
            voucher.setSoLuong(String.valueOf(soLuongVC - 1));
            voucherRepository.save(voucher);
        }

        // 5. Cập nhật tiền
        hoaDon.setGiamGia(tienGiam);
        hoaDon.setThanhTien(tongTien - tienGiam);

        // 6. Cập nhật trạng thái hóa đơn
        hoaDon.setTrangThai(1); // 1 = ĐÃ THANH TOÁN
        hoaDon.setNgayDatHang(LocalDateTime.now());
        hoaDon.setNgaySua(LocalDateTime.now());

        hoaDonRepo.save(hoaDon);

        return hoaDon.toResponse();
    }





}

