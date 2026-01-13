package org.example.Controller;

import org.example.entity.TheLoai;
import org.example.repository.TheLoaiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("the-loai")
public class TheLoaiController {

    @Autowired
    private TheLoaiRepo theLoaiRepo;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<TheLoai> list = theLoaiRepo.findAll();
        return ResponseEntity.ok(
                list.stream().map(TheLoai::toResponse).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") String id) {
        Optional<TheLoai> opt = theLoaiRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thể loại!");
        }
        return ResponseEntity.ok(opt.get().toResponse());
    }


    @PostMapping
    public ResponseEntity<?> add(@RequestBody TheLoai req) {
        String ma = req.getMa() == null ? "" : req.getMa().trim();
        String ten = req.getTen() == null ? "" : req.getTen().trim();

        if (ma.isBlank()) return ResponseEntity.badRequest().body("Mã không được để trống!");
        if (ten.isBlank()) return ResponseEntity.badRequest().body("Tên không được để trống!");

        if (!ma.matches("^[A-Za-z0-9_-]+$")) {
            return ResponseEntity.badRequest().body("Mã chỉ gồm chữ/số/_/- và không có khoảng trắng");
        }
        if (!ten.matches("^[A-Za-zÀ-ỹ0-9 ]+$")) {
            return ResponseEntity.badRequest().body("Tên không được chứa ký tự đặc biệt");
        }

        if (theLoaiRepo.existsByMaIgnoreCase(ma)) {
            return ResponseEntity.badRequest().body("Mã đã tồn tại");
        }
        if (theLoaiRepo.existsByTenIgnoreCase(ten)) {
            return ResponseEntity.badRequest().body("Tên đã tồn tại");
        }

        TheLoai tl = new TheLoai();
        tl.setId(UUID.randomUUID().toString().substring(0, 8));
        tl.setMa(ma);
        tl.setTen(ten);
        tl.setMoTa(req.getMoTa());
        tl.setKieu(req.getKieu());
        tl.setTrangThai(req.getTrangThai());
        tl.setNgayTao(LocalDateTime.now());
        tl.setNgaySua(LocalDateTime.now());

        TheLoai saved = theLoaiRepo.save(tl);
        return ResponseEntity.ok(saved.toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody TheLoai req) {
        Optional<TheLoai> opt = theLoaiRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thể loại để cập nhật!");
        }

        TheLoai tl = opt.get();

        String ma = req.getMa() == null ? "" : req.getMa().trim();
        String ten = req.getTen() == null ? "" : req.getTen().trim();

        if (ma.isBlank()) return ResponseEntity.badRequest().body("Mã không được để trống!");
        if (ten.isBlank()) return ResponseEntity.badRequest().body("Tên không được để trống!");

        if (!ma.matches("^[A-Za-z0-9_-]+$")) {
            return ResponseEntity.badRequest().body("Mã chỉ gồm chữ/số/_/- và không có khoảng trắng");
        }
        if (!ten.matches("^[A-Za-zÀ-ỹ0-9 ]+$")) {
            return ResponseEntity.badRequest().body("Tên không được chứa ký tự đặc biệt");
        }
        if (!tl.getMa().equalsIgnoreCase(ma) && theLoaiRepo.existsByMaIgnoreCase(ma)) {
            return ResponseEntity.badRequest().body("Mã đã tồn tại");
        }
        if (!tl.getTen().equalsIgnoreCase(ten) && theLoaiRepo.existsByTenIgnoreCase(ten)) {
            return ResponseEntity.badRequest().body("Tên đã tồn tại");
        }
        tl.setMa(ma);
        tl.setTen(ten);
        tl.setMoTa(req.getMoTa());
        tl.setKieu(req.getKieu());
        tl.setTrangThai(req.getTrangThai());
        tl.setNgaySua(LocalDateTime.now());

        TheLoai saved = theLoaiRepo.save(tl);
        return ResponseEntity.ok(saved.toResponse());
    }
}
