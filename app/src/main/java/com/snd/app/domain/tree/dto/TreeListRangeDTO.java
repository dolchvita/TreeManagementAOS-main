package com.snd.app.domain.tree.dto;

import lombok.Data;

@Data
public class TreeListRangeDTO {
    private Double latitude;    // 위도 (필수)
    private Double longitude;   // 경도 (필수)
    private Double range;       // 범위 (필수)
    private Integer treeCount;  // 나무개수 (필수)
    private Double minLatitude;
    private Double maxLatitude;
    private Double minLongitude;
    private Double maxLongitude;
    private Double centerLatitude;
    private Double centerLongitude;
}
