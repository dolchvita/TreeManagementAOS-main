package com.snd.app.ui.business;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.repository.company.CompanyUseCase;

import java.util.List;

import javax.inject.Inject;

public class BusinessViewModel extends TMViewModel {
    public MutableLiveData<String> Authorization=new MutableLiveData<>();

    @Inject
    CompanyUseCase companyUseCase;

    public BusinessViewModel() {
        companyUseCase = getAppComponent().companyUseCase();
    }


    /* ------------------------------------------------- READ METHODS------------------------------------------------- */

    // 특정 업체 진행중 사업객체 리스트
    public void getCompanyBusinessList(String authorization, String companyName){
        companyUseCase.getCompanyBusinessList(authorization, companyName);
    }
    public LiveData<List<Object>> getProjectList() {
        return companyUseCase.getProjectList();
    }




}
