package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.giohang.CTGHRespon;
import org.example.entity.GioHang;
import org.example.repository.CTGioHangRepo;
import org.example.repository.GioHangRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gio-hang")
@RequiredArgsConstructor
public class BanHangOnlineController {
    @Autowired
    GioHangRepo gioHangRepo;
    @Autowired
    CTGioHangRepo ctGioHangRepo;

    @GetMapping("/khach-hang/{idkh}")
    public List<CTGHRespon> hienGioHangTheoIdKH(@PathVariable String idkh) {
        GioHang gioHang = gioHangRepo.findByKhachHang_Id(idkh)
                .orElseThrow(() -> new RuntimeException("Khách hàng chưa có giỏ hàng"));
        return ctGioHangRepo.findByGioHang_Id(gioHang.getId())
                .stream()
                .map(ctgh -> ctgh.toResponse())
                .toList();
    }
}
