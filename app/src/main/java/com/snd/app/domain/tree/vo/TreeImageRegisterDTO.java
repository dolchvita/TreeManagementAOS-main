package com.snd.app.domain.tree.vo;

import java.io.File;

public class TreeImageRegisterDTO {

    String tagId;
    File image1;
    File image2;


    public String getTagId() {
        return tagId;
    }
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
    public File getImage1() {
        return image1;
    }
    public void setImage1(File image1) {
        this.image1 = image1;
    }
    public File getImage2() {
        return image2;
    }
    public void setImage2(File image2) {
        this.image2 = image2;
    }
}
