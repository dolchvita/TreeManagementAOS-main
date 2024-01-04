package com.snd.app.domain.tree.vo;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

// 수목 상태 정보
@Data
@Builder
public class TreeStatusInfoVO {
    private String nfc;     // not Null
    private double dbh;
    private double rcc;
    private double height;
    private double length;
    private double width;
    private Boolean pest;
    private LocalDate creation;
    private String submitter;       // not Null
    private String vendor;          // not Null
    private List<Double> inserted;
    private List<Double> modified;

}