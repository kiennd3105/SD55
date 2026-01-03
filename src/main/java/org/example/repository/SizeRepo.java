package org.example.repository;

import org.example.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SizeRepo extends JpaRepository<Size, String> {
    Optional<Size> findByMaSZIgnoreCase(String maSZ);
    Optional<Size> findByTenSZIgnoreCase(String tenSZ);

    // search theo tên (contains ignore case)
    List<Size> findByTenSZContainingIgnoreCase(String keyword);

    // filter theo trạng thái
    List<Size> findByTrangThai(Integer trangThai);

    // search theo tên + trạng thái
    List<Size> findByTenSZContainingIgnoreCaseAndTrangThai(String keyword, Integer trangThai);
}
