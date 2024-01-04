package com.snd.app.ui.business;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.repository.project.ProjectUseCase;

import java.util.List;

import javax.inject.Inject;

public class ProjectViewModel extends TMViewModel {
    public MutableLiveData<String> Authorization=new MutableLiveData<>();

    @Inject
    ProjectUseCase projectUseCase;

    public ProjectViewModel() {
        projectUseCase = getAppComponent().projectUseCase();
    }



    /* ------------------------------------------------- READ METHODS------------------------------------------------- */

    // 사업 이력조회
    public void getCompanyBusinessList(String authorization, String projectName){
        Log.d(TAG, ""+projectName);
        projectUseCase.getCompanyBusinessList(authorization, projectName);
    }
    public LiveData<List<Object>> getManagementList() {
        return projectUseCase.getManagementList();
    }



}
