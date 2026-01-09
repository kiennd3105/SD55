package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.hoadon.DoanhThuRespon;
import org.example.dto.hoadon.TopSanPhamRespon;
import org.example.repository.ThongKeRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThongKeService {
    private final ThongKeRepo thongKeRepo;

    public List<DoanhThuRespon> thongKeTheoNgay(LocalDate fromDate, LocalDate toDate) {

        if (fromDate == null || toDate == null) {
            toDate = LocalDate.now();
            fromDate = toDate.minusDays(4);
        }

        LocalDate toDateFix = toDate.plusDays(1);

        return thongKeRepo.thongKeTheoNgayRaw(fromDate, toDateFix)
                .stream()
                .map(r -> new DoanhThuRespon(
                        r[0].toString(),
                        ((Number) r[1]).longValue(),
                        ((Number) r[2]).longValue(),
                        ((Number) r[3]).longValue()
                ))
                .toList();
    }

    public List<TopSanPhamRespon> topSanPham() {
        return thongKeRepo.topSanPhamRaw()
                .stream()
                .map(o -> new TopSanPhamRespon(
                        o[0].toString(),
                        ((Number) o[1]).longValue(),
                        ((Number) o[2]).longValue()
                ))
                .toList();
    }
}
