package org.example.repository;

import org.example.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GioHangRepo extends JpaRepository<GioHang, String> {
}
