package org.example.Controller;

import org.example.dto.chatlieu.ChatLieuRequest;
import org.example.dto.chatlieu.ChatLieuRespon;
import org.example.entity.ChatLieu;
import org.example.repository.ChatLieuRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/san-pham/chat-lieu")
public class ChatLieuController {
    private final ChatLieuRepo chatLieuRepo;

    public ChatLieuController(ChatLieuRepo chatLieuRepo) {
        this.chatLieuRepo = chatLieuRepo;
    }

    @GetMapping
    public ResponseEntity<List<ChatLieuRespon>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai
    ) {
        List<ChatLieu> list;
        if ((keyword == null || keyword.isBlank()) && trangThai == null) {
            list = chatLieuRepo.findAll();
        } else if (keyword != null && !keyword.isBlank() && trangThai == null) {
            list = chatLieuRepo.findByTenCLContainingIgnoreCase(keyword);
        } else if ((keyword == null || keyword.isBlank()) && trangThai != null) {
            list = chatLieuRepo.findByTrangThai(trangThai);
        } else {
            list = chatLieuRepo.findByTenCLContainingIgnoreCaseAndTrangThai(keyword, trangThai);
        }
        return ResponseEntity.ok(
                list.stream().map(ChatLieu::toResponse).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatLieuRespon> getDetail(@PathVariable String id) {
        return chatLieuRepo.findById(id)
                .map(cl -> ResponseEntity.ok(cl.toResponse()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ChatLieuRequest req) {
        if (req.getMaCL() == null || req.getMaCL().isBlank()) {
            return ResponseEntity.badRequest().body("Mã chất liệu không được để trống");
        }
        if (req.getTenCL() == null || req.getTenCL().isBlank()) {
            return ResponseEntity.badRequest().body("Tên chất liệu không được để trống");
        }

        if (chatLieuRepo.findByMaCLIgnoreCase(req.getMaCL()).isPresent()) {
            return ResponseEntity.badRequest().body("Mã chất liệu đã tồn tại");
        }
        if (chatLieuRepo.findByTenCLIgnoreCase(req.getTenCL()).isPresent()) {
            return ResponseEntity.badRequest().body("Tên chất liệu đã tồn tại");
        }
        ChatLieu cl = new ChatLieu();
        cl.setId(UUID.randomUUID().toString().substring(0, 8));
        cl.setMaCL(req.getMaCL().trim());
        cl.setTenCL(req.getTenCL().trim());
        cl.setTrangThai(req.getTrangThai());
        cl.setMoTa(req.getMoTa());

        ChatLieu saved = chatLieuRepo.save(cl);
        return ResponseEntity.ok(saved.toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @RequestBody ChatLieuRequest req) {
        return chatLieuRepo.findById(id).map(old -> {
            if (req.getMaCL() == null || req.getMaCL().isBlank()) {
                return ResponseEntity.badRequest().body("Mã chất liệu không được để trống");
            }
            if (req.getTenCL() == null || req.getTenCL().isBlank()) {
                return ResponseEntity.badRequest().body("Tên chất liệu không được để trống");
            }
            chatLieuRepo.findByMaCLIgnoreCase(req.getMaCL())
                    .filter(x -> !x.getId().equals(id))
                    .ifPresent(x -> {
                        throw new RuntimeException("Mã chất liệu đã tồn tại");
                    });
            chatLieuRepo.findByTenCLIgnoreCase(req.getTenCL())
                    .filter(x -> !x.getId().equals(id))
                    .ifPresent(x -> {
                        throw new RuntimeException("Tên chất liệu đã tồn tại");
                    });
            old.setMaCL(req.getMaCL().trim());
            old.setTenCL(req.getTenCL().trim());
            old.setTrangThai(req.getTrangThai());
            old.setMoTa(req.getMoTa());
            return ResponseEntity.ok(chatLieuRepo.save(old).toResponse());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
