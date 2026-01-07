package org.example.Controller;

import org.example.dto.user.HomePageProductDTO;
import org.example.service.HomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserHomeController {

    @Autowired
    private HomePageService homePageService;

    @GetMapping("/san-pham-ban-chay")
    public ResponseEntity<?> getBestSellingProducts(
            @RequestParam(defaultValue = "8") int limit) {
        List<HomePageProductDTO> products = homePageService.getBestSellingProducts(limit);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/san-pham-moi")
    public ResponseEntity<?> getNewProducts(
            @RequestParam(defaultValue = "8") int limit) {
        List<HomePageProductDTO> products = homePageService.getNewProducts(limit);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/trang-chu")
    public String trangChu() {
        return "redirect:/user/layout-user.html";
    }
}

