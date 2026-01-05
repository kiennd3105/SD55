package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.CTSP.SanPhamChiTietRespon;
import org.example.dto.sanpham.SanPhamRespon;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CTSP")
public class SanPhamChiTiet {
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "MA")
    private String ma;
    @Column(name = "GIA")
    private String gia;
    @Column(name = "SOLUONG")
    private String soLuong;
    @Column(name = "TRANGTHAI")
    private int trangThai;
    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;
    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;
    @Column(name = "IMG")
    private String IMG;
    @Column(name = "MOTA")
    private String moTa;

    @ManyToOne
    @JoinColumn(name = "IDSANPHAM")
    private SanPham sanPham;
    @ManyToOne
    @JoinColumn(name = "IDSIZE")
    private Size size;
    @ManyToOne
    @JoinColumn(name = "IDMAU")
    private MauSac mauSac;

    public SanPhamChiTietRespon toResponse() {
        return new SanPhamChiTietRespon(
                id,
                ma,
                gia,
                soLuong,
                trangThai,
                ngayTao,
                ngaySua,
                IMG == null
                        ? null
                        : "http://localhost:8084/uploads/" + IMG,
                moTa,
                sanPham != null ? sanPham.getId() : null,
                size != null ? size.getTenSZ() : null,
                mauSac != null ? mauSac.getTenM() : null
        );
    }


}
