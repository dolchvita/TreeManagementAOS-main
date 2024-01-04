package com.snd.app.domain.tree.vo;

import java.util.List;

import lombok.Data;

@Data
public class TreePestInfoVO {
    private Integer index_no;
    private String nfc;
    private String species;
    private String pest;
    private List<Double> occurred;  // 발생일시 (필수)
    private String severity;
    private List<Double> recovered;
    private String submitter;
    private String vendor;
    private List<Double> inserted;
}
