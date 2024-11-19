package com.coconut.stock_app.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private String username;           // 사용자 이름
    private String email;              // 이메일
    private String phone;              // 전화번호
    private String gender;             // 성별
    private String job;                // 직업
    private String investmentStyle;    // 투자 스타일
    private String birthdate;          // 생년월일
    private String primaryAccountId;   // 기본 계좌 ID
}
