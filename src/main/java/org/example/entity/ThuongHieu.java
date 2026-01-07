package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.thuonghieu.ThuongHieuRespon;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "THUONGHIEU")
public class ThuongHieu {

    // ================= KHÓA CHÍNH =================
    @Id
    @Column(name = "ID", length = 8, updatable = false, nullable = false)
    private String id; // DB tự sinh 8 ký tự, hoặc Java sinh trước khi insert

    // ================= MÃ HIỂN THỊ =================
    @Column(name = "MA", length = 10, unique = true, nullable = false)
    private String maTH;

    @Column(name = "TENTH", nullable = false)
    private String tenTH;

    @Column(name = "TRANGTHAI")
    private Integer trangThai;

    @Column(name = "MOTA")
    private String moTa;

    @Column(name = "NGAYTAO", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    // ================= LIFECYCLE =================
    @PrePersist
    public void onCreate() {
        // Nếu ID chưa có, sinh 8 ký tự từ UUID
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        }

        // Nếu chưa có mã, đặt tạm TH000, mã thực sẽ được service cập nhật
        if (this.maTH == null || this.maTH.isBlank()) {
            this.maTH = "TH000";
        }

        // Thiết lập ngày tạo
        if (this.ngayTao == null) {
            this.ngayTao = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        // Cập nhật ngày sửa
        this.ngaySua = LocalDateTime.now();
    }


    // ================= TO STRING =================
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
