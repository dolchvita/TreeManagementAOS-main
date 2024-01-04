package com.snd.app.domain.tree.dto;

import lombok.Builder;
import lombok.Data;

// 수목 위치 상세 정보
@Data
public class TreeSpecificLocationInfoDTO {
    private String nfc;
    private Boolean sidewalk;
    private double distance;
    private int carriageway;
}
