package com.snd.app.domain.treeManagement;

import lombok.Data;

// 생육환경개선 정보
@Data
public class TreeHorticultureDTO {
    private String nfc;
    private String horticulture;
    private String horticultureDate;
    private String horticultureOperator;
    private String horticultureCompany;
    private String horticultureBusiness;
    private String horticultureNote;
}
