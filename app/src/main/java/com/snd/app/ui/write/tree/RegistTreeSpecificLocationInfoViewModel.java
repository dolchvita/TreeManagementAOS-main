package com.snd.app.ui.write.tree;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.dto.TreeSpecificLocationInfoDTO;
import com.snd.app.repository.tree.TreeUseCase;
import javax.inject.Inject;


public class RegistTreeSpecificLocationInfoViewModel extends TMViewModel {
    private MutableLiveData _back = new MutableLiveData();
    public LiveData back=getBack();
    public ObservableField<String> NFC=new ObservableField<>();
    public MutableLiveData<String> distance=new MutableLiveData<>();
    public MutableLiveData<String> carriageway=new MutableLiveData<>();
    public MutableLiveData<String> insertProcess = new MutableLiveData<>();
    public String Authorization;
    TreeSpecificLocationInfoDTO treeSpecificLocationInfoDTO;
    //TreeLocationInfoDTO treeLocationInfoDTO;
    @Inject
    TreeUseCase treeUseCase;



    /* Constructor */
    public RegistTreeSpecificLocationInfoViewModel() {
        treeUseCase = getAppComponent().treeUseCase();
    }

    public void setBack(){
        _back.setValue("goBasic");
    }
    public LiveData getBack(){
        return _back;
    }


    // 화면에 표시되는 메서드 - NFC 고유 번호 - 이게
    public void setTextViewModel(TreeSpecificLocationInfoDTO treeSpecificLocationInfoDTO){
        this.treeSpecificLocationInfoDTO = treeSpecificLocationInfoDTO;
        NFC.set(treeSpecificLocationInfoDTO.getNfc());
    }


    // 저장 버튼
    public void save(){
        insertProcess.setValue("insert");
    }


    public void loadSpecificLocationInfo(){
        Log.d(TAG, "저장될 위치 객체 ** " + treeSpecificLocationInfoDTO);
        treeUseCase.loadSpecificLocationInfo(Authorization, treeSpecificLocationInfoDTO);
    }


    public LiveData<String> checkSpecificLocationInfo(){
        return treeUseCase.checkSpecificLocationInfo();
    }



}
