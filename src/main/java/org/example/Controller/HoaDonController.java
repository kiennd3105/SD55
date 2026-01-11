package org.example.Controller;

import org.example.entity.HoaDon;
import org.example.repository.HoaDonRepo;
import org.example.repository.KhachHangRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hoa-don")
@CrossOrigin(origins = "*")
public class HoaDonController {

    @Autowired
    private HoaDonRepo hoaDonRepo;
    @Autowired
    KhachHangRepo khachHangRepo;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<HoaDon> hoaDonList = hoaDonRepo.findAll();
        return ResponseEntity.ok(
                hoaDonList.stream()
                        .map(HoaDon::toResponse)
                        .toList()
        );
    }

    @GetMapping("/getByKhachHangOnline/{idKH}")
    public ResponseEntity<?> getByKhachHangOnline(@PathVariable String idKH) {
        List<HoaDon> hoaDonList = hoaDonRepo.findByKhachHang_IdAndLoaiHoaDonOrderByNgayTaoDesc(idKH, 1);
        return ResponseEntity.ok(
                hoaDonList.stream()
                        .map(HoaDon::toResponse)
                        .toList()
        );
    }


}
