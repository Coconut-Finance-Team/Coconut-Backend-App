package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.trade.OrderDTO;
import com.coconut.stock_app.entity.on_premise.Account;
import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.exception.CustomException;
import com.coconut.stock_app.exception.ErrorCode;
import com.coconut.stock_app.service.AuthenticationService;
import com.coconut.stock_app.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class TradeController {
    private final TradeService tradeService;

    private final AuthenticationService authenticationService;

    @PostMapping("/buy-order")
    public ResponseEntity<Void> buyOrder(@RequestBody OrderDTO orderDTO) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Account account = authenticatedUser.getPrimaryAccount();
        if(account == null) throw new CustomException(ErrorCode.NOT_EXIST_ACCOUNT);

        tradeService.processBuyOrder(orderDTO, account);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sell-order")
    public ResponseEntity<Void> sellOrder(@RequestBody OrderDTO orderDTO) {
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Account account = authenticatedUser.getPrimaryAccount();
        if(account == null) throw new CustomException(ErrorCode.NOT_EXIST_ACCOUNT);

        tradeService.processSellOrder(orderDTO, account);
        return ResponseEntity.ok().build();
    }
}
