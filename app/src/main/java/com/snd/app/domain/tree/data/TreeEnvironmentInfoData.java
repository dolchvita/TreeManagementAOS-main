package com.snd.app.domain.tree.data;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreeEnvironmentInfoData {
    private String nfc;
    private Double frameHorizontal;
    private Double frameVertical;
    private String frameMaterial;
    private Boolean boundaryStone;
    private Double roadWidth;
    private Double sidewalkWidth;
    private String packingMaterial;
    private Double soilPH;
    private Double soilDensity;
    private String submitter;
    private String vendor;

    private List<Double> inserted;
    private List<Double> modified;

}
