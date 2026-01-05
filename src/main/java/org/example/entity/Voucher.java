package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.hoadon.VoucherRespon;
import org.example.utils.IdGenerator;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "VOUCHER")
@Getter
@Setter
public class Voucher {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, insertable = false )
    private String idVoucher;

    @Column(name = "MA")
    private String ma;

    @Column(name = "TENVC")
    private String ten;

    @Column(name = "LOAIGIAMGIA")
    private Integer loai;

    @Column(name = "DIEUKIENAPDUNG")
    private String dieuKienApDung;

    @Column(name = "GIAMMAX")
    private String giamMax;

    @Column(name = "GIATRIGIAM")
    private String giaTriGiam;

    @Column(name = "SOLUONG")
    private String soLuong;

    @Column(name = "NGAYBATDAU")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime ngayBatDau;

    @Column(name = "NGAYKETTHUC")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime ngayKetThuc;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @Column(name = "MOTA")
    private String moTa;

    @Column(name = "NGAYTAO", updatable = false)
    @CreatedDate
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    @UpdateTimestamp
    private LocalDateTime ngaySua;

    @PrePersist
    public void generateId() {
        if (idVoucher == null) {
            idVoucher = IdGenerator.generate8Hex();
        }
    }

    public VoucherRespon toResponse() {
        return new VoucherRespon(
                this.idVoucher,
                this.ma,
                this.ten,
                this.loai,
                this.dieuKienApDung,
                this.giamMax,
                this.giaTriGiam,
                this.soLuong,
                this.ngayBatDau,
                this.ngayKetThuc,
                this.trangThai
        );
    }

}
