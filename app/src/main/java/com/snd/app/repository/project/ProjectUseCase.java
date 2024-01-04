package com.snd.app.repository.project;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ProjectUseCase {
    public String TAG = this.getClass().getName();
    ProjectRepository projectRepository;


    public ProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }



    /* ------------------------------------------------- READ METHODS------------------------------------------------- */

    // 사업 이력조회
    public void getCompanyBusinessList(String authorization, String projectName){
        Log.d(TAG, ""+projectName);
        projectRepository.getProjectManagementHistoryList(authorization, projectName);
    }
    public LiveData<List<Object>> getManagementList() {
        return projectRepository.managementList;
    }



}
