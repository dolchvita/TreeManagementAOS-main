package com.snd.app.repository.company;

import com.snd.app.domain.tree.vo.ResponseVO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CompanyService {



    /* -------------------------------------------READ METHODS------------------------------------------- */

    // 업체명 리스트
    @GET("app/company/info/name/list/all")
    Call<ResponseVO> getCompanyNameListAll();


    // 특정 업체 진행중 사업명 리스트
    @GET("app/company/info/businessName/list/{companyName}")
    Call<ResponseVO> getCompanyBusinessNameList(@Path("companyName") String companyName);


    // 특정 업체 진행중 사업객체 리스트
    @GET("app/company/info/business/list/{companyName}")
    Call<ResponseVO> getCompanyBusinessList(@Path("companyName") String companyName);



}
