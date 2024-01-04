package com.snd.app.repository.project;

import com.snd.app.domain.tree.vo.ResponseVO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProjectService {


    /* -------------------------------------------READ METHODS------------------------------------------- */

    // 사업 이력조회
    @GET("app/projects/info/project/list/{projectName}")
    Call<ResponseVO> getProjectManagementHistoryList(@Path("projectName") String projectName);


}
