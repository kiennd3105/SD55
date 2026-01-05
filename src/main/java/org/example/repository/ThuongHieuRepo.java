package org.example.repository;

import org.example.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThuongHieuRepo extends JpaRepository<ThuongHieu, String> {

    // ===============================
    // SEARCH CƠ BẢN (CÓ DẤU – IGNORE CASE)
    // Controller sẽ xử lý không dấu
    // ===============================
    @Query("""
        SELECT th FROM ThuongHieu th
        WHERE (:ten IS NULL 
               OR LOWER(th.tenTH) LIKE LOWER(CONCAT('%', :ten, '%')))
        AND (:trangThai IS NULL OR th.trangThai = :trangThai)
        ORDER BY th.maTH ASC
    """)
    List<ThuongHieu> search(
            @Param("ten") String ten,
            @Param("trangThai") Integer trangThai
    );

    // ===============================
    // CHECK TỒN TẠI
    // ===============================
    boolean existsByTenTHIgnoreCase(String tenTH);

    boolean existsByMaTH(String maTH);

    // ===============================
    // FIND BY (để check trùng khi update)
    // ===============================
    Optional<ThuongHieu> findByTenTHIgnoreCase(String tenTH);

    Optional<ThuongHieu> findByMaTH(String maTH);

    // ===============================
    // LẤY MÃ LỚN NHẤT
    // ===============================
    @Query("SELECT MAX(th.maTH) FROM ThuongHieu th")
    Optional<String> getMaxMaTH();
}
