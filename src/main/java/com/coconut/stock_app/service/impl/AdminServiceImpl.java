package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.config.ReadOnlyTransaction;
import com.coconut.stock_app.config.WriteTransaction;
import com.coconut.stock_app.dto.admin.AccountInfoDTO;
import com.coconut.stock_app.dto.admin.UserHistoryDTO;
import com.coconut.stock_app.dto.admin.UserInfoDetailForAdminDTO;
import com.coconut.stock_app.dto.admin.UserInfoForAdminDto;
import com.coconut.stock_app.entity.on_premise.*;
import com.coconut.stock_app.exception.CustomException;
import com.coconut.stock_app.exception.ErrorCode;
import com.coconut.stock_app.repository.on_premise.AccountRepository;
import com.coconut.stock_app.repository.on_premise.UserRepository;
import com.coconut.stock_app.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @ReadOnlyTransaction
    public List<UserInfoForAdminDto> getAllUser(){
        List<User> users = userRepository.findAll();
        List<UserInfoForAdminDto> userInfoDtos = users.stream()
                .map(this::mapUserToUserInfoDto)
                .collect(Collectors.toList());

        return userInfoDtos;
    }

    @ReadOnlyTransaction
    public UserInfoDetailForAdminDTO getUser(String uuid){
        User user = userRepository.findByUserUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));

        Account account = user.getPrimaryAccount();

        List<UserHistoryDTO> orderHistory = account.getOrders().stream()
                .map(this::orderToUserHistoryDTO).collect(Collectors.toList());

        List<UserHistoryDTO> transactionHistory = account.getTransactions().stream()
                .map(this::transactionToUserHistoryDTO).collect(Collectors.toList());

        List<UserHistoryDTO> combineHistory = Stream.concat(orderHistory.stream(),transactionHistory.stream())
                .sorted(Comparator.comparing(UserHistoryDTO::getTime).reversed())
                .collect(Collectors.toList());

        AccountInfoDTO accountInfoDTO = AccountInfoDTO.builder()
                .accountId(account.getAccountId())
                .accountName(account.getAccountAlias())
                .accountStatus(account.getAccountStatus())
                .createTime(account.getCreatedAt())
                .build();

        return UserInfoDetailForAdminDTO.builder()
                .uuid(uuid)
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .job(user.getJob())
                .investmentStyle(user.getInvestmentStyle())
                .status(user.getAccountStatus())
                .createTime(account.getCreatedAt())
                .role(user.getRole())
                .accountInfo(accountInfoDTO)
                .userHistory(combineHistory)
                .build();
    }

    @WriteTransaction
    public void suspendUser(String uuid){
        User user = userRepository.findByUserUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));

        if(user.getAccountStatus() == UserAccountStatus.SUSPENDED){
            throw new CustomException(ErrorCode.USER_ALREADY_SUSPEND);
        }

        user.setAccountStatus(UserAccountStatus.SUSPENDED);
        userRepository.save(user);
    }

    @WriteTransaction
    public void resumeUser(String uuid){
        User user = userRepository.findByUserUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));

        if(user.getAccountStatus() == UserAccountStatus.ACTIVE){
            throw new CustomException(ErrorCode.USER_ALREADY_ACTIVE);
        }

        user.setAccountStatus(UserAccountStatus.ACTIVE);
        userRepository.save(user);
    }

    @WriteTransaction
    public void suspendAccount(String uuid){
        Account account = accountRepository.findByAccountUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));

        if(account.getAccountStatus() == AccountStatus.OPEN){
            throw new CustomException(ErrorCode.ACCOUNT_ALREADY_ACTIVE);
        }

        account.setAccountStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    @WriteTransaction
    public void resumeAccount(String uuid){
        Account account = accountRepository.findByAccountUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));

        if(account.getAccountStatus() == AccountStatus.CLOSED){
            throw new CustomException(ErrorCode.ACCOUNT_ALREADY_SUSPEND);
        }

        account.setAccountStatus(AccountStatus.OPEN);
        accountRepository.save(account);
    }

    private UserHistoryDTO orderToUserHistoryDTO(Order order){
        String title = order.getOrderType() == OrderType.BUY ? "매수 주문" : "매도 주문";
        String description = String.format(
                "%s %d주 %s",
                order.getStockCode(),
                order.getOrderQuantity(),
                order.getOrderType() == OrderType.BUY ? "매수" : "매도"
        );

        return UserHistoryDTO.builder()
                .time(order.getCreatedAt())
                .title(title)
                .description(description)
                .build();
    }

    private UserHistoryDTO transactionToUserHistoryDTO(Transaction transaction){
        String title = transaction.getTransactionType() == TransactionType.DEPOSIT ? "입금" : "출금";

        String formattedAmount = String.format("%,d원", transaction.getAmount().intValue());

        String description = String.format(
                "%s %s %s",
                transaction.getName(),
                formattedAmount,
                title
        );

        return UserHistoryDTO.builder()
                .time(transaction.getTransactionDate())
                .title(title)
                .description(description)
                .build();
    }


    private UserInfoForAdminDto mapUserToUserInfoDto(User user){
        return UserInfoForAdminDto.builder()
                .uuid(user.getUserUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createTime(user.getCreatedAt())
                .status(user.getAccountStatus())
                .build();
    }
}
