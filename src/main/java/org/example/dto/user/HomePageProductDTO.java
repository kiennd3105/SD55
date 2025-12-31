package org.example.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HomePageProductDTO {
    private String id;
    private String ma;
    private String tenSanPham;
    private String gia;
    private String hinhAnh;
    private String idSanPham;
    private String idSize;
    private String idMau;
    private String tenSize;
    private String tenMau;
}

