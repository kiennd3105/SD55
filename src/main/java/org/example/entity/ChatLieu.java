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
import org.example.dto.chatlieu.ChatLieuRespon;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "CHATLIEU")
public class ChatLieu {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MA")
    private String maCL;

    @Column(name = "TENCL")
    private String tenCL;

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
        return "ChatLieu{" +
                "id='" + id + '\'' +
                ", maCL='" + maCL + '\'' +
                ", tenCL='" + tenCL + '\'' +
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

    @PreUpdate
    public void onUpdate() {
        this.ngaySua = LocalDateTime.now();
    }

    public ChatLieuRespon toResponse() {
        return new ChatLieuRespon(
                id,
                maCL,
                tenCL,
                trangThai,
                moTa,
                ngayTao,
                ngaySua
        );
    }
}
