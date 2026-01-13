package org.example.dto.sanpham;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.CTSP.ChiTietSanPhamRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class sanPhamRequest {
    private String ten;
    private Integer trangThai;
    private String moTa;
    private String theLoaiId;
    private String chatLieuId;
    private String thuongHieuId;


    private List<ChiTietSanPhamRequest> ctspList;
}
