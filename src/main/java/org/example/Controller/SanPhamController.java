package org.example.Controller;

import org.example.dto.CTSP.SanPhamChiTietRespon;
import org.example.entity.SanPham;
import org.example.entity.TheLoai;
import org.example.repository.SanPhamChiTietRepo;
import org.example.repository.SanPhamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("san-pham")
public class SanPhamController {
    @Autowired
    SanPhamRepo sanPhamRepo;
    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<SanPham> sanPhamList = sanPhamRepo.findAll();
        return ResponseEntity.ok(
                sanPhamList.stream()
                        .map(SanPham::toResponse)
                        .toList()
        );
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        return sanPhamRepo.findById(id)
                .map(sp -> ResponseEntity.ok(sp.toResponse()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/san-pham/{idSanPham}")
    public ResponseEntity<?> getBySanPham(@PathVariable String idSanPham) {

        List<SanPhamChiTietRespon> list = sanPhamChiTietRepo
                .findBySanPham_Id(idSanPham)
                .stream()
                .map(ct -> new SanPhamChiTietRespon(
                        ct.getId(),
                        ct.getMa(),
                        ct.getGia(),
                        ct.getSoLuong(),
                        ct.getTrangThai(),
                        ct.getNgayTao(),
                        ct.getNgaySua(),
                        ct.getIMG(),
                        ct.getMoTa(),
                        ct.getSanPham() != null ? ct.getSanPham().getId() : null,
                        ct.getIdSize(),
                        ct.getIdMau()
                ))
                .toList();

        return ResponseEntity.ok(list);
    }

}
