package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.account.*;
import com.coconut.stock_app.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{uuid}/account/assets")
    ResponseEntity<AssetDTO> getAccountAssets(@PathVariable String uuid) {
        AssetDTO assetDTO = accountService.getAsset(uuid);

        return ResponseEntity.ok(assetDTO);
    }

    @GetMapping("/{uuid}/account/transactions/all")
    ResponseEntity<List<TransactionHistoryDTO>> getTransactionsAll(@PathVariable String uuid) {
        List<TransactionHistoryDTO> transactionHistoryDTOS = accountService.getTransactionsAll(uuid);
        return ResponseEntity.ok(transactionHistoryDTOS);
    }

    @GetMapping("/{uuid}/account/transactions/txn")
    ResponseEntity<AccountTransactionResponseDTO> getTransactionsTxn(@PathVariable String uuid) {
        return null;
    }

    @GetMapping("/{uuid}/account/transactions/trade/detail/{trade_id}")
    ResponseEntity<TransactionDetailDTO> getTransactionsTradeDetail(@PathVariable String uuid, @PathVariable(name = "trade_id") Long tradeId) {
        return null;
    }

    @GetMapping("/{uuid}/account/transactions/deposits-withdrawals")
    ResponseEntity<AccountTransactionResponseDTO> getTransactionsDepositsAndWithdrawals(@PathVariable String uuid) {
        return null;
    }

    @GetMapping("/{uuid}/account/transactions/deposits-withdrawals/detail/{transaction_id}")
    ResponseEntity<TransactionAmountDTO> getTransactionsDepositsAndWithdrawalsDetail(@PathVariable String uuid, @PathVariable(name = "transaction_id") Long transactionId) {
        return null;
    }

    @GetMapping("/{uuid}/account/orders")
    ResponseEntity<AccountTransactionResponseDTO> getAccountOrder(@PathVariable String uuid) {
        return null;
    }

    @GetMapping("/{uuid}/account/orders/detail/{order_id}")
    ResponseEntity<TransactionDetailDTO> getAccountOrderDetail(@PathVariable String uuid, @PathVariable(name = "order_id") Long order_id) {
        return null;
    }

    @GetMapping("/{uuid}/account/sales-profit")
    ResponseEntity<TransactionDetailDTO> getAccountSalesProfit(@PathVariable String uuid) {
        return null;
    }

    @GetMapping("/{uuid}/account/sales-profit/detail/{sales_id}")
    ResponseEntity<TransactionDetailDTO> getAccountSalesProfitDetail(@PathVariable String uuid, @PathVariable(name = "sales_id") Long salesId) {
        return null;
    }

    @GetMapping("/{uuid}/account")
    ResponseEntity<TransactionDetailDTO> getAccount(@PathVariable String uuid) {
        return null;
    }









}
