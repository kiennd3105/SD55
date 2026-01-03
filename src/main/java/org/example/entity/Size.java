package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.Size.SizeRespon;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Table(name ="SIZE")
public class Size {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "MA")
    private String maSZ;

    @Column(name = "TENS")
    private String tenSZ;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @Column(name = "MOTA")
    private String moTa;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    public Size(@NotBlank(message = "Chưa chọn size") String sizeId) {
    }

    @Override
    public String toString() {
        return "Size{" +
                "id='" + id + '\'' +
                ", maSZ='" + maSZ + '\'' +
                ", tenSZ='" + tenSZ + '\'' +
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

    public SizeRespon toResponse() {
        return new SizeRespon(
                id,
                maSZ,
                tenSZ,
                trangThai,
                moTa,
                ngayTao,
                ngaySua
        );
    }
}
