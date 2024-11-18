package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.account.AssetDTO;

public interface AccountService {
    AssetDTO getAsset(String uuid);
}
