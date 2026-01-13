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
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("san-pham")
public class SanPhamController {
    @Autowired
    SanPhamRepo sanPhamRepo;
    @Autowired
    SanPhamChiTietRepo sanPhamChiTietRepo;
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

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable String id) {
        return sanPhamRepo.findById(id)
                .map(sp -> {
                    HashMap<String, Object> res = new HashMap<>();
                    res.put("id", sp.getId());
                    res.put("ma", sp.getMa());
                    res.put("ten", sp.getTen());
                    res.put("moTa", sp.getMoTa());
                    res.put("trangThai", sp.getTrangThai());
                    res.put("ngayTao", sp.getNgayTao());
                    res.put("ngaySua", sp.getNgaySua());
                    if (sp.getTheLoai() != null) {
                        res.put("theLoaiId", sp.getTheLoai().getId());
                        res.put("tenTheLoai", sp.getTheLoai().getTen());
                    }
                    if (sp.getChatLieu() != null) {
                        res.put("chatLieuId", sp.getChatLieu().getId());
                        res.put("tenChatLieu", sp.getChatLieu().getTenCL());
                    }
                    if (sp.getThuongHieu() != null) {
                        res.put("thuongHieuId", sp.getThuongHieu().getId());
                        res.put("tenThuongHieu", sp.getThuongHieu().getTenTH());
                    }
                    return ResponseEntity.ok(res);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/update/{id}")
    @Transactional
    public ResponseEntity<?> updateSanPham(
            @PathVariable String id,
            @RequestBody sanPhamRequest req
    ) {
        SanPham sp = sanPhamRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        boolean isTrangThaiChanged = !Objects.equals(sp.getTrangThai(), req.getTrangThai());

        // Cập nhật thông tin cơ bản
        sp.setTen(req.getTen());
        sp.setTrangThai(req.getTrangThai());
        sp.setMoTa(req.getMoTa());
        sp.setTheLoai(theLoaiRepo.getReferenceById(req.getTheLoaiId()));
        sp.setChatLieu(chatLieuRepo.getReferenceById(req.getChatLieuId()));
        sp.setThuongHieu(thuongHieuRepo.getReferenceById(req.getThuongHieuId()));
        sp.setNgaySua(LocalDateTime.now());

        sanPhamRepo.save(sp);

        // Nếu trạng thái thay đổi, cập nhật luôn trạng thái cho tất cả CTSP liên quan
        if (isTrangThaiChanged) {
            sanPhamChiTietRepo.updateTrangThaiBySanPhamId(sp.getId(), sp.getTrangThai());
        }

        return ResponseEntity.ok(
                new HashMap<String, Object>() {{
                    put("success", true);
                    put("message", "Cập nhật sản phẩm thành công");
                }}
        );
    }

    @PostMapping(value = "/ctsp/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> addChiTietSanPham(
            @ModelAttribute ChiTietSanPhamRequest req
    ) {
        SanPham sp = sanPhamRepo.findById(req.getSanPhamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        boolean exists = sanPhamChiTietRepo
                .existsBySanPham_IdAndSize_IdAndMauSac_Id(
                        req.getSanPhamId(),
                        req.getSizeId(),
                        req.getMauId()
                );
        if (exists) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "ERROR",
                            "message", "Sản phẩm đã tồn tại size và màu này"
                    )
            );
        }
        if (req.getGia() == null || req.getGia().isBlank()
                || !req.getGia().matches("\\d+")) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Giá không hợp lệ")
            );
        }
        if (req.getSoLuong() == null || req.getSoLuong().isBlank()
                || !req.getSoLuong().matches("\\d+")) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Số lượng không hợp lệ")
            );
        }
        SanPhamChiTiet ct = new SanPhamChiTiet();
        ct.setId(UUID.randomUUID().toString().substring(0, 8));
        ct.setMa("CT" + System.currentTimeMillis() % 100000000);
        ct.setSanPham(sp);

        ct.setGia(req.getGia());
        ct.setSoLuong(req.getSoLuong());
        ct.setTrangThai(1);
        ct.setNgayTao(LocalDateTime.now());
        ct.setNgaySua(LocalDateTime.now());

        ct.setSize(sizeRepo.getReferenceById(req.getSizeId()));
        ct.setMauSac(mauSacRepo.getReferenceById(req.getMauId()));

        // 5. Upload ảnh
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_"
                        + req.getImage().getOriginalFilename();

                Path uploadDir = Paths.get("uploads");
                Files.createDirectories(uploadDir);

                Files.copy(
                        req.getImage().getInputStream(),
                        uploadDir.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );

                ct.setIMG(fileName);

            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(
                        Map.of("message", "Lỗi upload ảnh")
                );
            }
        }

        sanPhamChiTietRepo.save(ct);

        return ResponseEntity.ok(
                Map.of(
                        "status", "SUCCESS",
                        "message", "Thêm chi tiết sản phẩm thành công"
                )
        );
    }

    @PutMapping(value = "/ctsp/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> updateChiTietSanPham(
            @PathVariable String id,
            @ModelAttribute ChiTietSanPhamRequest req
    ) {

        SanPhamChiTiet ct = sanPhamChiTietRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CTSP"));
        boolean exists = sanPhamChiTietRepo
                .existsBySanPham_IdAndSize_IdAndMauSac_IdAndIdNot(
                        ct.getSanPham().getId(),
                        req.getSizeId(),
                        req.getMauId(),
                        id
                );
        if (exists) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "ERROR",
                            "message", "CTSP đã tồn tại size và màu này"
                    )
            );
        }
        if (req.getGia() == null || req.getGia().isBlank()
                || !req.getGia().matches("\\d+")) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Giá không hợp lệ")
            );
        }
        if (req.getSoLuong() == null || req.getSoLuong().isBlank()
                || !req.getSoLuong().matches("\\d+")) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Số lượng không hợp lệ")
            );
        }
        ct.setGia(req.getGia());
        ct.setSoLuong(req.getSoLuong());
        ct.setSize(sizeRepo.getReferenceById(req.getSizeId()));
        ct.setMauSac(mauSacRepo.getReferenceById(req.getMauId()));
        ct.setNgaySua(LocalDateTime.now());

        if (req.getImage() != null && !req.getImage().isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_"
                        + req.getImage().getOriginalFilename();

                Path uploadDir = Paths.get("uploads");
                Files.createDirectories(uploadDir);

                Files.copy(
                        req.getImage().getInputStream(),
                        uploadDir.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );
                ct.setIMG(fileName);

            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(
                        Map.of("message", "Lỗi upload ảnh")
                );
            }
        }
        sanPhamChiTietRepo.save(ct);
        return ResponseEntity.ok(
                Map.of(
                        "status", "SUCCESS",
                        "message", "Cập nhật chi tiết sản phẩm thành công"
                )
        );
    }

    @GetMapping("/detailsp/{id}")
    public ResponseEntity<?> detailsp(@PathVariable String id) {

        SanPhamChiTiet ct = sanPhamChiTietRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CTSP"));

        Map<String, Object> res = new HashMap<>();
        res.put("id", ct.getId());
        res.put("sanPhamId", ct.getSanPham().getId());

        res.put("sizeId", ct.getSize().getId());
        res.put("tenSize", ct.getSize().getTenSZ());

        res.put("mauId", ct.getMauSac().getId());
        res.put("tenMau", ct.getMauSac().getTenM());

        res.put("gia", ct.getGia());
        res.put("soLuong", ct.getSoLuong());
        res.put("img", ct.getIMG());


        return ResponseEntity.ok(res);
    }


}
