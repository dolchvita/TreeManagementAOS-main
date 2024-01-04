package com.snd.app.domain.treeManagement;

import java.util.List;

import lombok.Data;

@Data
public class TreeManagementIntegratedVOForProject {
    private String business;
    private String nfc;
    private String operator;
    private String company;
    private List<Double> operationDate;
    private List<Double> inserted;
}
