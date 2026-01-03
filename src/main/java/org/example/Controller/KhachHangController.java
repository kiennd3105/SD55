package org.example.Controller;

import org.example.entity.KhachHang;
import org.example.repository.KhachHangRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping("khach-hang")
@RestController

public class KhachHangController {

    @Autowired
    KhachHangRepo khachHangRepo;

    @GetMapping("/hien-thi")
    public List<KhachHang> hienThi() {
        return khachHangRepo.findAll();
    }

}
