package org.example.dto.chatlieu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ChatLieuRespon {
    private String id;
    private String maCL;
    private String tenCL;
    private Integer trangThai;
    private String moTa;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
}
