package com.snd.app.ui.write.tree;

import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.dto.TreeStatusInfoDTO;
import com.snd.app.repository.tree.TreeUseCase;


import javax.inject.Inject;

public class RegistTreeStatusInfoViewModel extends TMViewModel {
    public ObservableField<String> NFC=new ObservableField<>();
    public MutableLiveData<Boolean> pestProccess = new MutableLiveData<>();
    public MutableLiveData<String> insertProcess = new MutableLiveData<>();
    public MutableLiveData<String> setCreation = new MutableLiveData<>();

    public String Authorization;
    TreeStatusInfoDTO treeStatusInfoDTO;
    @Inject
    TreeUseCase treeUseCase;


    public RegistTreeStatusInfoViewModel() {
        treeUseCase=getAppComponent().treeUseCase();
    }


    private MutableLiveData _back = new MutableLiveData<>();
    public LiveData back = getBack();


    // 화면에 표시되는 메서드
    public void setTextViewModel(TreeStatusInfoDTO treeStatusInfoDTO){
        this.treeStatusInfoDTO=treeStatusInfoDTO;
        NFC.set(treeStatusInfoDTO.getNfc());
    }


    /* --------------------------------------------- CREATE MEHTOD --------------------------------------------- */

    // 수목 상태 정보 등록
    public void registerTreeStatusInfo(){
        treeUseCase.loadStatusInfo(Authorization, treeStatusInfoDTO);
    }
    public LiveData<String> checkStatusInfo(){
        return  treeUseCase.checkStatusInfo();
    }


    /* ---------------------------------------------Spinner--------------------------------------------- */

    // 1-1) 스피너 선택
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        if(item.equals("있음 O")){
            treeStatusInfoDTO.setPest(true);
            pestProccess.setValue(true);
        }else {
            treeStatusInfoDTO.setPest(false);
            pestProccess.setValue(false);
        }
    }


    // 1-1) 스피너 선택
    public void onCreationItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        setCreation.setValue(item);
    }


    // 저장 버튼
    public void save(){
        insertProcess.setValue("insert");
    }


    // 뒤로 가기
    public void setBack(){
        _back.setValue("goSpecificLocation");
    }
    public LiveData getBack(){
        return _back;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }


}
