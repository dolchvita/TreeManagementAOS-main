package com.snd.app.domain.treeDataList;


// 각 리스트 받아온 객체를 담아두는 객체 - 곧 없어질 예정
public class TreeDataList {
    private String species;
    private String pestName;
    private String packingMaterial;
    private String frameMaterial;


    // getters
    public String getSpecies() {
        return species;
    }
    public String getPestName() {
        return pestName;
    }
    public String getPackingMaterial() {
        return packingMaterial;
    }
    public String getFrameMaterial() {
        return frameMaterial;
    }
}
