package org.example.Controller;

import org.example.entity.MauSac;
import org.example.exception.MessageException;
import org.example.service.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/san-pham")
public class MauSacController {

    @Autowired
    private MauSacService service;

    // ========================== GET ALL ==========================
    @GetMapping("/mau-sac")
    public ResponseEntity<List<MauSac>> hienThi() {
        return ResponseEntity.ok(service.getAll());
    }

    // ========================== SEARCH ==========================
    @GetMapping("/mau-sac/search")
    public ResponseEntity<List<MauSac>> timKiem(
            @RequestParam(required = false) String ten,
            @RequestParam(required = false) Integer trangThai
    ) {
        return ResponseEntity.ok(
                service.getAll().stream()
                        .filter(ms ->
                                (ten == null || ten.isBlank() || 
                                 ms.getTenM() != null && ms.getTenM().toLowerCase().contains(ten.toLowerCase()))
                                && (trangThai == null || java.util.Objects.equals(ms.getTrangThai(), trangThai))
                        )
                        .toList()
        );
    }

    // ========================== GET DETAIL THEO ID ==========================
    @GetMapping("/mau-sac/{id}")
    public ResponseEntity<MauSac> getDetail(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (MessageException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========================== ADD ==========================
    @PostMapping("/mau-sac")
    public ResponseEntity<?> add(@RequestBody MauSac ms) {
        try {
            return ResponseEntity.ok(service.add(ms));
        }
        catch (MessageException e) {
            System.err.println(e.getDefaultMessage());
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "message", e.getDefaultMessage()
                    ));
        }
    }

    // ========================== UPDATE THEO ID ==========================
    @PutMapping("/mau-sac/{id}")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @RequestBody MauSac ms
    ) {
        try {
            return ResponseEntity.ok(service.update(id, ms));
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
    @DeleteMapping("/mau-sac/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.ok().build();
        } catch (MessageException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getDefaultMessage());
        }
    }
}
