package org.example.repository;

import org.example.entity.SanPham;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SanPhamRepo extends JpaRepository<SanPham, String> {
    @Query("""
            SELECT sp FROM SanPham sp
            LEFT JOIN FETCH sp.theLoai
            LEFT JOIN FETCH sp.chatLieu
            LEFT JOIN FETCH sp.thuongHieu """)
    List<SanPham> findAllWithJoin();

    @Query("""
            SELECT sp FROM SanPham sp
            LEFT JOIN FETCH sp.theLoai
            LEFT JOIN FETCH sp.chatLieu
            LEFT JOIN FETCH sp.thuongHieu
            WHERE sp.id = :id """)
    Optional<SanPham> findDetailById(@Param("id") String id);

    @Query("SELECT MAX(sp.ma) FROM SanPham sp")
    String findMaxMa();

    boolean existsByTenIgnoreCase(String ten);

    @Query("SELECT sp FROM SanPham sp WHERE sp.trangThai = 1 ORDER BY sp.ngayTao DESC")
    List<SanPham> findNewProducts(Pageable pageable);

    @Query("SELECT sp FROM SanPham sp WHERE sp.trangThai = 1 ORDER BY sp.ngayTao DESC")
    List<SanPham> findBestSellingProducts(Pageable pageable);


}
