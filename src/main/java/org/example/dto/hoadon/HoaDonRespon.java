package org.example.dto.hoadon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonRespon {

    private String id;
    private String ma;
    private int trangThai;
    private int loaiHoaDon;
    private int phiVanChuyen;
    private String diaChi;
    private String phuongThucThanhToan;
    private LocalDateTime ngayDatHang;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String tenNV;
    private String tenKH;
    private String sdt;
    private String email;
    private String tenVC;
    private String maVC;
    private int tongTien;
    private int giamGia;
    private int thanhTien;
    private VoucherRespon voucher;
    public HoaDonRespon(String id, Integer tongTien, Integer giamGia, Integer thanhTien) {
        this.id = id;
        this.tongTien = tongTien;
        this.giamGia = giamGia;
        this.thanhTien = thanhTien;
    }

}
