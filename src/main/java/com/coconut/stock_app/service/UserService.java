package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.auth.UserRegisterRequest;
import com.coconut.stock_app.dto.user.UserInfoDto;
import com.coconut.stock_app.entity.on_premise.User;

public interface UserService {

    void registerUser(UserRegisterRequest request);

    UserInfoDto getUserInfo(User user);
}
