package org.example.repository;

import org.example.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SizeRepo extends JpaRepository<Size, String> {
    List<Size> findByTenSZContainingIgnoreCase(String ten);

    List<Size> findByTrangThai(Integer trangThai);

    List<Size> findByTenSZContainingIgnoreCaseAndTrangThai(String ten, Integer trangThai);

    Optional<Size> findByMaSZIgnoreCase(String ma);

    Optional<Size> findByTenSZIgnoreCase(String ten);
}
