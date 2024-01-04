package com.snd.app.ui.read;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.repository.tree.treeManagement.TreeManagementUseCase;

import java.util.List;

import javax.inject.Inject;

public class GetManagementHistoryViewModel extends TMViewModel {
    @Inject
    TreeManagementUseCase treeManagementUseCase;
    public MutableLiveData<String> Authorization = new MutableLiveData<>();


    public GetManagementHistoryViewModel() {
        treeManagementUseCase = getAppComponent().treeManagementUseCase();
    }



    /* -----------------------------------------------READ METHODS----------------------------------------------- */

    // 수목 관리이력 통합리스트
    public void getManagementHistoryListAll(String Authorization, String tagId){
        treeManagementUseCase.getManagementHistoryListAll(Authorization, tagId);
    }
    public LiveData<List<Object>> getManagementList(){
        return  treeManagementUseCase.getManagementList();
    }



}
