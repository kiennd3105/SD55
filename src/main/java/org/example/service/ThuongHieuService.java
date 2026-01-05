package org.example.service;

import org.example.entity.ThuongHieu;
import org.example.exception.MessageException;
import org.example.repository.ThuongHieuRepo;
import org.example.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThuongHieuService {

    @Autowired
    private ThuongHieuRepo repo;

    // ========================== GET ALL ==========================
    public List<ThuongHieu> getAll() {
        List<ThuongHieu> list = repo.findAll();
        list.sort(java.util.Comparator.comparing(ThuongHieu::getMaTH));
        return list;
    }

    // ========================== GET BY ID ==========================
    public ThuongHieu getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new MessageException("Không tìm thấy thương hiệu"));
    }

    // ========================== ADD ==========================
    @Transactional
    public ThuongHieu add(ThuongHieu th) {
        // Validate
        validate(th, null);

        // Sinh mã tự động
        th.setMaTH(IdGenerator.generate8Hex());

        return repo.save(th);
    }

    // ========================== UPDATE ==========================
    @Transactional
    public ThuongHieu update(String id, ThuongHieu th) {
        ThuongHieu old = repo.findById(id)
                .orElseThrow(() -> new MessageException("Không tìm thấy thương hiệu"));

        // Validate (trừ bản ghi hiện tại)
        validate(th, id);

        old.setTenTH(th.getTenTH().trim());
        old.setTrangThai(th.getTrangThai());
        old.setMoTa(th.getMoTa());

        return repo.save(old);
    }

    // ========================== DELETE ==========================
    @Transactional
    public void delete(String id) {
        if (!repo.existsById(id)) {
            throw new MessageException("Không tìm thấy thương hiệu");
        }
        repo.deleteById(id);
    }

    // ========================== VALIDATE ==========================
    private void validate(ThuongHieu th, String excludeId) {
        // Tên thương hiệu
        if (th.getTenTH() == null || th.getTenTH().trim().isEmpty()) {
            throw new MessageException("Tên thương hiệu không được để trống");
        }

        String ten = th.getTenTH().trim();
        if (ten.length() < 2) {
            throw new MessageException("Tên thương hiệu tối thiểu 2 ký tự");
        }
        if (ten.length() > 50) {
            throw new MessageException("Tên thương hiệu tối đa 50 ký tự");
        }
        if (!ten.matches("^[\\p{L}\\s]+$")) {
            throw new MessageException("Tên thương hiệu không được chứa số hoặc ký tự đặc biệt");
        }

        // Check trùng tên (trừ bản ghi hiện tại nếu là update)
        repo.findByTenTHIgnoreCase(ten)
                .filter(t -> excludeId == null || !t.getId().equals(excludeId))
                .ifPresent(t -> {
                    throw new MessageException("Tên thương hiệu đã tồn tại");
                });

        // Trạng thái
        if (th.getTrangThai() == null) {
            throw new MessageException("Vui lòng chọn trạng thái");
        }

        // Mô tả
        if (th.getMoTa() != null && th.getMoTa().length() > 255) {
            throw new MessageException("Mô tả tối đa 255 ký tự");
        }
    }
}

