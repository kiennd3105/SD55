package org.example.repository;

import org.example.entity.CTGioHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CTGioHangRepo extends JpaRepository<CTGioHang, String> {
    List<CTGioHang> findByGioHang_Id(String idgh);
}
