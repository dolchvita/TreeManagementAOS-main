package com.snd.app.ui.management;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.repository.tree.TreeUseCase;

import javax.inject.Inject;

public class TreeManagementMenuViewModel extends TMViewModel {
    public MutableLiveData<String> click = new MutableLiveData<>();

    public MutableLiveData<String> Authorization = new MutableLiveData<>();
    public MutableLiveData<String> idHex = new MutableLiveData<>();

    @Inject
    TreeUseCase treeUseCase;


    public TreeManagementMenuViewModel() {
        treeUseCase = getAppComponent().treeUseCase();
    }



    /* ------------------------------------------------- READ METHODS ------------------------------------------------- */

    // 회원 통합 정보 조회
    public void getTreeInfoByNFCtagId(){
        treeUseCase.loadTreeInfoByNFCtagId(Authorization.getValue(), idHex.getValue());
    }


    /* ------------------------------------------------- EXCEPTION ------------------------------------------------- */

    // NFC 권한 체크
    public LiveData<String> failCheck(){
        return treeUseCase.failCheck();
    }




    public void setClick(){
        click.setValue("click");
    }


}
