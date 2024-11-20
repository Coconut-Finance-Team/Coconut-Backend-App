package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.user.UserInfoForAdminDto;

import java.util.List;

public interface AdminService {
    List<UserInfoForAdminDto> getAllUser();
}
