package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.account.AccountCreationRequest;
import com.coconut.stock_app.dto.account.AccountCreationResponse;
import com.coconut.stock_app.service.AccountService;
import com.coconut.stock_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.coconut.stock_app.service.AuthenticationService;
import com.coconut.stock_app.dto.account.*;
import com.coconut.stock_app.entity.on_premise.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class AccountController {

    private final AccountService accountService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/account/create")
    public ResponseEntity<AccountCreationResponse> createAccount(@RequestBody AccountCreationRequest request) {
        AccountCreationResponse response = accountService.createAccount(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/assets")
    ResponseEntity<AssetDTO> getAccountAssets() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        String uuid = authenticatedUser.getPrimaryAccount().getAccountUuid();
        AssetDTO assetDTO = accountService.getAsset(uuid);

        return ResponseEntity.ok(assetDTO);
    }

    @GetMapping("/account/transactions/all")
    ResponseEntity<List<TransactionHistoryDTO>> getTransactionsAll() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        String uuid = authenticatedUser.getPrimaryAccount().getAccountUuid();
        List<TransactionHistoryDTO> transactionHistoryDTOS = accountService.getTransactionsAll(uuid);
        return ResponseEntity.ok(transactionHistoryDTOS);
    }

    @GetMapping("/account/transactions/txn")
    ResponseEntity<List<TransactionHistoryDTO>> getTransactionsTxn() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        String uuid = authenticatedUser.getPrimaryAccount().getAccountUuid();
        List<TransactionHistoryDTO> transactionHistoryDTOS = accountService.getTransactionsTxn(uuid);
        return ResponseEntity.ok(transactionHistoryDTOS);
    }

    @GetMapping("/account/transactions/deposits-withdrawals")
    ResponseEntity<List<TransactionHistoryDTO>> getTransactionsDepositsAndWithdrawals() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        String uuid = authenticatedUser.getPrimaryAccount().getAccountUuid();
        List<TransactionHistoryDTO> transactionHistoryDTOS = accountService.getTransactionsDepositAndWithdrawals(uuid);
        return ResponseEntity.ok(transactionHistoryDTOS);
    }

    @GetMapping("/account/transactions/trade/detail/{trade_id}")
    ResponseEntity<TradeDetailDTO> getTransactionsTradeDetail(@PathVariable(name = "trade_id") Long tradeId) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        TradeDetailDTO tradeDetailDTO = accountService.getTradeDetail(tradeId);
        return ResponseEntity.ok(tradeDetailDTO);
    }

    @GetMapping("/account/transactions/deposits-withdrawals/detail/{transaction_id}")
    ResponseEntity<TransactionAmountDTO> getTransactionsDepositsAndWithdrawalsDetail(@PathVariable(name = "transaction_id") Long transactionId) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        TransactionAmountDTO transactionAmountDTO = accountService.getTransactionsDepositsAndWithdrawalsDetail(transactionId);
        return ResponseEntity.ok(transactionAmountDTO);
    }

    @GetMapping("/account/orders")
    ResponseEntity<List<OrderHistoryDTO>> getAccountOrder() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        String uuid = authenticatedUser.getPrimaryAccount().getAccountUuid();
        List<OrderHistoryDTO> orderHistoryDTOS = accountService.getAccountOrder(uuid);
        return ResponseEntity.ok(orderHistoryDTOS);
    }

    @GetMapping("/account/orders/detail/{order_id}")
    ResponseEntity<OrderHistoryDTO> getAccountOrderDetail(@PathVariable(name = "order_id") Long order_id) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        OrderHistoryDTO orderHistoryDTO = accountService.getOrderDetail(order_id);
        return ResponseEntity.ok(orderHistoryDTO);
    }

    @GetMapping("/account/sales-profit")
    ResponseEntity<List<ProfitLossDTO>> getAccountSalesProfit() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        String uuid = authenticatedUser.getPrimaryAccount().getAccountUuid();
        List<ProfitLossDTO> profitLossDTOS = accountService.getAccountSalesProfit(uuid);
        return ResponseEntity.ok(profitLossDTOS);
    }

    @GetMapping("/account/sales-profit/detail/{sales_id}")
    ResponseEntity<ProfitLossDTO> getAccountSalesProfitDetail(@PathVariable(name = "sales_id") Long salesId) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        ProfitLossDTO profitLossDTO = accountService.getAccountSalesProfitDetail(salesId);
        return ResponseEntity.ok(profitLossDTO);
    }

    @GetMapping("/account/ipo")
    ResponseEntity<List<OwnedIpoDTO>> getOwnedIpoDTO(){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        String uuid = authenticatedUser.getPrimaryAccount().getAccountUuid();

        List<OwnedIpoDTO> ownedIpoDTOS = accountService.getOwnedIpoDTO(uuid);

        return ResponseEntity.ok(ownedIpoDTOS);
    }

    @GetMapping("/account")
    ResponseEntity<AccountDTO> getAccount() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        String uuid = authenticatedUser.getPrimaryAccount().getAccountUuid();
        AccountDTO accountDTO = accountService.getAccount(uuid);
        return ResponseEntity.ok(accountDTO);
    }


}
