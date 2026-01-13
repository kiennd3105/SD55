package org.example.repository;

import org.example.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HoaDonRepo extends JpaRepository<HoaDon, String> {
    @Query("select max(h.ma) from HoaDon h")
    String findMaxMa();

    List<HoaDon> findByLoaiHoaDonAndTrangThaiOrderByNgayTaoDesc(int loaiHoaDon, int trangThai);


    List<HoaDon> findByKhachHang_IdAndLoaiHoaDonOrderByNgayTaoDesc(String idKH, int loaiHoaDon);

    long countByLoaiHoaDonAndTrangThai(Integer loaiHoaDon, Integer trangThai);

    List<HoaDon> findByLoaiHoaDonAndTrangThaiOrderByNgayTaoDesc(
            Integer loaiHoaDon,
            Integer trangThai
    );

}
