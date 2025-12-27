package org.example.Controller;

import org.example.entity.TheLoai;
import org.example.repository.TheLoaiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("the-loai")
public class TheLoaiController {
    @Autowired
    TheLoaiRepo theLoaiRepo;
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<TheLoai> theLoaiList = theLoaiRepo.findAll();
        return ResponseEntity.ok(
                theLoaiList.stream()
                        .map(TheLoai::toResponse)
                        .toList()
        );
    }


}
