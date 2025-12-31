package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.sanpham.SanPhamRespon;
import org.example.dto.theloai.TheLoaiRespon;

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
    @Column(name = "SOLUONG")
    private String soLuong;
    @Column(name = "TRANGTHAI")
    private int trangThai;
    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;
    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;
    @Column(name = "MOTA")
    private String moTa;
    @Column(name = "IDTHELOAI")
    private String idTheLoai;
    @Column(name = "IDCHATLIEU")
    private String idChatLieu;
    @Column(name = "IDTHUONGHIEU")
    private String idThuongHieu;

    public SanPhamRespon toResponse(){
        return new SanPhamRespon(id,ma,ten,soLuong,trangThai,ngayTao,ngaySua,moTa,idTheLoai,idChatLieu,idThuongHieu);
    }
}

