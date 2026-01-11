package org.example.repository;

import org.example.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GioHangRepo extends JpaRepository<GioHang, String> {
    Optional<GioHang> findByKhachHang_Id(String idkh);
    
}
