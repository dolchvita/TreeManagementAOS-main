package com.snd.app.domain.tree.dto;

import lombok.Data;

@Data
public class TreePestInfoDTO {
    private Integer index_no;
    private String nfc;
    private String pest;
    private String occurred;
    private String severity;
    private String recovered;
    private String submitter;
    private String vendor;
    private String inserted;
}
