package org.example.repository;

import org.example.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienRepo extends JpaRepository<NhanVien, String> {
}
