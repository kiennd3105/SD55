package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
