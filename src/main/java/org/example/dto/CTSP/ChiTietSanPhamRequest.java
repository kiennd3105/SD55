package org.example.dto.CTSP;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPhamRequest {
    private String sizeId;
    private String mauId;
    private String gia;
    private String soLuong;
    private MultipartFile image;
}
