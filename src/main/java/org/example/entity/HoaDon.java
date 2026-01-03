package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.hoadon.HoaDonRespon;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HOADON")
public class HoaDon {

    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "MA")
    private String ma;
    @Column(name = "TRANGTHAI")
    private int trangThai;
    @Column(name = "LOAIHOADON")
    private int loaiHoaDon;
    @Column(name = "PHIVANCHUYEN")
    private int phiVanChuyen;
    @Column(name = "DIACHI")
    private String diaChi;
    @Column(name = "PHUONGTHUCTHANHTOAN")
    private String phuongThucThanhToan;
    @Column(name = "NGAYDATHANG")
    private LocalDateTime ngayDatHang;
    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;
    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;
    @JoinColumn(name = "IDNHANVIEN")
    private String idNhanVien;
    @Column(name = "IDKHACHHANG")
    private String idKhachHang;
    @Column(name = "IDVOUCHER")
    private String idVoucher;

    public HoaDonRespon toResponse(){
        return new HoaDonRespon(id,ma,trangThai,loaiHoaDon,phiVanChuyen,diaChi,phuongThucThanhToan,ngayDatHang,ngayTao,ngaySua,idNhanVien,idKhachHang,idVoucher);
    }
}
