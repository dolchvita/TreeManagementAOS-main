package com.snd.app.repository.user;


import android.util.Log;

import androidx.lifecycle.LiveData;

import com.snd.app.domain.user.UserDTO;
import com.snd.app.domain.user.UserModifyInfoDTO;

public class UserUseCase {
    public String TAG = this.getClass().getName();
    UserRepository userRepository;

    public UserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    // 유저회원 정보 by UserId
    public void loadUserDTO(String authorization, String userId){
        Log.d(TAG, "loadUserDTO ** " + userId + " * " + authorization);     // nulll * null
        userRepository.getUserInfoByUserId(authorization, userId);
    }
    public LiveData<UserDTO> getUserDTO(){
        return userRepository.getUserDTO();
    }


    // 아이디 중복체크
    public void loadIdCheck(String id){
        userRepository.idCheck(id);
    }
    public LiveData<String> idCheck(){
        return userRepository.getIdCheck();
    }



    // 회원가입
    public void loadRegisterUser(UserDTO user){
        Log.d(TAG, "** loadRegisterUser ** " + user);
        userRepository.appRegisterUser(user);
    }
    public LiveData<String> appRegisterUser(){
        return userRepository.getRegisterUser();
    }





}
