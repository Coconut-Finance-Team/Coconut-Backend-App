package com.coconut.stock_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndexDetailDto {
    private String upjongCode;
    private String businessHour;
    private double currentIndex;
    private String prdyVrssSign;
    private double prdyVrss;
    private long acmlVol;
    private long acmlTrPbmn;
    private long pcasVol;
    private long pcasTrPbmn;
    private double prdyCtrt;
    private double openIndex;
    private double highIndex;
    private double lowIndex;
    private double openVrssIndex;
    private String openVrssSign;
    private double highVrssIndex;
    private String highVrssSign;
    private double lowVrssIndex;
    private String lowVrssSign;
    private double prdyClprOpenRate;
    private double prdyClprHighRate;
    private double prdyClprLowRate;
    private int uplmCount;
    private int ascnCount;
    private int stnrCount;
    private int downCount;
    private int lslCount;
    private int strongAscnCount;
    private int strongDownCount;
    private int tickVrss;
}