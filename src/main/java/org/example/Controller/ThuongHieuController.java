package org.example.Controller;

import org.example.entity.ThuongHieu;
import org.example.exception.MessageException;
import org.example.service.ThuongHieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@CrossOrigin("*")
@RestController
@RequestMapping("/san-pham/thuong-hieu")
public class ThuongHieuController {

    @Autowired
    private ThuongHieuService service;

    // ========================== BỎ DẤU TIẾNG VIỆT ==========================
    private String boDau(String text) {
        if (text == null) return "";
        String temp = Normalizer.normalize(text, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(temp)
                .replaceAll("")
                .toLowerCase()
                .trim();
    }

    // ========================== GET ALL ==========================
    @GetMapping
    public ResponseEntity<List<ThuongHieu>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ========================== SEARCH ==========================
    @GetMapping("/search")
    public ResponseEntity<List<ThuongHieu>> search(
            @RequestParam(required = false) String ten,
            @RequestParam(required = false) Integer trangThai
    ) {
        String keyword = boDau(ten);

        List<ThuongHieu> result = service.getAll().stream()
                .filter(th ->
                        (ten == null || ten.isBlank() || boDau(th.getTenTH()).contains(keyword))
                                && (trangThai == null || Objects.equals(th.getTrangThai(), trangThai))
                )
                .sorted(java.util.Comparator.comparing(ThuongHieu::getMaTH))
                .toList();

        return ResponseEntity.ok(result);
    }

    // ========================== GET DETAIL THEO ID ==========================
    @GetMapping("/{id}")
    public ResponseEntity<ThuongHieu> getDetail(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (MessageException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========================== ADD ==========================
    @PostMapping
    public ResponseEntity<?> add(@RequestBody ThuongHieu th) {
        try {
            return ResponseEntity.ok(service.add(th));
        } catch (MessageException e) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "message", e.getDefaultMessage()
                    ));
        }
    }

    // ========================== UPDATE THEO ID ==========================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @RequestBody ThuongHieu th
    ) {
        try {
            return ResponseEntity.ok(service.update(id, th));
        } catch (MessageException e) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "message", e.getDefaultMessage()
                    ));
        }
    }

    // ========================== DELETE ==========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok().build();
        } catch (MessageException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getDefaultMessage());
        }
    }
}
