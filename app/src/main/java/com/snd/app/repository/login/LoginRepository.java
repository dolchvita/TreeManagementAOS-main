package com.snd.app.repository.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.domain.login.UserCredentialsVO;
import com.snd.app.domain.tree.vo.ResponseVO;
import com.snd.app.data.singleton.RetrofitClient;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginRepository {
    public String TAG = this.getClass().getName();
    LoginService service;
    Retrofit retrofit;

    public MutableLiveData<String> loginCkeck = new MutableLiveData<>();
    public MutableLiveData<String> token = new MutableLiveData<>();
    private MutableLiveData<String> _validation = new MutableLiveData<>();
    public LiveData<String> validation = _validation;

    // 최초 로그인 요청
    public void appLoginRequest(UserCredentialsVO userCredentialsVO) {
        Log.d(TAG, "로그인 시도 " + userCredentialsVO);
        retrofit = new Retrofit.Builder()
                .baseUrl("http://125.143.174.65:9955/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 추가
                .build();
        service = retrofit.create(LoginService.class);
        service.appLoginRequest(userCredentialsVO)
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
                            Object data = responseVO.getData();
                            token.setValue(data.toString());
                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + response.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + response);
                                token.setValue("fail");

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "** onError ** " + e.getMessage());
                    }
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "** onComplete ** ");
                    }
                });
    }



    public void validationCheckRequest(String Authorization){
        LoginService service = RetrofitClient.getInstance(Authorization).create(LoginService.class);
        Call<ResponseBody> call = service.validationCheckRequest();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response);
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());

                    String responseVO = response.toString();    // 얘를 제이슨으로 만들면
                    Log.d(TAG, "** 지금 체크 중 **" + responseVO);
                    _validation.setValue(responseVO);

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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"** 통신 실패 ** "+t.getMessage());
            }
        });
    }



}
