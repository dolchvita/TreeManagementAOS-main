package com.snd.app.domain.tree.dto;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.snd.app.BR;
import java.util.List;

import lombok.Builder;
import lombok.Data;


// 수목 환경 정보
@Data
public class TreeEnvironmentInfoDTO extends BaseObservable {
    private String nfc;
    private Double frameHorizontal;
    private Double frameVertical;
    private String frameMaterial;   //보호틀 재질
    private Boolean boundaryStone;
    private Double roadWidth;
    private Double sidewalkWidth;
    private String packingMaterial;     // 포장재 재질
    private Double soilPH;
    private Double soilDensity;
    private String submitter;
    private String vendor;

    private List<Double> inserted;
    private List<Double> modified;


}
