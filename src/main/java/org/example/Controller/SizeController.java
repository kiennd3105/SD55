package org.example.Controller;

import org.example.entity.Size;
import org.example.repository.SizeRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/san-pham/size")
public class SizeController {

    private final SizeRepo sizeRepo;

    public SizeController(SizeRepo sizeRepo) {
        this.sizeRepo = sizeRepo;
    }

    @GetMapping
    public ResponseEntity<List<Size>> getAll(
            @RequestParam(value = "ten", required = false) String ten,
            @RequestParam(value = "trangThai", required = false) Integer trangThai
    ) {
        String keyword = (ten == null) ? null : ten.trim();
        if (keyword != null && !keyword.isEmpty() && trangThai != null) {
            return ResponseEntity.ok(sizeRepo.findByTenSZContainingIgnoreCaseAndTrangThai(keyword, trangThai));
        }
        if (keyword != null && !keyword.isEmpty()) {
            return ResponseEntity.ok(sizeRepo.findByTenSZContainingIgnoreCase(keyword));
        }
        if (trangThai != null) {
            return ResponseEntity.ok(sizeRepo.findByTrangThai(trangThai));
        }
        return ResponseEntity.ok(sizeRepo.findAll());
    }


    @PostMapping
    public ResponseEntity<?> create(@RequestBody Size req) {
        String ma = req.getMaSZ() == null ? "" : req.getMaSZ().trim();
        String ten = req.getTenSZ() == null ? "" : req.getTenSZ().trim();

        if (ma.isEmpty()) {
            return ResponseEntity.badRequest().body("Mã size không được để trống");
        }
        if (ten.isEmpty()) {
            return ResponseEntity.badRequest().body("Tên size không được để trống");
        }

        if (sizeRepo.findByMaSZIgnoreCase(ma).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Mã size đã tồn tại");
        }
        if (sizeRepo.findByTenSZIgnoreCase(ten).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên size đã tồn tại");
        }

        Size s = new Size();
        s.setId(UUID.randomUUID().toString().substring(0, 8));
        s.setMaSZ(ma);
        s.setTenSZ(ten);

        s.setTrangThai(req.getTrangThai() == null ? 1 : req.getTrangThai());
        s.setMoTa(req.getMoTa());

        return ResponseEntity.ok(sizeRepo.save(s));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Size req) {

        Optional<Size> opt = sizeRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String ma = req.getMaSZ() == null ? "" : req.getMaSZ().trim();
        String ten = req.getTenSZ() == null ? "" : req.getTenSZ().trim();

        if (ma.isEmpty()) {
            return ResponseEntity.badRequest().body("Mã size không được để trống");
        }
        if (ten.isEmpty()) {
            return ResponseEntity.badRequest().body("Tên size không được để trống");
        }
        Optional<Size> dupMa = sizeRepo.findByMaSZIgnoreCase(ma);
        if (dupMa.isPresent() && !dupMa.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Mã size đã tồn tại");
        }
        Optional<Size> dupTen = sizeRepo.findByTenSZIgnoreCase(ten);
        if (dupTen.isPresent() && !dupTen.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên size đã tồn tại");
        }
        Size old = opt.get();
        old.setMaSZ(ma);
        old.setTenSZ(ten);
        old.setTrangThai(req.getTrangThai() == null ? old.getTrangThai() : req.getTrangThai());
        old.setMoTa(req.getMoTa());

        return ResponseEntity.ok(sizeRepo.save(old));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (!sizeRepo.existsById(id)) return ResponseEntity.notFound().build();
        sizeRepo.deleteById(id);
        return ResponseEntity.ok("OK");
    }
}
