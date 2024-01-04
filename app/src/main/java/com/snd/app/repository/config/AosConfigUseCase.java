package com.snd.app.repository.config;


import androidx.lifecycle.LiveData;

public class AosConfigUseCase {
    public String TAG = this.getClass().getName();
    AosConfigRepository aosConfigRepository;


    public AosConfigUseCase(AosConfigRepository aosConfigRepository) {
        this.aosConfigRepository = aosConfigRepository;
    }


    /* ------------------------------------------------- READ ------------------------------------------------- */

    // AOS 앱 버전 정보 확인
    public void loadCurrentApkInfo(){
        aosConfigRepository.getCurrentApkInfo();
    }
    public LiveData<String> getCurrentApkInfo(){
        return  aosConfigRepository.currentVersion;
    }


    // 현재 APK 다운로드 URL 가져오기
    public void loadCurrentDownloadLink(){
        aosConfigRepository.getCurrentDownloadLink();
    }
    public LiveData<String> getCurrentDownloadLink(){
        return  aosConfigRepository.currentDownloadLink;
    }



}
