package com.snd.app.repository.config;

import com.snd.app.domain.tree.vo.ResponseVO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AosConfigService {

    /* ------------------------------------------- READ ------------------------------------------- */

    // 현재 APK 정보 가져오기
    @GET("config/getCurrentApkInfo")
    Call<ResponseVO> getCurrentApkInfo();


    // 현재 APK 다운로드 URL 가져오기
    @GET("config/getCurrentDownloadLink")
    Call<ResponseVO> getCurrentDownloadLink();


}
