package com.snd.app.repository.login;

import com.snd.app.domain.login.UserCredentialsVO;
import com.snd.app.domain.tree.vo.ResponseVO;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginService {

    // 외부로그인 요청 - AOS App으로 부터 로그인 요청 처리 프로세스
    @POST("app/login")
    Observable<Response<ResponseVO>> appLoginRequest(@Body UserCredentialsVO userCredentialsVO);

    // JsonWebToken 유효성 검증
    @GET("app/validationCheck")
    Call<ResponseBody> validationCheckRequest();


}
