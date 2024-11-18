package com.coconut.stock_app.dto.auth;

import lombok.Data;

@Data
public class LoginDto {
    private String id;       // 사용자 ID
    private String password; // 사용자 Password
}