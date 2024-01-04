package com.snd.app.repository.tree.treeManagement;

import com.snd.app.domain.tree.vo.ResponseVO;
import com.snd.app.domain.treeManagement.TreeDefoliationDTO;
import com.snd.app.domain.treeManagement.TreeFertilizationDTO;
import com.snd.app.domain.treeManagement.TreeHorticultureDTO;
import com.snd.app.domain.treeManagement.TreeMiscellaneousDTO;
import com.snd.app.domain.treeManagement.TreePesticidesDTO;
import com.snd.app.domain.treeManagement.TreePruningDTO;
import com.snd.app.domain.treeManagement.TreeSurgeryDTO;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TreeManagementService {


    /* -----------------------------------------------CREATE METHODS----------------------------------------------- */

    // 수목 가지치기 정보 등록
    @POST("app/tree/manage/registerPruning")
    Observable<Response<ResponseVO>> registerPruning(@Body TreePruningDTO pruning);


    // 수목 맹아제거 정보 등록
    @POST("app/tree/manage/registerDefoliation")
    Observable<Response<ResponseVO>> registerDefoliation(@Body TreeDefoliationDTO defoliation);


    // 수목 병충해방제 정보 등록
    @POST("app/tree/manage/registerPesticides")
    Observable<Response<ResponseVO>> registerPesticides(@Body TreePesticidesDTO pesticides);


    // 수목 외과수술 정보 등록
    @POST("app/tree/manage/registerSurgery")
    Observable<Response<ResponseVO>> registerSurgery(@Body TreeSurgeryDTO surgery);


    // 수목 생육환경개선 정보 등록
    @POST("app/tree/manage/registerHorticulture")
    Observable<Response<ResponseVO>> registerHorticulture(@Body TreeHorticultureDTO horticulture);


    // 수목 비료주기 정보 등록
    @POST("app/tree/manage/registerFertilization")
    Observable<Response<ResponseVO>> registerFertilization(@Body TreeFertilizationDTO fertilization);


    // 수목 기타관리정보 정보 등록
    @POST("app/tree/manage/registerMiscellaneous")
    Observable<Response<ResponseVO>> registerMiscellaneous(@Body TreeMiscellaneousDTO miscellaneous);




    /* ----------------------------------------------READ METHODS----------------------------------------------- */

    // 수목 관리이력 통합리스트 by NFC tagId
    @GET("app/tree/manage/getManagementHistoryList/{tagId}")
    Call<ResponseVO> getManagementHistoryListAll(@Path("tagId") String tagId);


    /*
    // RxJava
    @GET("app/tree/manage/getManagementHistoryList/{tagId}")
    Single<ResponseVO> getManagementHistoryListAll(String tagId);
     */




}
