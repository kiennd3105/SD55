package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.theloai.TheLoaiRespon;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "THELOAI")
public class TheLoai {
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "MA")
    private String ma;
    @Column(name = "TENTL")
    private String ten;
    @Column(name = "TRANGTHAI")
    private int trangThai;
    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;
    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;
    @Column(name = "MOTA")
    private String moTa;
    @Column(name = "KIEU")

    private String kieu;

    @Override
    public String toString() {
        return "TheLoai{" +
                "id='" + id + '\'' +
                ", ma='" + ma + '\'' +
                ", ten='" + ten + '\'' +
                ", trangThai=" + trangThai +
                ", ngayTao=" + ngayTao +
                ", ngaySua=" + ngaySua +
                ", moTa='" + moTa + '\'' +
                ", kieu=" + kieu +
                '}';
    }
    public TheLoaiRespon toResponse(){
            return new TheLoaiRespon(id,ma,ten,trangThai,ngayTao,ngaySua,moTa,kieu);
    }
}
