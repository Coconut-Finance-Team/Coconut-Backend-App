package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.account.AccountTransactionResponseDTO;
import com.coconut.stock_app.dto.account.AssetDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class AccountController {

    @GetMapping("/{uuid}/account/assets")
    ResponseEntity<AssetDTO> getAccountAssets(@PathVariable String uuid) {
        return null;
    }

    @GetMapping("/{uuid}/account/transactions/all")
    ResponseEntity<AccountTransactionResponseDTO> getTransactionsAll(@PathVariable String uuid) {
        return null;
    }

    @GetMapping("/{uuid}/account/transactions/txn")
    ResponseEntity<AccountTransactionResponseDTO> getTransactionsTxn(@PathVariable String uuid) {
        return null;
    }

    @GetMapping("/{uuid}/account/transactions/deposits-withdrawals")
    ResponseEntity<AccountTransactionResponseDTO> getTransactionsDepositsAndWithdrawals(@PathVariable String uuid) {
        return null;
    }


}
