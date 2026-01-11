package org.example.Controller;

import org.example.dto.CTSP.SanPhamChiTietRespon;
import org.example.dto.hoadon.HoaDonChiTietRespon;
import org.example.dto.hoadon.HoaDonRespon;
import org.example.dto.hoadon.VoucherRespon;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    VoucherRepository voucherRepository;
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

    @GetMapping("/hoa-don/detail-info/{idHD}")
    public ResponseEntity<?> getHoaDonDetail(@PathVariable String idHD) {
        HoaDon hd = hoaDonRepo.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        return ResponseEntity.ok(hd.toResponse()); // trả về DTO với tenKH, sdt, email
    }


    @PostMapping("/hoa-don/add")
    public HoaDon addHoaDon() {

        HoaDon hd = new HoaDon();
        hd.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        hd.setLoaiHoaDon(0);
        hd.setTrangThai(0);
        hd.setKhachHang(null);
        hd.setNhanVien(null);
        hd.setTongTien(null);
        hd.setGiamGia(null);
        hd.setThanhTien(null);
        hd.setPhiVanChuyen(0);
        HoaDon hoaDonSaved = hoaDonRepo.save(hd);
        hoaDonSaved.setMa("HD" + hoaDonSaved.getId());
        hoaDonRepo.save(hoaDonSaved);
        return hoaDonSaved;
    }

    @GetMapping("/hoa-don/{id}")
    public HoaDonRespon getHoaDonById(@PathVariable String id) {
        return hoaDonRepo.findById(id)
                .map(HoaDon::toResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
    }

    @PostMapping("/hoa-don/them-san-pham")
    public HoaDonChiTietRespon themSanPhamVaoHoaDon(
            @RequestParam String idHoaDon,
            @RequestParam String idSPCT,
            @RequestParam int soLuong
    ) {

        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        SanPhamChiTiet spct = sanPhamChiTietRepo.findById(idSPCT)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        int tonKho = Integer.parseInt(spct.getSoLuong());
        if (tonKho <= soLuong) {
            throw new RuntimeException("Sản phẩm trong kho không đủ");
        }
        HDCT hdct = hdctRepo
                .findByHoaDonIdAndSanPhamChiTietId(idHoaDon, idSPCT)
                .orElse(null);
        if (hdct != null) {
            int slCu = Integer.parseInt(hdct.getSoLuong());
            hdct.setSoLuong(String.valueOf(slCu + soLuong));
        } else {
            hdct = new HDCT();
            hdct.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            hdct.setHoaDon(hoaDon);
            hdct.setSanPhamChiTiet(spct);
            hdct.setSoLuong(String.valueOf(soLuong));
            hdct.setGiaBan(spct.getGia());
        }
        spct.setSoLuong(String.valueOf(tonKho - soLuong));

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

        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
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
        hdctRepo.deleteAll(dsHDCT);
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
        kh.setId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        kh.setMa("KH" + System.currentTimeMillis() % 100000000);
        return khachHangRepo.save(kh);
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

    @PostMapping("/hoa-don/ap-dung-voucher")
    public HoaDonRespon apDungVoucher(
            @RequestParam String idHoaDon,
            @RequestParam String idVoucher
    ) {

        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn đã thanh toán, không thể áp dụng voucher");
        }

        Voucher voucher = voucherRepository.findById(idVoucher)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));

        if (voucher.getTrangThai() != 1) {
            throw new RuntimeException("Voucher không còn hiệu lực");
        }

        int soLuongVoucher = voucher.getSoLuong() != null
                ? Integer.parseInt(voucher.getSoLuong())
                : 0;

        if (soLuongVoucher <= 0) {
            throw new RuntimeException("Voucher đã hết lượt sử dụng");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getNgayBatDau()) || now.isAfter(voucher.getNgayKetThuc())) {
            throw new RuntimeException("Voucher đã hết hạn");
        }

        int tongTien = hdctRepo.tinhTongTien(idHoaDon);

        int dieuKienApDung = voucher.getDieuKienApDung() != null
                ? Integer.parseInt(voucher.getDieuKienApDung())
                : 0;

        if (tongTien < dieuKienApDung) {
            hoaDon.setVoucher(null);
            hoaDonRepo.save(hoaDon);
            return hoaDon.toResponse();
        }

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

        tienGiam = Math.min(tienGiam, tongTien);

        hoaDon.setVoucher(voucher);
        hoaDon.setNgaySua(LocalDateTime.now());

        hoaDonRepo.save(hoaDon);
        return hoaDon.toResponse();
    }

    @PostMapping("/hoa-don/huy-voucher")
    public HoaDonRespon boVoucher(@RequestParam String idHoaDon) {

        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn đã thanh toán");
        }

        hoaDon.setVoucher(null);
        hoaDon.setNgaySua(LocalDateTime.now());

        hoaDonRepo.save(hoaDon);

        return hoaDon.toResponse();
    }


    private int tinhGiamGia(Voucher voucher, int tongTien) {

        if (voucher == null) return 0;

        int dieuKien = voucher.getDieuKienApDung() != null
                ? Integer.parseInt(voucher.getDieuKienApDung())
                : 0;

        if (tongTien < dieuKien) return 0;

        int giaTriGiam = voucher.getGiaTriGiam() != null
                ? Integer.parseInt(voucher.getGiaTriGiam())
                : 0;

        Integer giamMax = (voucher.getGiamMax() != null && !voucher.getGiamMax().isEmpty())
                ? Integer.parseInt(voucher.getGiamMax())
                : null;

        int tienGiam;
        if (voucher.getLoai() == 0) {
            // giảm tiền cố định
            tienGiam = giaTriGiam;
        } else {
            // giảm %
            tienGiam = tongTien * giaTriGiam / 100;
            if (giamMax != null) {
                tienGiam = Math.min(tienGiam, giamMax);
            }
        }

        return Math.min(tienGiam, tongTien);
    }

    @GetMapping("/hoa-don/tinh-tien/{idHoaDon}")
    public HoaDonRespon tinhTienTam(@PathVariable String idHoaDon) {

        int tongTien = hdctRepo.tinhTongTien(idHoaDon);

        HoaDon hd = hoaDonRepo.findById(idHoaDon).orElseThrow();

        int giamGia = 0;
        Voucher voucher = hd.getVoucher();

        if (voucher != null) {
            // y hệt logic bạn viết
            giamGia = tinhGiamGia(voucher, tongTien);
        }

        return new HoaDonRespon(
                hd.getId(),
                tongTien,
                giamGia,
                tongTien - giamGia
        );
    }

    @PostMapping("/hoa-don/thanh-toan")
    public HoaDonRespon thanhToan(@RequestParam String idHoaDon) {
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

                return hoaDon.toResponse();
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

        return hoaDon.toResponse();
    }


}

