package org.example.Controller;

import org.example.entity.KhachHang;
import org.example.repository.KhachHangRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RequestMapping("khach-hang")
@RestController

public class KhachHangController {

    @Autowired
    KhachHangRepo khachHangRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

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


        if (updated.getPassw() != null && !updated.getPassw().isBlank()) {
            if (!updated.getPassw().startsWith("$2a$")) {
                kh.setPassw(encoder.encode(updated.getPassw()));
            }
        }

        return khachHangRepo.save(kh);
    }



    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody KhachHang kh) {

        // ===== TEN =====
        if (kh.getTen() == null || kh.getTen().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("field", "ten", "message", "Tên khách hàng không được để trống"));
        }

        validateTen(kh.getTen());

        if (khachHangRepo.existsByTenIgnoreCase(kh.getTen().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("field", "ten", "message", "Tên khách hàng đã tồn tại"));
        }

        // ===== EMAIL =====
        if (kh.getEmail() == null || kh.getEmail().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("field", "email", "message", "Email không được để trống"));
        }

        if (khachHangRepo.existsByEmail(kh.getEmail().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("field", "email", "message", "Email đã tồn tại"));
        }

        // ===== SDT =====
        if (kh.getSdt() == null || kh.getSdt().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("field", "sdt", "message", "Số điện thoại không được để trống"));
        }

        if (khachHangRepo.existsBySdt(kh.getSdt().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("field", "sdt", "message", "Số điện thoại đã tồn tại"));
        }

        // ===== PASSWORD =====
        if (kh.getPassw() == null || kh.getPassw().isBlank()) {
            kh.setPassw("123456");
        }
        kh.setPassw(encoder.encode(kh.getPassw()));

        // ===== DEFAULT =====
        kh.setTrangThai(1);
        kh.setNgayTao(LocalDateTime.now());

        return ResponseEntity.ok(khachHangRepo.save(kh));
    }




}
