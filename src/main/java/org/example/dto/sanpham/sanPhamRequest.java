package org.example.dto.sanpham;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.CTSP.ChiTietSanPhamRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class sanPhamRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String ten;
    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;
    private String moTa;
    @NotBlank(message = "Chưa chọn thể loại")
    private String theLoaiId;
    @NotBlank(message = "Chưa chọn chất liệu")
    private String chatLieuId;
    @NotBlank(message = "Chưa chọn thương hiệu")
    private String thuongHieuId;


    private List<ChiTietSanPhamRequest> ctspList;
}
