package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "KHACHHANG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHang {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MA")
    private String ma;

    @Column(name = "TEN")
    private String ten;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSW")
    private String passw;

    @Column(name = "GIOITINH")
    private String gioiTinh;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "DIACHI")
    private String diaChi;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;




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
        this.ngaySua = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.ngaySua = LocalDateTime.now();
    }
}
