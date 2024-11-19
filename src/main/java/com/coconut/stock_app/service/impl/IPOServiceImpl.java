package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.ipo.IPODTO;
import com.coconut.stock_app.entity.cloud.IPO;
import com.coconut.stock_app.repository.cloud.IPORepository;
import com.coconut.stock_app.service.IPOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IPOServiceImpl implements IPOService {
    private final IPORepository ipoRepository;

    public List<IPODTO> getActiveIPOs() {
        LocalDate today = LocalDateTime.now().toLocalDate(); // LocalDate로 변환
        List<IPO> activeIPOs = ipoRepository.findActiveIPOs(today);

        return activeIPOs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private IPODTO convertToDTO(IPO ipo) {
        return IPODTO.builder()
                .id(ipo.getIPOId())
                .category(ipo.getCategory())
                .companyName(ipo.getCompanyName())
                .leadUnderwriter(ipo.getLeadUnderwriter())
                .subscriptionStartDate(ipo.getSubscriptionStartDate())
                .subscriptionEndDate(ipo.getSubscriptionEndDate())
                .refundDate(ipo.getRefundDate())
                .maxSubscriptionLimit(ipo.getMaxSubscriptionLimit())
                .finalOfferPrice(ipo.getFinalOfferPrice())
                .build();
    }
}
