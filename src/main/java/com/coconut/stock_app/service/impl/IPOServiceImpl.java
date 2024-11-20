package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.ipo.IPODTO;
import com.coconut.stock_app.dto.ipo.IPORequestDTO;
import com.coconut.stock_app.entity.cloud.IPO;
import com.coconut.stock_app.entity.on_premise.Account;
import com.coconut.stock_app.entity.on_premise.OwnedIPO;
import com.coconut.stock_app.exception.CustomException;
import com.coconut.stock_app.exception.ErrorCode;
import com.coconut.stock_app.repository.cloud.IPORepository;
import com.coconut.stock_app.repository.on_premise.OwnedIPORepository;
import com.coconut.stock_app.service.IPOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IPOServiceImpl implements IPOService {
    private final IPORepository ipoRepository;
    private final OwnedIPORepository ownedIPORepository;


    public List<IPODTO> getActiveIPOs() {
        LocalDate today = LocalDateTime.now().toLocalDate(); // LocalDate로 변환
        List<IPO> activeIPOs = ipoRepository.findActiveIPOs(today);

        return activeIPOs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void subscriptionIPO(IPORequestDTO ipoRequestDTO, Account account){
        IPO ipo = ipoRepository.findByIPOId(ipoRequestDTO.getIpoId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_IPO));

        if(ipoRequestDTO.getQuantity() > ipo.getMaxSubscriptionLimit()){
            throw new CustomException(ErrorCode.MAX_IPO_REQUEST_EXCEEDED);
        }

        OwnedIPO ownedIPO = OwnedIPO.builder()
                .ownedIPOid(ipo.getIPOId())
                .quantity(ipoRequestDTO.getQuantity())
                .totalPrice(ipo.getFinalOfferPrice().multiply(new BigDecimal(ipoRequestDTO.getQuantity())))
                .account(account)
                .build();

        ownedIPORepository.save(ownedIPO);
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
