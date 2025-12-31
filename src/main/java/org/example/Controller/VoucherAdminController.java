package org.example.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dto.Result;
import org.example.entity.Voucher;
import org.example.exception.MessageException;
import org.example.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin/voucher")
public class VoucherAdminController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping("/find-all")
    public ResponseEntity<?> viewPage(@RequestParam(defaultValue = "") String search, Pageable pageable){
        Page<Voucher> page = voucherService.findAll(search, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<?> showCreateForm(@RequestParam String id) {
        Voucher voucher = voucherService.findById(id).orElse(null);
        return new ResponseEntity<>(voucher, HttpStatus.OK);
    }

    @PostMapping("/create-or-update")
    public ResponseEntity<?> saveCategory(@RequestBody Voucher voucher) {
        Voucher result = voucherService.saveOrUpdate(voucher);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String id) {
        Result<Void> result = voucherService.delete(id);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
