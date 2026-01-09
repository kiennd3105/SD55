package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.giohang.GioHangRespon;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "GIOHANG")
public class GioHang {
    @Id
    @Column
    private String id;
    @ManyToOne
    @JoinColumn(name = "IDKH")
    private KhachHang khachHang;

    public GioHangRespon toResponse() {
        return new GioHangRespon(
                id,
                khachHang != null ? khachHang.getTen() : null
        );
    }
}
