package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.account.AccountCreationRequest;
import com.coconut.stock_app.dto.account.AccountCreationResponse;

public interface AccountService {
    AccountCreationResponse createAccount(AccountCreationRequest request);
}
