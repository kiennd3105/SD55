package org.example.repository;

import org.example.entity.TheLoai;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheLoaiRepo extends JpaRepository<TheLoai, String> {

    boolean existsByMaIgnoreCase(String ma);

    boolean existsByTenIgnoreCase(String ten);
}
