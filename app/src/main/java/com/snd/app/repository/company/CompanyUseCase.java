package com.snd.app.repository.company;


import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CompanyUseCase {
    public String TAG = this.getClass().getName();
    CompanyRepository companyRepository;


    public CompanyUseCase(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }



    /* ------------------------------------------------- READ METHODS------------------------------------------------- */

    // 업체명 리스트 가져오기
    public void loadCompanyNameList(){
        companyRepository.getCompanyNameListAll();
    }
    public LiveData<List<String>> getCompanyNameListAll() {
        return companyRepository.getCompanyNameList();
    }


    // 특정 업체 진행중 사업명 리스트
    public void loadCompanyBusinessNameList(String authorization, String companyName){
        Log.d(TAG, ""+companyName);
        companyRepository.getCompanyBusinessNameList(authorization, companyName);
    }
    public LiveData<List<String>> getCompanyBusinessNameList() {
        return companyRepository.projectNameList;
    }


    // 특정 업체 진행중 사업객체 리스트
    public void getCompanyBusinessList(String authorization, String companyName){
        Log.d(TAG, ""+companyName);
        companyRepository.getCompanyBusinessList(authorization, companyName);
    }
    public LiveData<List<Object>> getProjectList() {
        return companyRepository.projectList;
    }



}
