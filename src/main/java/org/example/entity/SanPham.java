package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.sanpham.SanPhamRespon;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SANPHAM")
public class SanPham {
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "MA")
    private String ma;
    @Column(name = "TENSP")
    private String ten;
    @Column(name = "TRANGTHAI")
    private int trangThai;
    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;
    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;
    @Column(name = "MOTA")
    private String moTa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IDTHELOAI")
    private TheLoai theLoai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IDCHATLIEU")
    private ChatLieu chatLieu;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IDTHUONGHIEU")
    private ThuongHieu thuongHieu;


    public SanPhamRespon toResponse() {
        return new SanPhamRespon(
                id,
                ma,
                ten,
                trangThai,
                ngayTao,
                ngaySua,
                moTa,
                theLoai != null ? theLoai.getTen() : null,
                chatLieu != null ? chatLieu.getTenCL() : null,
                thuongHieu != null ? thuongHieu.getTenTH() : null
        );
    }

}

