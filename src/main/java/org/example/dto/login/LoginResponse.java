package org.example.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class LoginResponse {
    private String role; // ADMIN | USER
    private String name;
    private String email;
}
