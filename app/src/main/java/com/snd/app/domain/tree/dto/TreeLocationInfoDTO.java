package com.snd.app.domain.tree.dto;

import java.util.List;

import lombok.Data;

// 수목 위치 정보
@Data
public class TreeLocationInfoDTO {
    private String nfc;
    private double latitude;
    private double longitude;
    private String postalCode;
    private String address;
    private String streetCode;
    private String areaCode;
    private int carriageway;
    private boolean sidewalk;
    private int distance;
    private int orderNumber;
    private String submitter;
    private String vendor;
    private List<Double> inserted;
    private List<Double> modified;

}