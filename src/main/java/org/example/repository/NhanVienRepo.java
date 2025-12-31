package org.example.repository;

import org.example.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface NhanVienRepo extends JpaRepository<NhanVien, String> {

    boolean existsByEmail(String email);

    NhanVien findTopByOrderByMaDesc();
}
