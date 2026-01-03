package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.thuonghieu.ThuongHieuRespon;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Table(name = "THUONGHIEU")
public class ThuongHieu {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MA")
    private String maTH;

    @Column(name = "TENTH")
    private String tenTH;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @Column(name = "MOTA")
    private String moTa;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @Override
    public String toString() {
        return "ThuongHieu{" +
                "id='" + id + '\'' +
                ", maTH='" + maTH + '\'' +
                ", tenTH='" + tenTH + '\'' +
                ", trangThai=" + trangThai +
                ", moTa='" + moTa + '\'' +
                ", ngayTao=" + ngayTao +
                ", ngaySua=" + ngaySua +
                '}';
    }

    public ThuongHieu(String id) {
        this.id = id;
    }

    @PrePersist
    public void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.ngaySua = LocalDateTime.now();
    }

    public ThuongHieuRespon toResponse() {
        return new ThuongHieuRespon(
                id,
                maTH,
                tenTH,
                trangThai,
                moTa,
                ngayTao,
                ngaySua
        );
    }
}
