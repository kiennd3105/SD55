package org.example.Controller;

import org.example.entity.HDCT;
import org.example.entity.HoaDon;
import org.example.entity.NhanVien;
import org.example.entity.SanPhamChiTiet;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hoa-don")
@CrossOrigin(origins = "*")
public class HoaDonController {

    @Autowired
    private HoaDonRepo hoaDonRepo;
    @Autowired
    KhachHangRepo khachHangRepo;
    @Autowired
    NhanVienRepo nhanVienRepo;
    @Autowired
    HDCTRepo hdctRepo;
    @Autowired
    SanPhamChiTietRepo sanPhamChiTietRepo;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<HoaDon> hoaDonList = hoaDonRepo.findAll();
        return ResponseEntity.ok(
                hoaDonList.stream()
                        .map(HoaDon::toResponse)
                        .toList()
        );
    }

    @GetMapping("/getByKhachHangOnline/{idKH}")
    public ResponseEntity<?> getByKhachHangOnline(@PathVariable String idKH) {
        List<HoaDon> hoaDonList = hoaDonRepo.findByKhachHang_IdAndLoaiHoaDonOrderByNgayTaoDesc(idKH, 1);
        return ResponseEntity.ok(
                hoaDonList.stream()
                        .map(HoaDon::toResponse)
                        .toList()
        );
    }

    @PutMapping("/doi-trang-thai")
    @Transactional
    public ResponseEntity<?> doiTrangThai(
            @RequestParam String idHoaDon,
            @RequestParam Integer trangThai,
            @RequestParam String idNhanVien
    ) {
        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        NhanVien nv = nhanVienRepo.findById(idNhanVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        Integer trangThaiCu = hoaDon.getTrangThai();
        if (trangThai == 5 && trangThaiCu != 5) {
            List<HDCT> dsCTHD = hdctRepo.findByHoaDonId(idHoaDon);
            for (HDCT ct : dsCTHD) {
                SanPhamChiTiet spct = ct.getSanPhamChiTiet();
                int soLuongTon = 0;
                if (spct.getSoLuong() != null && !spct.getSoLuong().isEmpty()) {
                    soLuongTon = Integer.parseInt(spct.getSoLuong());
                }
                int soLuongHoan = 0;
                if (ct.getSoLuong() != null && !ct.getSoLuong().isEmpty()) {
                    soLuongHoan = Integer.parseInt(ct.getSoLuong());
                }
                int tongMoi = soLuongTon + soLuongHoan;
                spct.setSoLuong(String.valueOf(tongMoi));
                sanPhamChiTietRepo.save(spct);
            }
        }

        hoaDon.setTrangThai(trangThai);
        hoaDon.setNhanVien(nv);
        hoaDon.setNgaySua(LocalDateTime.now());

        hoaDonRepo.save(hoaDon);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật trạng thái thành công"
        ));
    }

    @PutMapping("/huy-trang-thai")
    @Transactional
    public ResponseEntity<?> huyTrangThai(
            @RequestParam String idHoaDon,
            @RequestParam Integer trangThai
    ) {
        HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        Integer trangThaiCu = hoaDon.getTrangThai();
        if (trangThai == 5 && trangThaiCu != 5) {
            List<HDCT> dsCTHD = hdctRepo.findByHoaDonId(idHoaDon);
            for (HDCT ct : dsCTHD) {
                SanPhamChiTiet spct = ct.getSanPhamChiTiet();
                int soLuongTon = 0;
                if (spct.getSoLuong() != null && !spct.getSoLuong().isEmpty()) {
                    soLuongTon = Integer.parseInt(spct.getSoLuong());
                }
                int soLuongHoan = 0;
                if (ct.getSoLuong() != null && !ct.getSoLuong().isEmpty()) {
                    soLuongHoan = Integer.parseInt(ct.getSoLuong());
                }
                int tongMoi = soLuongTon + soLuongHoan;
                spct.setSoLuong(String.valueOf(tongMoi));
                sanPhamChiTietRepo.save(spct);
            }
        }

        hoaDon.setTrangThai(trangThai);
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDonRepo.save(hoaDon);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật trạng thái thành công"
        ));
    }


}
