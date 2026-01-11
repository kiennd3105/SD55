package org.example.dto.hoadon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThanhToanRequest {
    private String idKH;
    private String diaChi;
    private int phiVanChuyen;
    private int tongTien;
    private int giamGia;
    private int thanhTien;
    private String idVoucher;
    @JsonProperty("id")
    private String idCTSP;
    private List<ChiTietItem> items;
}
