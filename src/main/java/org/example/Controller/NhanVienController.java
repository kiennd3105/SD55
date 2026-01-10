package org.example.Controller;

import jakarta.validation.Valid;
import org.example.entity.NhanVien;
import org.example.repository.NhanVienRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;



import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("nhan-vien")

public class NhanVienController {

    @Autowired
    NhanVienRepo nhanVienRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;


    @GetMapping("/hien-thi")
    public List<NhanVien> hienThi() {
        return nhanVienRepo.findAll(
                Sort.by(Sort.Direction.DESC, "id")
        );
    }


    @GetMapping("/detail/{id}")
    public NhanVien getDetail(@PathVariable("id") String id) {
        return nhanVienRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
    }

    @PutMapping("/update/{id}")
    public NhanVien updateNhanVien(@PathVariable("id") String id, @RequestBody NhanVien updated) {
        NhanVien nv = nhanVienRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        nv.setTen(updated.getTen());
        nv.setEmail(updated.getEmail());
        nv.setGioiTinh(updated.getGioiTinh());
        nv.setPassw(updated.getPassw());
        nv.setImg(updated.getImg());
        nv.setTrangThai(updated.getTrangThai());
        nv.setIdQuyen(updated.getIdQuyen());
        nv.setNgaySua(LocalDateTime.now());

        return nhanVienRepo.save(nv);
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody NhanVien nv) {

        if (nv.getEmail() == null || nv.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email không được trống");
        }

        if (nhanVienRepo.existsByEmail(nv.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        nv.setMa("NV" + UUID.randomUUID().toString().substring(0, 8));


        nv.setPassw(encoder.encode("123456"));

        nv.setTrangThai(1);

        nv.setNgayTao(LocalDateTime.now());

        return ResponseEntity.ok(nhanVienRepo.save(nv));
    }




}
