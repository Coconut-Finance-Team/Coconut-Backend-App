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
    ResponseEntity<List<TransactionHistoryDTO>> getTransactionsTxn(@PathVariable String uuid) {
        List<TransactionHistoryDTO> transactionHistoryDTOS = accountService.getTransactionsTxn(uuid);
        return ResponseEntity.ok(transactionHistoryDTOS);
    }

    @GetMapping("/{uuid}/account/transactions/deposits-withdrawals")
    ResponseEntity<List<TransactionHistoryDTO>> getTransactionsDepositsAndWithdrawals(@PathVariable String uuid) {
        List<TransactionHistoryDTO> transactionHistoryDTOS = accountService.getTransactionsDepositAndWithdrawals(uuid);
        return ResponseEntity.ok(transactionHistoryDTOS);
    }

    @GetMapping("/{uuid}/account/transactions/trade/detail/{trade_id}")
    ResponseEntity<TradeDetailDTO> getTransactionsTradeDetail(@PathVariable String uuid, @PathVariable(name = "trade_id") Long tradeId) {
        TradeDetailDTO tradeDetailDTO = accountService.getTradeDetail(tradeId);
        return ResponseEntity.ok(tradeDetailDTO);
    }



    @GetMapping("/{uuid}/account/transactions/deposits-withdrawals/detail/{transaction_id}")
    ResponseEntity<TransactionAmountDTO> getTransactionsDepositsAndWithdrawalsDetail(@PathVariable String uuid, @PathVariable(name = "transaction_id") Long transactionId) {
        TransactionAmountDTO transactionAmountDTO = accountService.getTransactionsDepositsAndWithdrawalsDetail(transactionId);
        return ResponseEntity.ok(transactionAmountDTO);
    }

    @GetMapping("/{uuid}/account/orders")
    ResponseEntity<List<OrderHistoryDTO>> getAccountOrder(@PathVariable String uuid) {
        List<OrderHistoryDTO> orderHistoryDTOS = accountService.getAccountOrder(uuid);
        return ResponseEntity.ok(orderHistoryDTOS);
    }

    @GetMapping("/{uuid}/account/orders/detail/{order_id}")
    ResponseEntity<OrderHistoryDTO> getAccountOrderDetail(@PathVariable String uuid, @PathVariable(name = "order_id") Long order_id) {
        OrderHistoryDTO orderHistoryDTO = accountService.getOrderDetail(order_id);
        return ResponseEntity.ok(orderHistoryDTO);
    }

    @GetMapping("/{uuid}/account/sales-profit")
    ResponseEntity<List<ProfitLossDTO>> getAccountSalesProfit(@PathVariable String uuid) {
        List<ProfitLossDTO> profitLossDTOS = accountService.getAccountSalesProfit(uuid);
        return ResponseEntity.ok(profitLossDTOS);
    }

    @GetMapping("/{uuid}/account/sales-profit/detail/{sales_id}")
    ResponseEntity<TradeDetailDTO> getAccountSalesProfitDetail(@PathVariable String uuid, @PathVariable(name = "sales_id") Long salesId) {
        return null;
    }

    @GetMapping("/{uuid}/account")
    ResponseEntity<TradeDetailDTO> getAccount(@PathVariable String uuid) {
        return null;
    }









}
