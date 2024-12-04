package com.coconut.stock_app.dto.admin;

import com.coconut.stock_app.entity.on_premise.UserAccountStatus;
import com.coconut.stock_app.entity.on_premise.UserRole;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDetailForAdminDTO {
    private String uuid;
    private String username;           // 사용자 이름
    private String email;              // 이메일
    private String phone;              // 전화번호
    private String gender;             // 성별
    private String job;                // 직업
    private String investmentStyle;    // 투자 스타일
    private LocalDate birthdate;          // 생년월일
    private UserAccountStatus status;
    private LocalDateTime createTime;
    private UserRole role;

    private AccountInfoDTO accountInfo;
    private List<UserHistoryDTO> userHistory;
}
