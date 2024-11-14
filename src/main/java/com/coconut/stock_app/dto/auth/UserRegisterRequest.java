package com.coconut.stock_app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    private String id; // 사용자 고유 ID
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String gender;
    private String job;
    private String investmentStyle;
    private String birthdate;
    private String phone;
    private String socialSecurityNumber;
}
