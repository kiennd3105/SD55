package org.example.repository;

import org.example.entity.HDCT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HDCTRepo extends JpaRepository<HDCT,String> {
    @Query("""
    SELECT h
    FROM HDCT h
    JOIN FETCH h.sanPhamChiTiet ct
    JOIN FETCH ct.sanPham sp
    JOIN FETCH sp.theLoai
    JOIN FETCH sp.chatLieu
    JOIN FETCH sp.thuongHieu
    WHERE h.hoaDon.id = :idHD
    """)
    List<HDCT> findByHoaDonId(@Param("idHD") String idHD);
    Optional<HDCT> findByHoaDonIdAndSanPhamChiTietId(
            String idHoaDon,
            String idSPCT
    );

    @Query("""
    SELECT COALESCE(SUM(
        CAST(h.giaBan AS integer) * CAST(h.soLuong AS integer)
    ), 0)
    FROM HDCT h
    WHERE h.hoaDon.id = :idHoaDon
""")
    Integer tinhTongTien(@Param("idHoaDon") String idHoaDon);


}
