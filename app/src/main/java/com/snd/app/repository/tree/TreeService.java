package com.snd.app.repository.tree;

import com.snd.app.domain.tree.dto.TreePestInfoDTO;
import com.snd.app.domain.tree.dto.TreeSpecificLocationInfoDTO;
import com.snd.app.domain.tree.vo.ResponseVO;
import com.snd.app.domain.tree.dto.TreeBasicInfoDTO;
import com.snd.app.domain.tree.dto.TreeEnvironmentInfoDTO;
import com.snd.app.domain.tree.dto.TreeInitializingDTO;
import com.snd.app.domain.tree.dto.TreeLocationInfoDTO;
import com.snd.app.domain.tree.dto.TreeStatusInfoDTO;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TreeService {


    /* ----------------------------------------------- CREATE METHODS ----------------------------------------------- */

    // 수목 기본정보 + 위치(기본)정보 등록
    @POST("app/tree/initializeTreeInfoForRegister")
    Observable<Response<ResponseVO>> initializeTreeBasicInfoForRegister(@Body TreeInitializingDTO initializingTreeInfo);

    // 수목 위치(상세)정보 등록
    @POST("app/tree/registerSpecificLocationInfo")
    Observable<Response<ResponseVO>> registerSpecificLocationInfo(@Body TreeSpecificLocationInfoDTO treeSpecificLocationInfo);

    // 수목 상태 정보 등록
    @POST("app/tree/registerStatusInfo")
    Observable<Response<ResponseVO>> registerTreeStatusInfo(@Body TreeStatusInfoDTO treeStatusInfo);

    // 수목 환경 정보 등록
    @POST("app/tree/registerEnvironmentInfo")
    Observable<Response<ResponseVO>> registerTreeEnvironmentInfo(@Body TreeEnvironmentInfoDTO treeEnvironmentInfo);

    // 수목 병해정보 등록
    @POST("app/tree/registerPestInfo")
    Observable<Response<ResponseVO>> registerTreePestInfo(@Body List<TreePestInfoDTO> pestinfoList);



    /* ----------------------------------------------- READ METHODS ----------------------------------------------- */

    // 수목 상태정보 - 병충해명 리스트 가져오기
    @GET("app/tree/get/pestNameList")
    Call<ResponseVO> getPestNameList();

    // 수목 통합 정보 가져오기 by NFC tagId
    @GET("app/tree/getTreeInfo/{tagId}")
    Call<ResponseVO> getTreeInfoByNFCtagId(@Path("tagId") String tagId);

    // 수목 병해 정보 가져오기 by NFC tagId
    @GET("app/tree/getTreePestInfo/{tagId}")
    Call<ResponseVO> getTreePetsInfoByNFCtagId(@Path("tagId") String tagId);

    // 범위내의 수목 통합 정보 리스트 가져오기
    @GET("app/tree/getTreeInfoListByRange")
    Call<ResponseVO> getTreeInfoListByRange(
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("range") Double range,
            @Query("treeCount") Integer treeCount);



    /* ----------------------------------------------- UPDATE METHODS ----------------------------------------------- */

    // 수목 기본정보 수정
    @POST("app/tree/modifyBasicInfo")
    Observable<Response<ResponseVO>> modifyTreeBasicInfo(@Body TreeBasicInfoDTO treeBasicInfo);

    // 수목 위치정보 수정
    @POST("app/tree/modifyLocationInfo")
    Observable<Response<ResponseVO>> modifyTreeLocationInfo(@Body TreeLocationInfoDTO treeLocationInfo);

    // 수목 상태정보 수정
    @POST("app/tree/modifyStatusInfo")
    Observable<Response<ResponseVO>> modifyTreeStatusInfo(@Body TreeStatusInfoDTO treeStatusInfo);

    // 수목 환경정보 수정
    @POST("app/tree/modifyEnvironmentInfo")
    Observable<Response<ResponseVO>> modifyTreeEnvironmentInfo(@Body TreeEnvironmentInfoDTO treeEnvironmentInfo);

    // 수목 병해정보 수정
    @POST("app/tree/modifyTreePestInfo")
    Observable<Response<ResponseVO>> modifyTreePestInfo(@Body TreePestInfoDTO treePestInfo);



}
