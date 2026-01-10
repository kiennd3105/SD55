package org.example.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String role; // ADMIN | USER
    private Object user;

//    public LoginResponse(String role, Object user) {
//        this.role = role;
//        this.user = user;
//    }
}
