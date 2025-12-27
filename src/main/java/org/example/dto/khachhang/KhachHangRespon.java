package org.example.dto.khachhang;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KhachHangRespon {

        private String id;

        private String ma;

        private String ten;

        private String email;

        private String gioiTinh;

        private String sdt;

        private String diaChi;

        private Integer trangThai;

        private LocalDateTime ngayTao;

        private LocalDateTime ngaySua;
    }


