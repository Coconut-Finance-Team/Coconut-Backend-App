package com.coconut.stock_app.dto.user;

import com.coconut.stock_app.entity.on_premise.UserAccountStatus;
import com.coconut.stock_app.entity.on_premise.UserRole;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoForAdminDto {
    private String uuid;
    private String username;           // 사용자 이름
    private LocalDateTime createTime;
    private UserRole role;
    private String email;              // 이메일
    private String phone;              // 전화번호
    private UserAccountStatus status;

}
