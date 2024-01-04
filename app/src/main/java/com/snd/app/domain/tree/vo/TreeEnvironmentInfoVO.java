package com.snd.app.domain.tree.vo;

import java.util.List;

import lombok.Builder;
import lombok.Data;

// 수목 환경 정보
@Data
@Builder
public class TreeEnvironmentInfoVO {
    private String nfc;
    private double frameHorizontal;
    private double frameVertical;
    private String frameMaterial;
    private Boolean boundaryStone;
    private double roadWidth;
    private double sidewalkWidth;
    private String packingMaterial;
    private double soilPH;
    private double soilDensity;
    private String submitter;
    private String vendor;

    private List<Double> inserted;
    private List<Double> modified;

}
