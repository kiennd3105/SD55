package org.example.repository;

import org.example.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SanPhamChiTietRepo extends JpaRepository<SanPhamChiTiet,String> {

    List<SanPhamChiTiet> findBySanPham_Id(String idSanPham);
    @Query("""
        SELECT ct FROM SanPhamChiTiet ct
        LEFT JOIN FETCH ct.size
        LEFT JOIN FETCH ct.mauSac
        LEFT JOIN FETCH ct.sanPham
        WHERE ct.sanPham.id = :idSanPham """)
    List<SanPhamChiTiet> findBySanPhamId(@Param("idSanPham") String idSanPham);

}
