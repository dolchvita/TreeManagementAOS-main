package com.snd.app.repository.login;

import androidx.lifecycle.LiveData;

import com.snd.app.domain.login.UserCredentialsVO;

public class LoginUseCase {
    public String TAG = this.getClass().getName();
    LoginRepository loginRepository;

    public LoginUseCase(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }


    public void loadLoginRequest(UserCredentialsVO userCredentialsVO){
        loginRepository.appLoginRequest(userCredentialsVO);
    }
    /*
    public LiveData<String> isLoginCheck(){
        return loginRepository.loginCkeck;
    }
     */

    public LiveData<String> getToken(){
        return loginRepository.token;
    }



    public void loadValidation(String Authorization){
        loginRepository.validationCheckRequest(Authorization);
    }
    public LiveData<String> getValidation(){
        return loginRepository.validation;
    }


    /* ----------------------------------------------- Dispose ----------------------------------------------- */

    // 리소스 해제
    public void setDisposables(){
        if(loginRepository != null){
            loginRepository.setDisposables();
        }
    }

}
