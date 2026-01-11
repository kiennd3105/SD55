package org.example.repository;

import org.example.entity.CTGioHang;
import org.example.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CTGioHangRepo extends JpaRepository<CTGioHang, String> {
    List<CTGioHang> findByGioHang_Id(String idgh);

    List<CTGioHang> findByGioHang(GioHang gioHang);

    Optional<CTGioHang> findByGioHang_IdAndSanPhamChiTiet_Id(
            String idGH,
            String idCTSP
    );
}
