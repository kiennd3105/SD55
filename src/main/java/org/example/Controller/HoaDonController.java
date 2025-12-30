package org.example.Controller;

import org.example.entity.HoaDon;
import org.example.repository.HoaDonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hoa-don")
@CrossOrigin(origins = "*")
public class HoaDonController {

    @Autowired
    private HoaDonRepo hoaDonRepo;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<HoaDon> hoaDonList = hoaDonRepo.findAll();
        return ResponseEntity.ok(
                hoaDonList.stream()
                        .map(HoaDon::toResponse)
                        .toList()
        );
    }

}
