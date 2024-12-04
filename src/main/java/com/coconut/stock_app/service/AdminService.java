package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.admin.UserInfoDetailForAdminDTO;
import com.coconut.stock_app.dto.admin.UserInfoForAdminDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface AdminService {
    List<UserInfoForAdminDto> getAllUser();
    UserInfoDetailForAdminDTO getUser(String uuid);
    void suspendUser(String uuid);
    void suspendAccount(String uuid);
    void resumeUser(String uuid);
    void resumeAccount(String uuid);
}
