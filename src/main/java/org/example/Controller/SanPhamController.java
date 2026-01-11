package org.example.Controller;

import jakarta.transaction.Transactional;
import org.example.dto.CTSP.ChiTietSanPhamRequest;
import org.example.dto.CTSP.SanPhamChiTietRespon;
import org.example.dto.sanpham.sanPhamRequest;
import org.example.entity.SanPham;
import org.example.entity.SanPhamChiTiet;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("san-pham")
public class SanPhamController {
    @Autowired
    SanPhamRepo sanPhamRepo;
    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;
    @Autowired
    private TheLoaiRepo theLoaiRepo;

    @Autowired
    private ChatLieuRepo chatLieuRepo;
    @Autowired
    private SizeRepo sizeRepo;
    @Autowired
    private MauSacRepo mauSacRepo;

    @Autowired
    private ThuongHieuRepo thuongHieuRepo;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<SanPham> sanPhamList = sanPhamRepo.findAllWithJoin();
        return ResponseEntity.ok(
                sanPhamList.stream()
                        .map(SanPham::toResponse)
                        .toList()
        );
    }

    @GetMapping("/check-ten")
    public ResponseEntity<Boolean> checkTen(@RequestParam String ten) {
        boolean exists = sanPhamRepo.existsByTenIgnoreCase(ten.trim());
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        return sanPhamRepo.findDetailById(id)
                .map(sp -> ResponseEntity.ok(sp.toResponse()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/san-pham/{idSanPham}")
    public ResponseEntity<?> getBySanPham(@PathVariable String idSanPham) {

        List<SanPhamChiTietRespon> list = sanPhamChiTietRepo
                .findBySanPhamId(idSanPham)
                .stream()
                .map(SanPhamChiTiet::toResponse)
                .toList();

        return ResponseEntity.ok(list);
    }
    

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> addSanPham(@ModelAttribute sanPhamRequest req) throws IOException {
        SanPham sp = new SanPham();
        sp.setId(UUID.randomUUID().toString().substring(0, 8));
        sp.setMa("SP" + System.currentTimeMillis() % 100000000);
        sp.setTen(req.getTen());
        sp.setTrangThai(req.getTrangThai());
        sp.setMoTa(req.getMoTa());
        sp.setTheLoai(theLoaiRepo.getReferenceById(req.getTheLoaiId()));
        sp.setChatLieu(chatLieuRepo.getReferenceById(req.getChatLieuId()));
        sp.setThuongHieu(thuongHieuRepo.getReferenceById(req.getThuongHieuId()));
        sp.setNgayTao(LocalDateTime.now());
        sp.setNgaySua(LocalDateTime.now());
        sanPhamRepo.save(sp);
        for (ChiTietSanPhamRequest ctReq : req.getCtspList()) {
            SanPhamChiTiet ct = new SanPhamChiTiet();
            ct.setId(UUID.randomUUID().toString().substring(0, 8));
            ct.setSanPham(sp);
            ct.setMa("CT" + System.currentTimeMillis() % 100000000);
            ct.setGia(ctReq.getGia());
            ct.setSoLuong(ctReq.getSoLuong());
            ct.setTrangThai(1);
            ct.setNgayTao(LocalDateTime.now());
            ct.setNgaySua(LocalDateTime.now());
            ct.setSize(sizeRepo.getReferenceById(ctReq.getSizeId()));
            ct.setMauSac(mauSacRepo.getReferenceById(ctReq.getMauId()));
            if (ctReq.getImage() != null && !ctReq.getImage().isEmpty()) {

                String fileName = System.currentTimeMillis() + "_" +
                        ctReq.getImage().getOriginalFilename();

                Path uploadDir = Paths.get("uploads");
                Files.createDirectories(uploadDir);

                Files.copy(
                        ctReq.getImage().getInputStream(),
                        uploadDir.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );

                ct.setIMG(fileName);
            }

            sanPhamChiTietRepo.save(ct);
        }

        return ResponseEntity.ok(
                new HashMap<String, Object>() {{
                    put("message", "Thêm sản phẩm thành công");
                    put("status", "SUCCESS");
                }}
        );


    }

}
