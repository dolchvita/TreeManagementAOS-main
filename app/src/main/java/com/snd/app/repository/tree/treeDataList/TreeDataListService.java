package com.snd.app.repository.tree.treeDataList;

import com.snd.app.domain.tree.vo.ResponseVO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TreeDataListService {


    /* -------------------------------------------READ METHODS------------------------------------------- */

    // 수목 기본정보 - 수종 리스트 가져오기
    @GET("app/tree/get/treeSpeciesList")
    Call<ResponseVO> getTreeSpeciesList();

    // 수목 환경정보 - 보호틀 재질 리스트 가져오기
    @GET("app/tree/get/frameMaterialList")
    Call<ResponseVO> getFrameMaterialList();

    // 수목 환경정보 - 포장재 재질 리스트 가져오기
    @GET("app/tree/get/packingMaterialList")
    Call<ResponseVO> getPackingMaterialList();

    // 수목 상태정보 - 병충해명 리스트 가져오기
    @GET("app/tree/get/pestNameList")
    Call<ResponseVO> getPestNameList();


}
