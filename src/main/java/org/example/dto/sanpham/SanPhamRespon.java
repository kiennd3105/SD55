package org.example.dto.sanpham;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamRespon {

    private String id;
    private String ma;
    private String ten;
    private String soLuong;
    private int trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String moTa;
    private String tenTheLoai;
    private String tenChatLieu;
    private String tenThuongHieu;
}
