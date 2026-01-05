package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.hoadon.HoaDonRespon;

import java.time.LocalDateTime;
import java.util.UUID;

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
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        }
        this.ngayTao = LocalDateTime.now();
    }

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
    @Column(name = "TONGTIEN")
    private Integer  tongTien;
    @Column(name = "GIAMGIA")
    private Integer  giamGia;
    @Column(name = "THANHTIEN")
    private Integer  thanhTien;
    @Column(name = "NGAYDATHANG")
    private LocalDateTime ngayDatHang;
    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;
    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;
    @ManyToOne
    @JoinColumn(name = "IDNHANVIEN")
    private NhanVien nhanVien;
    @ManyToOne
    @JoinColumn(name = "IDKHACHHANG")
    private KhachHang khachHang;
    @ManyToOne
    @JoinColumn(name = "IDVOUCHER")
    private Voucher voucher;


    public HoaDonRespon toResponse(){
        return new HoaDonRespon(
                this.id,
                this.ma,
                this.trangThai,
                this.loaiHoaDon,
                this.phiVanChuyen,
                this.diaChi,
                this.phuongThucThanhToan,
                this.ngayDatHang,
                this.ngayTao,
                this.ngaySua,
                this.nhanVien != null ? this.nhanVien.getTen() : null,
                this.khachHang != null ? this.khachHang.getTen() : null,
                this.khachHang != null ? this.khachHang.getSdt() : null,
                this.khachHang != null ? this.khachHang.getEmail() : null,
                this.voucher != null ? this.voucher.getTen() : null,
                this.voucher != null ? this.voucher.getMa() : null,
                this.tongTien != null ? this.tongTien : 0,
                this.giamGia != null ? this.giamGia : 0,
                this.thanhTien != null ? this.thanhTien : 0

        );    }
}
