package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.ipo.IPODTO;
import com.coconut.stock_app.dto.ipo.IPORequestDTO;
import com.coconut.stock_app.entity.cloud.IPO;
import com.coconut.stock_app.entity.on_premise.Account;

import java.util.List;

public interface IPOService {
    List<IPODTO> getActiveIPOs();
    void subscriptionIPO(IPORequestDTO ipoRequestDTO, Account account);
}
