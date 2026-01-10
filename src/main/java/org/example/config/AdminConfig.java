package org.example.config;

import org.example.entity.Admin;
import org.example.repository.AdminRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminConfig {
    @Bean
    CommandLineRunner createAdmin(AdminRepo adminRepo) {
        return args -> {
            if (adminRepo.findByUsername("admin").isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("123456"));

                adminRepo.save(admin);
                System.out.println("🔥 Đã tạo ADMIN TỔNG: admin / 123456");
            }
        };
    }
}
