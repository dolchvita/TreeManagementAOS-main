package com.snd.app.domain.tree.dto;


import lombok.Data;

@Data
public class TreeCoordinateDTO {

    private String nfc;
    private double longitude;
    private double latitude;
    private String submitter;
    private String vendor;

}
