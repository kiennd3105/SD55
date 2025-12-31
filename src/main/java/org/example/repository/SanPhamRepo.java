package org.example.repository;

import org.example.entity.SanPham;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SanPhamRepo extends JpaRepository<SanPham,String> {
    
    // Lấy sản phẩm mới nhất (theo ngày tạo)
    @Query("SELECT sp FROM SanPham sp WHERE sp.trangThai = 1 ORDER BY sp.ngayTao DESC")
    List<SanPham> findNewProducts(Pageable pageable);

    // Lấy sản phẩm bán chạy (theo ngày tạo, giả định sản phẩm mới hơn bán chạy hơn)
    @Query("SELECT sp FROM SanPham sp WHERE sp.trangThai = 1 ORDER BY sp.ngayTao DESC")
    List<SanPham> findBestSellingProducts(Pageable pageable);
}
