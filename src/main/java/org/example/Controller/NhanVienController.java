package org.example.Controller;

import jakarta.validation.Valid;
import org.example.entity.NhanVien;
import org.example.repository.NhanVienRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("nhan-vien")

public class NhanVienController {

    @Autowired
    NhanVienRepo nhanVienRepo;

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
    public ResponseEntity<?> add(
            @Valid @RequestBody NhanVien nhanVien,
            BindingResult result) {

        Map<String, String> errors = new HashMap<>();

        if (result.hasErrors()) {
            result.getFieldErrors().forEach(e ->
                    errors.put(e.getField(), e.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        if (nhanVienRepo.existsByTenIgnoreCase(nhanVien.getTen().trim())) {
            errors.put("ten", "Tên nhân viên đã tồn tại");
            return ResponseEntity.badRequest().body(errors);
        }

        NhanVien last = nhanVienRepo.findTopByOrderByMaDesc();

        int next = 1;
        if (last != null && last.getMa() != null && last.getMa().startsWith("NV")) {
            try {
                next = Integer.parseInt(last.getMa().substring(2)) + 1;
            } catch (Exception e) {
                next = 1;
            }
        }

        nhanVien.setMa("NV" + String.format("%08d", next));

        if (nhanVien.getImg() == null || nhanVien.getImg().isEmpty()) {
            nhanVien.setImg("default.png");
        }

        return ResponseEntity.ok(nhanVienRepo.save(nhanVien));
    }



}
