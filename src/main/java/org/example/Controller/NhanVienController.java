package org.example.Controller;

import org.example.entity.NhanVien;
import org.example.repository.NhanVienRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("nhan-vien")

public class NhanVienController {

    @Autowired
    NhanVienRepo nhanVienRepo;

    @GetMapping("/hien-thi")
    public List<NhanVien> hienThi() {
        return nhanVienRepo.findAll();
    }
}
