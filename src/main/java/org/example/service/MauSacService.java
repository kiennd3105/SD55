package org.example.service;

import org.example.entity.MauSac;
import org.example.exception.MessageException;
import org.example.repository.MauSacRepo;
import org.example.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MauSacService {

    @Autowired
    private MauSacRepo repo;

    // ========================== GET ALL ==========================
    public List<MauSac> getAll() {
        return repo.findAll();
    }

    // ========================== GET BY ID ==========================
    public MauSac getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new MessageException("Không tìm thấy màu sắc"));
    }

    // ========================== ADD ==========================
    @Transactional
    public MauSac add(MauSac ms) {
        // Validate (maM phải được người dùng cung cấp)
        ms.setMaM(IdGenerator.generate8Hex());
        ms.setId(IdGenerator.generate8Hex());
        validate(ms, null);
        return repo.save(ms);
    }

    // ========================== UPDATE ==========================
    @Transactional
    public MauSac update(String id, MauSac ms) {
        MauSac old = repo.findById(id)
                .orElseThrow(() -> new MessageException("Không tìm thấy màu sắc"));

        // Validate (trừ bản ghi hiện tại)
        validate(ms, id);

        old.setTenM(ms.getTenM().trim());
        old.setMaM(ms.getMaM().trim());
        old.setTrangThai(ms.getTrangThai());
        old.setMoTa(ms.getMoTa());

        return repo.save(old);
    }

    // ========================== DELETE ==========================
    @Transactional
    public void delete(String id) {
        if (!repo.existsById(id)) {
            throw new MessageException("Không tìm thấy màu sắc");
        }
        repo.deleteById(id);
    }

    // ========================== VALIDATE ==========================
    private void validate(MauSac ms, String excludeId) {
        // Mã màu sắc

        String maM = ms.getMaM().trim();

        // Check trùng mã (trừ bản ghi hiện tại nếu là update)
        repo.findByMaMIgnoreCase(maM)
                .filter(m -> excludeId == null || !m.getId().equals(excludeId))
                .ifPresent(m -> {
                    throw new MessageException("Mã màu sắc đã tồn tại");
                });

        // Tên màu sắc
        if (ms.getTenM() == null || ms.getTenM().trim().isEmpty()) {
            throw new MessageException("Tên màu sắc không được để trống");
        }

        String tenM = ms.getTenM().trim();
        if (tenM.length() < 2) {
            throw new MessageException("Tên màu sắc tối thiểu 2 ký tự");
        }
        if (tenM.length() > 50) {
            throw new MessageException("Tên màu sắc tối đa 50 ký tự");
        }

        // Check trùng tên (trừ bản ghi hiện tại nếu là update)
        repo.findByTenMIgnoreCase(tenM)
                .filter(m -> excludeId == null || !m.getId().equals(excludeId))
                .ifPresent(m -> {
                    throw new MessageException("Tên màu sắc đã tồn tại");
                });

        // Trạng thái
        if (ms.getTrangThai() == null) {
            throw new MessageException("Vui lòng chọn trạng thái");
        }

        // Mô tả
        if (ms.getMoTa() != null && ms.getMoTa().length() > 255) {
            throw new MessageException("Mô tả tối đa 255 ký tự");
        }
    }
}

