package org.example.service;

import org.example.dto.Result;
import org.example.entity.Voucher;
import org.example.exception.MessageException;
import org.example.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    public Voucher saveOrUpdate(Voucher voucher) {
        if(voucher.getIdVoucher() == null || voucher.getIdVoucher().equals("")){
            voucher.setIdVoucher(null);
        }
        if(voucher.getNgayBatDau().isAfter(voucher.getNgayKetThuc()) || voucher.getNgayBatDau().isEqual(voucher.getNgayKetThuc())){
            throw new MessageException("Ngày bắt đầu phải sau ngày kết thúc");
        }
        if(voucher.getLoai() == 2){
            voucher.setGiamMax(voucher.getGiaTriGiam());
        }
        if(voucher.getLoai() == 1){
            if(Integer.valueOf(voucher.getGiaTriGiam()) > 100){
                throw new MessageException("Giá trị giảm phải <= 100%");
            }
        }
        if(voucher.getIdVoucher() == null){
            Optional<Voucher> ex = voucherRepository.findByCode(voucher.getMa());
            if(ex.isPresent()){
                throw new MessageException("Mã voucher đã tồn tại, lỗi thêm mới");
            }
        }
        else{
            Optional<Voucher> ex = voucherRepository.findByCode(voucher.getMa(), voucher.getIdVoucher());
            if(ex.isPresent()){
                throw new MessageException("Mã voucher đã tồn tại, lỗi update");
            }
        }
        Voucher result = voucherRepository.save(voucher);
        return result;
    }

    public Result<Void> delete(String id) {
        Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
        if (optionalVoucher.isEmpty()) {
            return new Result<>(false, null, "Không tìm thấy voucher để xóa.");
        }
        voucherRepository.deleteById(id);
        return new Result<>(true, null, "Xóa voucher thành công!");
    }

    public Page<Voucher> findAll(String param, Pageable pageable) {
        Page<Voucher> list = null;
        if(param == null){
            list = voucherRepository.findAll(pageable);
        }
        else{
            list = voucherRepository.search("%"+param+"%",pageable);
        }
        return list;
    }

    public Optional<Voucher> findById(String id) {
        Optional<Voucher> ex = voucherRepository.findById(id);
        if(ex.isEmpty()){
            throw new MessageException("Not found");
        }
        return ex;
    }

}
