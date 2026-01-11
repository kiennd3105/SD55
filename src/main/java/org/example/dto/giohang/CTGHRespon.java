package org.example.dto.giohang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.GioHang;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CTGHRespon {

    private String id;
    private String gia;
    private String soLuong;
    private String ngayTao;
    private String tenSP;
    private String mauSac;
    private String size;
    private String img;
    private GioHang gioHang;
    private String idCTSP;
}
