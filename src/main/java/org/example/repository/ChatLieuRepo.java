package org.example.repository;

import org.example.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatLieuRepo extends JpaRepository<ChatLieu, String> {
    List<ChatLieu> findByTenCLContainingIgnoreCase(String tenCL);


    List<ChatLieu> findByTrangThai(Integer trangThai);

    // tìm theo tên + trạng thái
    List<ChatLieu> findByTenCLContainingIgnoreCaseAndTrangThai(String tenCL, Integer trangThai);

    // check trùng mã
    Optional<ChatLieu> findByMaCLIgnoreCase(String maCL);

    // check trùng tên
    Optional<ChatLieu> findByTenCLIgnoreCase(String tenCL);
}

