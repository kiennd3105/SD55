package org.example.Controller;

import org.example.dto.theloai.TheLoaiRespon;
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

    // =======================
    // GET ALL
    // =======================
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<TheLoai> list = theLoaiRepo.findAll();
        return ResponseEntity.ok(
                list.stream().map(TheLoai::toResponse).toList()
        );
    }

    // =======================
    // GET DETAIL
    // =======================
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") String id) {
        Optional<TheLoai> opt = theLoaiRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thể loại!");
        }
        return ResponseEntity.ok(opt.get().toResponse());
    }

    // =======================
    // ADD
    // =======================
    @PostMapping
    public ResponseEntity<?> add(@RequestBody TheLoai req) {
        // validate cơ bản
        String ma = req.getMa() == null ? "" : req.getMa().trim();
        String ten = req.getTen() == null ? "" : req.getTen().trim();

        if (ma.isBlank()) return ResponseEntity.badRequest().body("Mã không được để trống!");
        if (ten.isBlank()) return ResponseEntity.badRequest().body("Tên không được để trống!");

        // check format (giống bạn làm ở FE)
        if (!ma.matches("^[A-Za-z0-9_-]+$")) {
            return ResponseEntity.badRequest().body("Mã chỉ gồm chữ/số/_/- và không có khoảng trắng");
        }
        if (!ten.matches("^[A-Za-zÀ-ỹ0-9 ]+$")) {
            return ResponseEntity.badRequest().body("Tên không được chứa ký tự đặc biệt");
        }

        // check trùng
        if (theLoaiRepo.existsByMaIgnoreCase(ma)) {
            return ResponseEntity.badRequest().body("Mã đã tồn tại");
        }
        if (theLoaiRepo.existsByTenIgnoreCase(ten)) {
            return ResponseEntity.badRequest().body("Tên đã tồn tại");
        }

        // set data
        TheLoai tl = new TheLoai();
        tl.setId(UUID.randomUUID().toString());
        tl.setMa(ma);
        tl.setTen(ten);
        tl.setMoTa(req.getMoTa());
        tl.setKieu(req.getKieu());
        tl.setTrangThai(req.getTrangThai()); // 0/1
        tl.setNgayTao(LocalDateTime.now());
        tl.setNgaySua(LocalDateTime.now());

        TheLoai saved = theLoaiRepo.save(tl);
        return ResponseEntity.ok(saved.toResponse());
    }

    // =======================
    // UPDATE
    // =======================
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

        // check trùng nhưng bỏ qua chính nó
        // (vì repo existsBy... không có điều kiện id != ...)
        // -> xử lý bằng cách: nếu đổi ma/ten thì mới check trùng
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
