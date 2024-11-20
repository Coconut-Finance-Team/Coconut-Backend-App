package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.admin.UserInfoDetailForAdminDTO;
import com.coconut.stock_app.dto.admin.UserInfoForAdminDto;

import java.util.List;

public interface AdminService {
    List<UserInfoForAdminDto> getAllUser();
    UserInfoDetailForAdminDTO getUser(String uuid);
}
