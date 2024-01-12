package com.snd.app.repository.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.snd.app.data.singleton.RetrofitClient2;
import com.snd.app.domain.user.UserDTO;
import com.snd.app.domain.tree.vo.ResponseVO;
import com.snd.app.data.singleton.RetrofitClient;
import com.snd.app.domain.user.UserModifyInfoDTO;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserRepository {
    public String TAG = this.getClass().getName();
    MutableLiveData<UserDTO> userDTO = new MutableLiveData<>();
    MutableLiveData<String> idCheck = new MutableLiveData<>();
    MutableLiveData<String> registerUser = new MutableLiveData<>();
    MutableLiveData<UserModifyInfoDTO> userModifyInfo = new MutableLiveData<>();


    /* -----------------------------------------------CREATE METHODS----------------------------------------------- */

    // 회원가입
    public LiveData<String> getRegisterUser() {
        return registerUser;
    }
    public void appRegisterUser(UserDTO user){
        UserService userService = RetrofitClient2.getInstance().create(UserService.class);
        userService.appRegisterUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + response.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + response.toString());

                            ResponseVO responseVO = response.body();
                            String message = responseVO.getMessage();
                            Log.d(TAG, "** 성공 / 응답 !!!! ** " + message);
                            String result = responseVO.getResult();
                            registerUser.setValue(result);

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + response.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + response);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }



    /* ----------------------------------------------- READ METHODS ----------------------------------------------- */

    // 유저회원 정보 by UserId
    public LiveData<UserDTO> getUserDTO(){
        return userDTO;
    }
    public void getUserInfoByUserId(String authorization, String userId){
        UserService service = RetrofitClient.getInstance(authorization).create(UserService.class);
        Call<ResponseVO> call = service.getUserInfoByUserId(userId);
        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());
                    ResponseVO responseVO = response.body();
                    Object data = responseVO.getData();

                    Gson gson = new Gson();
                    String jsonData = gson.toJson(data);        // Object -> Json -> DTO
                    UserDTO userDTOData = gson.fromJson(jsonData, UserDTO.class);
                    userDTO.setValue(userDTOData);

                    Log.d(TAG,"** userDTO ** " + userDTO);

                }else {
                    try {
                        Log.d(TAG,"** 오류 / 응답 ** " + response.body());
                        Log.d(TAG,"** 오류 / 응답 ** " + response.toString());
                        Log.d(TAG,"** 오류 / 응답 ** "+response.errorBody().string());
                        Log.d(TAG,"** 오류 / 응답 ** "+response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseVO> call, Throwable t) {
                Log.d(TAG,"** 통신 실패 ** "+t.getMessage());
            }
        });
    }



    // 아이디 중복체크
    public LiveData<String> getIdCheck(){
        return idCheck;
    }

    public void idCheck(String id){
        UserService companyService = RetrofitClient2.getInstance().create(UserService.class);
        Call<ResponseVO> call = companyService.idCheck(id);
        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());

                    ResponseVO responseVO = response.body();
                    idCheck.setValue(responseVO.getMessage());

                }else {
                    try {
                        Log.d(TAG,"** 오류 / 응답 ** " + response.body());
                        Log.d(TAG,"** 오류 / 응답 ** " + response.toString());
                        Log.d(TAG,"** 오류 / 응답 ** "+response.errorBody().string());
                        Log.d(TAG,"** 오류 / 응답 ** "+response);
                        idCheck.setValue("fail");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseVO> call, Throwable t) {
                Log.d(TAG,"** 통신 실패 ** "+t.getMessage());
            }
        });
    }




}
