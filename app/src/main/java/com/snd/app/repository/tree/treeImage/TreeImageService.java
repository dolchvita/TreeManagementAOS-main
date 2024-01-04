package com.snd.app.repository.tree.treeImage;

import com.snd.app.domain.tree.vo.ResponseVO;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface TreeImageService {


    /* -----------------------------------------------CREATE METHODS----------------------------------------------- */

    // 수목 기본정보 이미지등록
    @Multipart
    @POST("app/tree/registerTreeImage")
    Observable<Response<ResponseVO>> registerTreeImage(@Part("tagId") RequestBody tagId, @Part List<MultipartBody.Part> files);


    /* -----------------------------------------------READ METHODS----------------------------------------------- */

    // 수목 저장된 이미지 갯수 확인
    @GET("app/tree/countTreeImages")
    Call<ResponseVO> countTreeImages(@Query("tagId") String tagId);

    // 수목 저장된 파일명 리스트 반환
    @GET("app/tree/getTreeImageFilenameList")
    Call<ResponseVO> getTreeImageFilenameList(@Query("tagId") String tagId);


    /* -----------------------------------------------UPDATE METHODS----------------------------------------------- */

    // 수목 기본정보 이미지수정 (모두)
    @Multipart
    @POST("app/tree/modifyTreeImageAll")
    Observable<Response<ResponseVO>> modifyTreeImageAll(@Part("tagId") RequestBody tagId, @Part List<MultipartBody.Part> files);


    // 수목 기본정보 이미지수정 (특정 하나만)
    @Multipart
    @POST("app/tree/modifyTreeParticularImage")
    Observable<Response<ResponseVO>> modifyTreeParticularImage(@Part("tagId") RequestBody tagId, @Part("keyword") RequestBody keyword, @Part MultipartBody.Part file);


    /* -----------------------------------------------DELETE METHODS----------------------------------------------- */

    // 수목 기본정보 이미지삭제
    @POST("app/tree/deleteTreeImage")
    Observable<Response<ResponseVO>> deleteTreeImage(@Query("tagId") String tagId, @Query("keyword") String keyword);


}
