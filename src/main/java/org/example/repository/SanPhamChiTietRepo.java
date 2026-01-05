package org.example.repository;

import org.example.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
<<<<<<< HEAD
import org.springframework.data.repository.query.Param;
=======
>>>>>>> 060b417fed95ebe3b4e6be6ef620af62cb4173a6

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

    // Tìm CTSP có giá rẻ nhất của một sản phẩm
    @Query(value = "SELECT TOP 1 * FROM CTSP WHERE IDSANPHAM = ?1 AND TRANGTHAI = 1 ORDER BY CAST(GIA AS FLOAT) ASC", nativeQuery = true)
    SanPhamChiTiet findCheapestBySanPhamId(String idSanPham);

    // Tìm CTSP đầu tiên của một sản phẩm (theo ngày tạo hoặc ID)
    @Query(value = "SELECT TOP 1 * FROM CTSP WHERE IDSANPHAM = ?1 AND TRANGTHAI = 1 ORDER BY NGAYTAO ASC, ID ASC", nativeQuery = true)
    SanPhamChiTiet findFirstBySanPhamId(String idSanPham);

}
