package org.example.Controller;


import org.example.repository.MauSacRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/san-pham")
public class ControllerMS {
    @Autowired
    MauSacRepo mauSacRepo;

    @GetMapping("/mau-sac")
    public ResponseEntity<?> hienThi() {
        return ResponseEntity.ok(mauSacRepo.findAll());
    }


}
