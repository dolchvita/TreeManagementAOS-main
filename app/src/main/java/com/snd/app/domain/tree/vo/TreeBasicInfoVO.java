package com.snd.app.domain.tree.vo;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreeBasicInfoVO {
    private String nfc;
    private String species;
    private String submitter;
    private String vendor;
    private List<Double> inserted;
    private List<Double> modified;
    private String hashtag;
}
