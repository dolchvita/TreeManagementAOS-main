package com.snd.app.ui.login;

import androidx.lifecycle.LiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.user.UserDTO;
import com.snd.app.domain.login.UserCredentialsVO;
import com.snd.app.repository.config.AosConfigUseCase;
import com.snd.app.repository.login.LoginUseCase;
import com.snd.app.repository.user.UserUseCase;

import javax.inject.Inject;

public class LoginViewModel extends TMViewModel {
    @Inject
    LoginUseCase loginUseCase;
    @Inject
    UserUseCase userUseCase;
    @Inject
    AosConfigUseCase aosConfigUseCase;


    public LoginViewModel() {
        loginUseCase = getAppComponent().loginUseCase();
        userUseCase = getAppComponent().userUseCase();
        aosConfigUseCase = getAppComponent().aosConfigUseCase();
    }


    // AOS 앱 버전 정보 확인
    public void loadCurrentApkInfo(){
        aosConfigUseCase.loadCurrentApkInfo();
    }
    public LiveData<String> getCurrentApkInfo(){
        return  aosConfigUseCase.getCurrentApkInfo();
    }

    // 현재 APK 다운로드 URL 가져오기
    public void loadCurrentDownloadLink(){
        aosConfigUseCase.loadCurrentDownloadLink();
    }
    public LiveData<String> getCurrentDownloadLink(){
        return  aosConfigUseCase.getCurrentDownloadLink();
    }


    // 로그인 요청
    public void loadLoginRequest(UserCredentialsVO userCredentialsVO){
        loginUseCase.loadLoginRequest(userCredentialsVO);
    }
    public LiveData<String> getToken(){
        return loginUseCase.getToken();
    }


    // 유효성 체크
    public void loadValidation(String Authorization){
        loginUseCase.loadValidation(Authorization);
    }
    public LiveData<String> getValidation(){
        return loginUseCase.getValidation();
    }


    public LiveData<String> isLoginCheck(){
        return loginUseCase.isLoginCheck();
    }


    // 유저 확인
    public void loadUserDTO(String authorization, String userId){
        userUseCase.loadUserDTO(authorization, userId);
    }
    public LiveData<UserDTO> getUserDTO(){
        return userUseCase.getUserDTO();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        loginUseCase = null;
        userUseCase = null;
    }


}
