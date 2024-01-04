package com.snd.app.domain.treeManagement;
import java.time.LocalDateTime;
import lombok.Data;

// 병충해 방제
@Data
public class TreePesticidesDTO {
    private String nfc;
    private String pesticides;
    private String pesticidesDate;
    private String pesticidesOperator;
    private String pesticidesCompany;
    private String pesticidesBusiness;
    private String pesticidesNote;
}
