package com.snd.app.domain.tree.dto;

import java.util.List;

import lombok.Data;

// 수목 상태 정보
@Data
public class TreeStatusInfoDTO {
    private String nfc;
    private double dbh;     // 흉고직경
    private double rcc;     // 근원직경
    private double height;  // 수고
    private double length;  // 지하고
    private double width;   //수관폭
    private Boolean pest;   // 병충해 유무
    private String creation;    // 조성일자
    private String submitter;       // not Null
    private String vendor;          // not Null
    private List<Double> inserted;
    private List<Double> modified;

}