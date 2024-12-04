package com.coconut.stock_app.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreationRequest {
    private String username;           // 사용자 이름
    private String phone;              // 전화번호
    private String socialSecurityNumber; // 주민등록번호
    private String accountPurpose;     // 계좌 목적
    private String accountAlias;       // 계좌 별칭
    private String accountPassword;    // 계좌 비밀번호
}
