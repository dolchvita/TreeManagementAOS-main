package com.snd.app.domain.treeManagement;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TreeSurgeryDTO {
    private String nfc;
    private String surgery;
    private String surgeryDate;
    private String surgeryOperator;
    private String surgeryCompany;
    private String surgeryBusiness;
    private String surgeryNote;
}
