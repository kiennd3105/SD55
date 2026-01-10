package org.example.dto.CTSP;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamChiTietRespon {

    private String id;
    private String ma;
    private String gia;
    private String soLuong;
    private int trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String IMG;
    private String moTa;
    private String idSanPham;
    private String tenSP;
    private String tenSize;
    private String tenMau;

}
