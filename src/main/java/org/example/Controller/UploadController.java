package org.example.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/upload")

public class UploadController {

    @PostMapping("/nhanvien")
    public String uploadNhanVien(@RequestParam("file") MultipartFile file) throws Exception {

        String dir = "src/main/resources/static/uploads/nhanvien/";
        File folder = new File(dir);
        if (!folder.exists()) folder.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        file.transferTo(new File(dir + fileName));

        return fileName;
    }
}
