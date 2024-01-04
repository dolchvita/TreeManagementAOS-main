package com.snd.app.domain.treeManagement;

import lombok.Data;

// 수목 맹아제거 정보
@Data
public class TreeDefoliationDTO {
    private String nfc;
    private String defoliation;
    private String defoliationDate;
    private String defoliationOperator;
    private String defoliationCompany;
    private String defoliationBusiness;
    private String defoliationNote;
}
