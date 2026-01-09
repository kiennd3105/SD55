package org.example.repository;

import org.example.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface NhanVienRepo extends JpaRepository<NhanVien, String> {


    Optional<NhanVien> findByEmail(String email);

    boolean existsByEmail(String email);



}
