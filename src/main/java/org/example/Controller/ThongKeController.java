package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.hoadon.DoanhThuRespon;
import org.example.dto.hoadon.TopSanPhamRespon;
import org.example.repository.HoaDonRepo;
import org.example.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/thong-ke")
@RequiredArgsConstructor
public class ThongKeController {


    private final ThongKeService thongKeService;

    @GetMapping("/doanh-thu-ngay")
    public List<DoanhThuRespon> doanhThuNgay(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return thongKeService.thongKeTheoNgay(fromDate,toDate);
    }

    @GetMapping("/top-san-pham")
    public List<TopSanPhamRespon> topSanPham() {
        return thongKeService.topSanPham();
    }


}
