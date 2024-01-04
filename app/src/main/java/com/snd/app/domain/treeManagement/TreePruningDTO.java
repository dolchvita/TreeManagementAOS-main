package com.snd.app.domain.treeManagement;

import lombok.Data;

// 수목 가지치기 정보
@Data
public class TreePruningDTO {
    private String nfc;
    private String pruning;
    private String pruningDate;
    private String pruningOperator;
    private String pruningCompany;
    private String pruningBusiness;
    private String pruningNote;
}
