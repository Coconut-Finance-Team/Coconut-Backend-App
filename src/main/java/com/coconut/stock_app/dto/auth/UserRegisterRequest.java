package com.coconut.stock_app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    private String id;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String gender;
    private String job;
    private String investmentStyle;
    private String phone;
    private String socialSecurityNumber;
}
