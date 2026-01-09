package org.example.Controller;

import org.example.entity.KhachHang;
import org.example.repository.KhachHangRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RequestMapping("khach-hang")
@RestController

public class KhachHangController {

    @Autowired
    KhachHangRepo khachHangRepo;

    private void validateTen(String ten) {
        if (ten == null || ten.trim().isEmpty()) {
            throw new RuntimeException("Tên không được để trống");
        }

        if (ten.trim().length() < 2) {
            throw new RuntimeException("Tên phải có ít nhất 2 ký tự");
        }
    }


    @GetMapping("/hien-thi")
    public List<KhachHang> hienThi() {
        return khachHangRepo.findAll(
                Sort.by(Sort.Direction.DESC, "id")
        );
    }

    @GetMapping("/detail/{id}")
    public KhachHang getDetail(@PathVariable("id") String id) {
        return khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    @PutMapping("/update/{id}")
    public KhachHang updateKhachHang(
            @PathVariable("id") String id,
            @RequestBody KhachHang updated) {

        validateTen(updated.getTen());

        if (khachHangRepo.existsByTenIgnoreCaseAndIdNot(
                updated.getTen().trim(), id)) {
            throw new RuntimeException("Tên khách hàng đã tồn tại");
        }

        KhachHang kh = khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        kh.setTen(updated.getTen());
        kh.setEmail(updated.getEmail());
        kh.setGioiTinh(updated.getGioiTinh());
        kh.setSdt(updated.getSdt());
        kh.setDiaChi(updated.getDiaChi());
        kh.setTrangThai(updated.getTrangThai());
        kh.setNgaySua(LocalDateTime.now());

        return khachHangRepo.save(kh);
    }


    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody KhachHang kh) {

        validateTen(kh.getTen());

        if (khachHangRepo.existsByTenIgnoreCase(kh.getTen().trim())) {
            return ResponseEntity.badRequest().body("Tên khách hàng đã tồn tại");
        }

        return ResponseEntity.ok(khachHangRepo.save(kh));
    }


}
