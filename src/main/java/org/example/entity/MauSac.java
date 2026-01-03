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
import org.example.dto.mausac.MauSacRespon;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Table(name ="MAU")
public class MauSac {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MA")
    private String maM;

    @Column(name = "TENM")
    private String tenM;

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
        return "MauSac{" +
                "id='" + id + '\'' +
                ", maM='" + maM + '\'' +
                ", tenM='" + tenM + '\'' +
                ", trangThai=" + trangThai +
                ", moTa='" + moTa + '\'' +
                ", ngayTao=" + ngayTao +
                ", ngaySua=" + ngaySua +
                '}';
    }

    @PrePersist
    public void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
    public MauSac(String id) {
        this.id = id;
    }
    @PreUpdate
    public void onUpdate() {
        this.ngaySua = LocalDateTime.now();
    }

    public MauSacRespon toResponse() {
        return new MauSacRespon(
                id,
                maM,
                tenM,
                trangThai,
                moTa,
                ngayTao,
                ngaySua
        );
    }
}
