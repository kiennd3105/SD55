package org.example.repository;

import org.example.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {

    @Query("select v from Voucher v where v.ma = ?1")
    Optional<Voucher> findByCode(String ma);

    @Query("select v from Voucher v where v.ma = ?1 and v.idVoucher <> ?2")
    Optional<Voucher> findByCode(String ma, String id);

    @Query("select v from Voucher v where v.ma like ?1 or v.ten like ?1")
    Page<Voucher> search(String s, Pageable pageable);

    @Query("""
                SELECT v FROM Voucher v
                WHERE v.trangThai = 1
                  AND CAST(v.soLuong AS integer) > 0
                  AND v.ngayBatDau <= CURRENT_TIMESTAMP
                  AND v.ngayKetThuc >= CURRENT_TIMESTAMP
                  AND CAST(v.dieuKienApDung AS integer) <= :tongTien
            """)
    List<Voucher> findVoucherApDung(@Param("tongTien") int tongTien);

    List<Voucher> findByTrangThai(Integer trangThai);


}
