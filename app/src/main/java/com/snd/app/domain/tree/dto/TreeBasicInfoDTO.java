package com.snd.app.domain.tree.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class TreeBasicInfoDTO {
    private String nfc;
    private String species;
    private String submitter;
    private String vendor;
    private List<Double> inserted;
    private List<Double> modified;
    private String hashtag;

}
