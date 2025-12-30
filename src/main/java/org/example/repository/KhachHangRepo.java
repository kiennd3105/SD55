package org.example.repository;

import org.example.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KhachHangRepo extends JpaRepository<KhachHang, String> {
    Optional<KhachHang> findDetailById(String id);

}
