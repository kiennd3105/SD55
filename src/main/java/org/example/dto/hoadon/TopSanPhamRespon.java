package org.example.dto.hoadon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopSanPhamRespon {
    private String tenSanPham;
    private Long tongSoLuong;
    private Long doanhThu;
}
