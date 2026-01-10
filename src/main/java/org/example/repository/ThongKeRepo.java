package org.example.repository;

import org.example.dto.hoadon.DoanhThuRespon;
import org.example.dto.hoadon.TopSanPhamRespon;
import org.example.entity.HDCT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public interface ThongKeRepo extends JpaRepository<HDCT,String> {

    @Query(
            value = """
    SELECT
        CONVERT(date, h.NGAYTAO) AS ngay,
        COUNT(DISTINCT h.ID) AS tongDon,
        SUM(h.TONGSOLUONG) AS tongSoLuong,
        SUM(h.THANHTIEN) AS doanhThu
    FROM (
        SELECT
            h.ID,
            h.NGAYTAO,
            h.THANHTIEN,
            SUM(CAST(ct.SOLUONG AS int)) AS TONGSOLUONG
        FROM HOADON h
        JOIN HDCT ct ON ct.IDHD = h.ID
        WHERE h.TRANGTHAI = 1
          AND (:fromDate IS NULL OR h.NGAYTAO >= :fromDate)
          AND (:toDate IS NULL OR h.NGAYTAO < DATEADD(day,1,:toDate))
        GROUP BY h.ID, h.NGAYTAO, h.THANHTIEN
    ) h
    GROUP BY CONVERT(date, h.NGAYTAO)
    ORDER BY CONVERT(date, h.NGAYTAO)
    """,
            nativeQuery = true
    )
    List<Object[]> thongKeTheoNgayRaw(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    @Query(
            value = """
            SELECT
                sp.TENSP AS tenSanPham,
                SUM(CAST(ct.SOLUONG AS int)) AS tongSoLuong,
                SUM(CAST(ct.SOLUONG AS int) * CAST(ct.GIABAN AS int)) AS doanhThu
            FROM HDCT ct
            JOIN HOADON h ON ct.IDHD = h.ID
            JOIN CTSP ctsp ON ct.IDCTSP = ctsp.ID
            JOIN SANPHAM sp ON ctsp.IDSANPHAM = sp.ID
            WHERE h.TRANGTHAI = 1
            GROUP BY sp.TENSP
            ORDER BY tongSoLuong DESC
        """,
            nativeQuery = true
    )
    List<Object[]> topSanPhamRaw();

}
