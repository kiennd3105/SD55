package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.CTSP.SanPhamChiTietRespon;
import org.example.dto.hoadon.HoaDonChiTietRespon;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HDCT")
public class HDCT {
    @Id
    @Column(name = "ID")
    private String id;
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        }
    }
    @Column(name = "MA")
    private String ma;
    @Column(name = "GIABAN")
    private String giaBan;
    @Column(name = "SOLUONG")
    private String soLuong;
    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;
    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @ManyToOne
    @JoinColumn(name = "IDCTSP")
    private SanPhamChiTiet sanPhamChiTiet;
    @ManyToOne
    @JoinColumn(name = "IDHD")
    private HoaDon hoaDon;


    public HoaDonChiTietRespon toResponse() {

        HoaDonChiTietRespon res = new HoaDonChiTietRespon();

        SanPhamChiTiet ctsp = this.sanPhamChiTiet;
        SanPham sp = ctsp != null ? ctsp.getSanPham() : null;

        res.setId(id);
        res.setMa(ma);
        res.setGiaBan(giaBan);
        res.setSoLuong(soLuong);
        res.setNgayTao(ngayTao);
        res.setNgaySua(ngaySua);
        res.setIdHD(hoaDon != null ? hoaDon.getId() : null);

        if (sp != null) {
            res.setTenSanPham(sp.getTen());
            res.setTenTL(sp.getTheLoai() != null ? sp.getTheLoai().getTen() : null);
            res.setTenCL(sp.getChatLieu() != null ? sp.getChatLieu().getTenCL() : null);
            res.setTenTH(sp.getThuongHieu() != null ? sp.getThuongHieu().getTenTH() : null);
        }

        if (ctsp != null) {
            res.setTenSize(ctsp.getSize() != null ? ctsp.getSize().getTenSZ() : null);
            res.setTenMau(ctsp.getMauSac() != null ? ctsp.getMauSac().getTenM() : null);

            // ✅ ẢNH
            res.setImg(
                    ctsp.getIMG() != null
                            ? "http://localhost:8084/uploads/" + ctsp.getIMG()
                            : null
            );
        }

        return res;
    }

}



