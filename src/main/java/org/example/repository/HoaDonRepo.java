package org.example.repository;

import org.example.dto.hoadon.DoanhThuRespon;
import org.example.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HoaDonRepo extends JpaRepository<HoaDon, String> {
    @Query("select max(h.ma) from HoaDon h")
    String findMaxMa();
    List<HoaDon> findByLoaiHoaDonAndTrangThai(int loaiHoaDon, int trangThai);



}
