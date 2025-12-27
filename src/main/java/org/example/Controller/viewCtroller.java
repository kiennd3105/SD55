package org.example.Controller;

import org.example.entity.TheLoai;
import org.example.repository.TheLoaiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("san-pham")
public class viewCtroller {

    @GetMapping("/ban-hang")
    public String layout() {
        return "redirect:/ban_tai_quay/layout.html";
    }

    @GetMapping("/san-pham")
    public String sanpham() {
        return "ban_tai_quay/view/sanpham";
    }

    @GetMapping("/trang-chu")
    public String trangchu() {
        return "ban_tai_quay/view/trangchu";
    }
}
