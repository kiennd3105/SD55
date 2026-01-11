package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.giohang.CTGHRespon;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CTGIOHANG")
public class CTGioHang {
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "GIA")
    private String gia;
    @Column(name = "SOLUONG")
    private String soLuong;
    @Column(name = "NGAYTAO")
    private String ngayTao;
    @ManyToOne
    @JoinColumn(name = "IDCTSP")
    private SanPhamChiTiet sanPhamChiTiet;
    @ManyToOne
    @JoinColumn(name = "IDGH")
    private GioHang gioHang;

    public CTGHRespon toResponse() {
        return new CTGHRespon(
                id,
                gia,
                soLuong,
                ngayTao,
                sanPhamChiTiet.getSanPham().getTen(),
                sanPhamChiTiet.getMauSac().getTenM(),
                sanPhamChiTiet.getSize().getTenSZ(),
                sanPhamChiTiet.getIMG(),
                gioHang,
                sanPhamChiTiet.getId()
        );
    }
}
