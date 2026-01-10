package org.example.repository;

import org.example.entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MauSacRepo extends JpaRepository<MauSac, String> {

    @Query("""
        SELECT m FROM MauSac m
        WHERE (:ten IS NULL OR LOWER(m.tenM) LIKE LOWER(CONCAT('%', :ten, '%')))
        AND (:trangThai IS NULL OR m.trangThai = :trangThai)
    """)
    List<MauSac> search(
            @Param("ten") String ten,
            @Param("trangThai") Integer trangThai
    );

    // Check tồn tại
    boolean existsByTenMIgnoreCase(String tenM);
    
    boolean existsByMaM(String maM);

    // Find by (để check trùng khi update)
    Optional<MauSac> findByTenMIgnoreCase(String tenM);
    
    Optional<MauSac> findByMaMIgnoreCase(String maM);

    // Lấy mã lớn nhất
    @Query("SELECT MAX(m.maM) FROM MauSac m")
    Optional<String> getMaxMaM();
}
