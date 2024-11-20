package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.user.UserInfoDto;
import com.coconut.stock_app.dto.user.UserInfoForAdminDto;
import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.repository.on_premise.UserRepository;
import com.coconut.stock_app.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;

    public List<UserInfoForAdminDto> getAllUser(){
        List<User> users = userRepository.findAll();
        List<UserInfoForAdminDto> userInfoDtos = users.stream()
                .map(this::mapUserToUserInfoDto)
                .collect(Collectors.toList());

        return userInfoDtos;
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
