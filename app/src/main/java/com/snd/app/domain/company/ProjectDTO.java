package com.snd.app.domain.company;

import java.util.List;

import lombok.Data;

// (특정 업체) 사업 객체
@Data
public class ProjectDTO {
    private Integer index_id;
    private String company;
    private String business;
    private String details;
    private List<Double> start;
    private List<Double> termination;
    private String status;
    private String area;
    private Integer cost;
    private String manager;
}
