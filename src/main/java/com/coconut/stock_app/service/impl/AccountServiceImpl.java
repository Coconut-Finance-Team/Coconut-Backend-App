package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.account.AccountCreationRequest;
import com.coconut.stock_app.dto.account.AccountCreationResponse;
import com.coconut.stock_app.entity.on_premise.Account;
import com.coconut.stock_app.entity.on_premise.AccountStatus;
import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.repository.on_premise.AccountRepository;
import com.coconut.stock_app.service.AccountService;
import com.coconut.stock_app.service.UserService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    private static final int ACCOUNT_NUMBER_LENGTH = 12;

    @Override
    public AccountCreationResponse createAccount(AccountCreationRequest request) {
        // 사용자 검증
        User user = userService.verifyUser(request.getUsername(), request.getPhone(), request.getSocialSecurityNumber());

        // 고유 계좌번호 생성
        String accountId = generateUniqueAccountId();

        // 계좌 생성
        Account account = Account.builder()
                .accountId(accountId)
                .accountUuid(UUID.randomUUID().toString())
                .accountStatus(AccountStatus.OPEN)
                .accountPurpose(request.getAccountPurpose())
                .accountAlias(request.getAccountAlias())
                .accountPassword(request.getAccountPassword())
                .deposit(BigDecimal.ZERO) // 초기 잔액 설정
                .reservedDeposit(BigDecimal.ZERO)
                .user(user)
                .build();

        accountRepository.save(account);

        return AccountCreationResponse.builder()
                .accountId(account.getAccountId())
                .message("계좌가 성공적으로 생성되었습니다.")
                .build();
    }

    /**
     * 고유 계좌번호 생성
     * @return 고유 계좌번호
     */
    private String generateUniqueAccountId() {
        String accountId;
        do {
            accountId = RandomStringUtils.randomNumeric(ACCOUNT_NUMBER_LENGTH);
        } while (accountRepository.existsByAccountId(accountId)); // 중복 검사
        return accountId;
    }
}
