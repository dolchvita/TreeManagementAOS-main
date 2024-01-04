package com.snd.app.domain.config;

import java.util.List;

import lombok.Data;

@Data
public class AosConfigDTO {
    private String currentVersion;
    private String currentFileName;
    private String currentFileSize;
    private List<Double> registeredDatetime;
}