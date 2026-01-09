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
@Table(name = "NHANVIEN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVien {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MA", length = 10, unique = true)
    private String ma;

    @Column(name = "TEN")
    private String ten;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSW")
    private String passw;

    @Column(name = "GIOITINH")
    private String gioiTinh;

    @Column(name = "IMG")
    private String img;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @Column(name = "IDQUYEN", length = 8)
    private String idQuyen;


    @PrePersist
    public void prePersist() {
        System.out.println(">>> PRE_PERSIST ID BEFORE = [" + this.id + "]");
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        }
        System.out.println(">>> PRE_PERSIST ID AFTER = [" + this.id + "]");
        this.ngayTao = LocalDateTime.now();
        this.ngaySua = LocalDateTime.now();
    }


    @PreUpdate
    public void preUpdate() {
        this.ngaySua = LocalDateTime.now();
    }
}

