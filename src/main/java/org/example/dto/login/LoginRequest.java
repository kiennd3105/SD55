package org.example.dto.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String username; // ten NV hoặc ten KH
    private String password;
}
