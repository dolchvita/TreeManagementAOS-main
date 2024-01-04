package com.snd.app.ui.write.pest;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.dto.TreePestInfoDTO;
import com.snd.app.repository.tree.TreeUseCase;
import com.snd.app.repository.tree.treeDataList.TreeDataListUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RegistTreePestInfoViewModel extends TMViewModel {
    public MutableLiveData<Boolean> addPestInfoBox = new MutableLiveData<>();
    public MutableLiveData<String> Authorization = new MutableLiveData<>();
    List<TreePestInfoDTO> treePestInfoList = new ArrayList<>();
    public MutableLiveData<String> back = new MutableLiveData<>();
    public MutableLiveData<String> insertProcess = new MutableLiveData<>();
    public MutableLiveData<String> result = new MutableLiveData<>();

    @Inject
    TreeDataListUseCase treeDataListUseCase;
    @Inject
    TreeUseCase treeUseCase;


    public RegistTreePestInfoViewModel() {
        treeDataListUseCase = getAppComponent().treeDataListUseCase();
        treeUseCase = getAppComponent().treeUseCase();
    }


    // 추가 버튼 이벤트
    public void addPestInfoBox(){
        addPestInfoBox.setValue(true);
    }



    /* ------------------------------------------------- READ METHODS ------------------------------------------------- */

    public void loadPestNameList(){
        treeDataListUseCase.loadPestNameList(Authorization.getValue());
    }
    public LiveData<List<String>> getTreePestNameLists() {
        return treeDataListUseCase.getTreePestNameLists();
    }


    /* ------------------------------------------------- CREATE METHODS ------------------------------------------------- */

    // 저장 버튼 이벤트
    public void save(){
        insertProcess.setValue("click");
    }


    public void registerTreePestInfo(){
        Log.d(TAG, "보내기 전 최종 점검 " + treePestInfoList);
        treeUseCase.registerTreePestInfo(Authorization.getValue(), treePestInfoList);
    }
    public LiveData<String> checkTreePestInfo(){
        return  treeUseCase.checkTreePestInfo();
    }


    // 취소 버튼
    public void setBack(){
        back.setValue("");
    }



}
