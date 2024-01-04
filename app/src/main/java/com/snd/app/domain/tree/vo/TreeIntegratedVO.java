package com.snd.app.domain.tree.vo;

import java.util.List;
import lombok.Data;

// 수목의 모든 정보를 담는 객체
@Data
public class TreeIntegratedVO {

    /*Data Of TreeBasicInfo*/
    private String nfc;
    private String species;
    private String basicSubmitter;
    private String basicVendor;
    private List<Double> basicInserted;
    private List<Double> basicModified;
    private String hashtag;

    /*Data Of TreeLocationInfo*/
    private Double latitude;
    private Double longitude;
    private String postalCode;
    private String address;
    private String streetCode;
    private String areaCode;
    private Integer carriageway;
    private Boolean sidewalk;
    private Integer distance;
    private Integer orderNumber;
    private String locationSubmitter;
    private String locationVendor;
    private List<Double> locationInserted;
    private List<Double> locationModified;

    /*Data Of TreeStatusInfo*/
    private Double dbh;
    private Double rcc;
    private Double height;
    private Double length;
    private Double width;
    private Boolean pest;
    private List<Double> creation;
    private String statusSubmitter;
    private String statusVendor;
    private List<Double> statusInserted;
    private List<Double> statusModified;

    /*Data Of TreeEnvironmentInfo*/
    private Double frameHorizontal;
    private Double frameVertical;
    private String frameMaterial;
    private Boolean boundaryStone;
    private Double roadWidth;
    private Double sideWalkWidth;
    private String packingMaterial;
    private Double soilPH;
    private Double soilDensity;
    private String environmentSubmitter;
    private String environmentVendor;
    private List<Double> environmentInserted;
    private List<Double> environmentModified;

}
