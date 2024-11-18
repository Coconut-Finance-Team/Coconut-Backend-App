package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.account.AssetDTO;
import com.coconut.stock_app.entity.on_premise.Account;
import com.coconut.stock_app.entity.on_premise.OwnedStock;
import com.coconut.stock_app.exception.CustomException;
import com.coconut.stock_app.exception.ErrorCode;
import com.coconut.stock_app.repository.on_premise.AccountRepository;
import com.coconut.stock_app.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    public AssetDTO getAsset(String uuid){
        Account account = accountRepository.findByAccountUuid(uuid).orElseThrow(()-> new CustomException(ErrorCode.NOT_EXIST_ACCOUNT));

        List<OwnedStock> ownedStocks = account.getOwnedStocks();
        BigDecimal investedAmount = BigDecimal.ZERO;

        for(OwnedStock ownedStock : ownedStocks){
            investedAmount = investedAmount.add(ownedStock.getTotalPurchasePrice());
        }

        BigDecimal totalAssets = account.getDeposit().add(investedAmount);

        return AssetDTO.builder()
                .accountAlias(account.getAccountAlias())
                .accountId(account.getAccountId())
                .totalAssets(totalAssets)
                .deposit(account.getDeposit())
                .investedAmount(investedAmount)
                .build();
    }
}
