package com.snd.app.repository.user;

import com.snd.app.domain.tree.vo.ResponseVO;
import com.snd.app.domain.user.UserDTO;
import com.snd.app.domain.user.UserModifyInfoDTO;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    /* -----------------------------------------------CREATE METHODS----------------------------------------------- */

    // 회원가입
    @POST("app/user/register")
    Observable<Response<ResponseVO>> appRegisterUser(@Body UserDTO user);



    /* -----------------------------------------------READ METHODS----------------------------------------------- */

    // 유저회원 정보 by UserId
    @GET("app/user/info/{userId}")
    Call<ResponseVO> getUserInfoByUserId(@Path("userId") String userId);

    // 아이디 중복체크
    @GET("app/user/register/idCheck")
    Call<ResponseVO> idCheck(@Query("id") String id);



    /* -----------------------------------------------UPDATE METHODS----------------------------------------------- */

    // 유저회원 정보 수정 by UserId
    @POST("app/user/info/put/userInfo")
    Observable<Response<ResponseVO>> patchUserInfoByUserId(@Body UserModifyInfoDTO user);


}
