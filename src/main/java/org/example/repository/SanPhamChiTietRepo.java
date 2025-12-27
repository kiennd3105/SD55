package org.example.repository;

import org.example.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SanPhamChiTietRepo extends JpaRepository<SanPhamChiTiet,String> {

    List<SanPhamChiTiet> findBySanPham_Id(String idSanPham);

}
