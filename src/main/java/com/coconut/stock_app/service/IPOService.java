package com.coconut.stock_app.service;

import com.coconut.stock_app.dto.ipo.IPODTO;
import com.coconut.stock_app.entity.cloud.IPO;

import java.util.List;

public interface IPOService {
    List<IPODTO> getActiveIPOs();
}
