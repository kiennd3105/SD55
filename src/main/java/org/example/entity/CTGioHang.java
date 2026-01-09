package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CTGIOHANG")
public class CTGioHang {
    @Id
    @Column
    private String id;
    private String gia;
    private String soLuong;
    private String ngayTao;
    @ManyToOne
    @JoinColumn(name = "IDCTSP")
    private SanPhamChiTiet sanPhamChiTiet;
    @ManyToOne
    @JoinColumn(name = "IDGH")
    private GioHang gioHang;
}
