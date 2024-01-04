package com.snd.app.domain.tree.dto;

import lombok.Data;

@Data
public class TreeInitializingDTO {
    /*Basic Info*/
    /*Basic Location*/
    private String nfc;
    private String species;
    private String submitter;
    private String vendor;
    /*Basic Location*/
    private double longitude;
    private double latitude;
    private String hashtag;
}
