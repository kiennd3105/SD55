package org.example.Controller;

import org.example.dto.login.LoginRequest;
import org.example.dto.login.LoginResponse;
import org.example.dto.login.RegisterRequest;
import org.example.entity.Admin;
import org.example.entity.KhachHang;
import org.example.entity.NhanVien;
import org.example.repository.AdminRepo;
import org.example.repository.KhachHangRepo;
import org.example.repository.NhanVienRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8084")

public class AuthController {

    @Autowired
    private NhanVienRepo nhanVienRepo;
    @Autowired
    private KhachHangRepo khachHangRepo;
    @Autowired
    private AdminRepo adminRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        if (req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body("Thiếu thông tin đăng nhập");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 🔹 1. Kiểm tra ADMIN
        Optional<Admin> adminOpt = adminRepo.findByUsername(req.getUsername());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();

            if (!encoder.matches(req.getPassword(), admin.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Sai tên đăng nhập hoặc mật khẩu");
            }

            return ResponseEntity.ok(new LoginResponse("ADMIN", admin));
        }

        // 🔹 2. Kiểm tra USER
        Optional<KhachHang> khOpt = khachHangRepo.findByTen(req.getUsername());
        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();

            if (!encoder.matches(req.getPassword(), kh.getPassw())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Sai tên đăng nhập hoặc mật khẩu");
            }

            return ResponseEntity.ok(new LoginResponse("USER", kh));
        }

        // 🔹 3. Không tồn tại
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Sai tên đăng nhập hoặc mật khẩu");
    }





    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (khachHangRepo.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        KhachHang kh = new KhachHang();
        kh.setTen(req.getTen());
        kh.setEmail(req.getEmail());

        // 🔐 HASH MẬT KHẨU
        kh.setPassw(encoder.encode(req.getPassw()));

        kh.setSdt(req.getSdt());
        kh.setDiaChi(req.getDiaChi());
        kh.setGioiTinh(req.getGioiTinh());
        kh.setTrangThai(1);
        kh.setNgayTao(LocalDateTime.now());
        kh.setNgaySua(LocalDateTime.now());

        khachHangRepo.save(kh);

        return ResponseEntity.ok("Đăng ký thành công");
    }

}
