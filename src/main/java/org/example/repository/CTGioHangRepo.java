package org.example.repository;

import jakarta.transaction.Transactional;
import org.example.entity.CTGioHang;
import org.example.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CTGioHangRepo extends JpaRepository<CTGioHang, String> {
    List<CTGioHang> findByGioHang_Id(String idgh);

    List<CTGioHang> findByGioHang(GioHang gioHang);

    Optional<CTGioHang> findByGioHang_IdAndSanPhamChiTiet_Id(
            String idGH,
            String idCTSP
    );

    @Modifying
    @Transactional
    @Query("delete from CTGioHang c where c.sanPhamChiTiet.id = ?1 and c.gioHang.khachHang.id = ?2")
    void deleteBySpCtAndKh(String idCTGH, String id);
}
