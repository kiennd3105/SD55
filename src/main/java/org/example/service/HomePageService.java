package org.example.service;

import org.example.dto.user.HomePageProductDTO;
import org.example.entity.SanPham;
import org.example.entity.SanPhamChiTiet;
import org.example.repository.MauSacRepo;
import org.example.repository.SanPhamChiTietRepo;
import org.example.repository.SanPhamRepo;
import org.example.repository.SizeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomePageService {

    @Autowired
    private SanPhamRepo sanPhamRepo;

    @Autowired
    private SanPhamChiTietRepo sanPhamChiTietRepo;

    @Autowired
    private MauSacRepo mauSacRepo;

    @Autowired
    private SizeRepo sizeRepo;

    public List<HomePageProductDTO> getBestSellingProducts(int limit) {
        List<SanPham> products = sanPhamRepo.findBestSellingProducts(PageRequest.of(0, limit));
        return products.stream()
                .map(this::convertSanPhamToDTOWithCheapest)
                .filter(dto -> dto != null) // Lọc bỏ những sản phẩm không có CTSP
                .collect(Collectors.toList());
    }

    public List<HomePageProductDTO> getNewProducts(int limit) {
        List<SanPham> products = sanPhamRepo.findNewProducts(PageRequest.of(0, limit));
        return products.stream()
                .map(this::convertSanPhamToDTOWithFirst)
                .filter(dto -> dto != null) // Lọc bỏ những sản phẩm không có CTSP
                .collect(Collectors.toList());
    }

    // Convert SanPham sang DTO với CTSP có giá rẻ nhất (cho sản phẩm bán chạy)
    private HomePageProductDTO convertSanPhamToDTOWithCheapest(SanPham sanPham) {
        // Tìm CTSP có giá rẻ nhất của sản phẩm này
        SanPhamChiTiet cheapestCTSP = sanPhamChiTietRepo.findCheapestBySanPhamId(sanPham.getId());

        if (cheapestCTSP == null) {
            return null; // Nếu không có CTSP nào, bỏ qua sản phẩm này
        }

        return buildDTO(sanPham, cheapestCTSP);
    }

    // Convert SanPham sang DTO với CTSP đầu tiên (cho sản phẩm mới)
    private HomePageProductDTO convertSanPhamToDTOWithFirst(SanPham sanPham) {
        // Tìm CTSP đầu tiên của sản phẩm này
        SanPhamChiTiet firstCTSP = sanPhamChiTietRepo.findFirstBySanPhamId(sanPham.getId());

        if (firstCTSP == null) {
            return null; // Nếu không có CTSP nào, bỏ qua sản phẩm này
        }

        return buildDTO(sanPham, firstCTSP);
    }

    // Build DTO từ SanPham và SanPhamChiTiet
    private HomePageProductDTO buildDTO(SanPham sanPham, SanPhamChiTiet ctsp) {
        HomePageProductDTO dto = new HomePageProductDTO();
        dto.setId(ctsp.getId());
        dto.setMa(ctsp.getMa());
        dto.setGia(ctsp.getGia());
        dto.setHinhAnh(ctsp.getIMG());
        dto.setIdSanPham(sanPham.getId());
        dto.setTenSanPham(sanPham.getTen()); // Tên sản phẩm từ SanPham.ten
        dto.setIdSize(ctsp.getSize().toString());
        dto.setIdMau(ctsp.getMauSac().toString());

        // Lấy tên size
        if (ctsp.getSize() != null) {
            sizeRepo.findById(ctsp.getSize().toString()).ifPresent(size -> {
                dto.setTenSize(size.getTenSZ());
            });
        }

        // Lấy tên màu
        if (ctsp.getMauSac() != null) {
            mauSacRepo.findById(ctsp.getMauSac().toString()).ifPresent(mau -> {
                dto.setTenMau(mau.getTenM());
            });
        }

        return dto;
    }
}

