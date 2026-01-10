package org.example.dto.hoadon;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.HoaDon;
import org.example.entity.SanPhamChiTiet;

import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class HoaDonChiTietRespon {

    private String id;
    private String ma;
    private String giaBan;
    private String soLuong;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String idHD;
    private String tenSanPham;
    private String tenSize;
    private String tenMau;
    private String tenTL;
    private String tenCL;
    private String tenTH;
    private String img;

}
