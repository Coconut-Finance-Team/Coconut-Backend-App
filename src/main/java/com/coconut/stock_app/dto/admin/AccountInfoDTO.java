package com.coconut.stock_app.dto.admin;

import com.coconut.stock_app.entity.on_premise.AccountStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDTO {
    private String accountId;
    private LocalDateTime createTime;
    private String accountName;
    private AccountStatus accountStatus;
}
