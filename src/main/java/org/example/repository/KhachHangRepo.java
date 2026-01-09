package org.example.repository;

import org.example.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KhachHangRepo extends JpaRepository<KhachHang, String> {
    Optional<KhachHang> findDetailById(String id);

    Optional<KhachHang> findByEmail(String email); // ✅ DÙNG CÁI NÀY

    boolean existsByEmail(String email);
    boolean existsByTenIgnoreCaseAndIdNot(String ten, String id);
    boolean existsByTenIgnoreCase(String ten);

    List<KhachHang> findByTrangThai(Integer trangThai);
    Optional<KhachHang> findByTen(String ten);


}
