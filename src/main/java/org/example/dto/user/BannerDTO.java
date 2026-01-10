package org.example.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannerDTO {
    private String id;
    private String hinhAnh;
    private String tieuDe;
    private String moTa;
    private String link;
    private int thuTu;
}

