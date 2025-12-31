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
    public KhachHang updateKhachHang(@PathVariable("id") String id, @RequestBody KhachHang updated) {
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
        kh.setId(UUID.randomUUID().toString().substring(0,8).toUpperCase());
        kh.setNgayTao(LocalDateTime.now());
        kh.setNgaySua(LocalDateTime.now());

        return ResponseEntity.ok(khachHangRepo.save(kh));
    }
}
