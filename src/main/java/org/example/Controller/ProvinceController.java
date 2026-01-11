package org.example.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/provinces")
public class ProvinceController {
    private final RestTemplate rest = new RestTemplate();

    @GetMapping("/p") // Lấy danh sách tỉnh
    public ResponseEntity<?> getProvinces() {
        RestTemplate rest = new RestTemplate();
        String url = "https://provinces.open-api.vn/api/?depth=1"; // sửa link mới
        ResponseEntity<String> resp = rest.getForEntity(url, String.class);
        return ResponseEntity.ok(resp.getBody());
    }

    @GetMapping("/p/{code}")
    public ResponseEntity<?> getProvinceByCode(@PathVariable String code, @RequestParam(defaultValue = "1") int depth) {
        String url = "https://provinces.open-api.vn/api/p/" + code + "?depth=" + depth;
        ResponseEntity<String> resp = rest.getForEntity(url, String.class);
        return ResponseEntity.ok(resp.getBody());
    }

    @GetMapping("/d/{code}")
    public ResponseEntity<?> getDistrictByCode(@PathVariable String code, @RequestParam(defaultValue = "1") int depth) {
        String url = "https://provinces.open-api.vn/api/d/" + code + "?depth=" + depth;
        ResponseEntity<String> resp = rest.getForEntity(url, String.class);
        return ResponseEntity.ok(resp.getBody());
    }
}
