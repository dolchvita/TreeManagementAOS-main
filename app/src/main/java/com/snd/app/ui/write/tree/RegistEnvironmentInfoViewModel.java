package com.snd.app.ui.write.tree;


import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.dto.TreeEnvironmentInfoDTO;
import com.snd.app.repository.tree.TreeUseCase;
import com.snd.app.repository.tree.treeDataList.TreeDataListUseCase;

import java.util.List;

import javax.inject.Inject;


public class RegistEnvironmentInfoViewModel extends TMViewModel {
    public ObservableField<String> NFC = new ObservableField<>();
    TreeEnvironmentInfoDTO treeEnvironmentInfoDTO;
    public MutableLiveData back = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEditText = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> packingEditText = new MutableLiveData<>(false);
    public MutableLiveData<String> insertProcess = new MutableLiveData<>();
    public String Authorization;

    @Inject
    TreeUseCase treeUseCase;
    @Inject
    TreeDataListUseCase treeDataListUseCase;


    public RegistEnvironmentInfoViewModel() {
        treeUseCase = getAppComponent().treeUseCase();
        treeDataListUseCase = getAppComponent().treeDataListUseCase();
    }


    // 화면에 표시되는 메서드
    public void setTextViewModel(TreeEnvironmentInfoDTO treeEnvironmentInfoDTO){
        this.treeEnvironmentInfoDTO = treeEnvironmentInfoDTO;
        loadFrameMaterialList(Authorization);
        loadPackingMaterialList(Authorization);
        NFC.set(treeEnvironmentInfoDTO.getNfc());
    }



    /* --------------------------------------------- READ MEHTOD --------------------------------------------- */

    // 보호틀 재질 리스트 조회
    public void loadFrameMaterialList(String Authorization){
        treeDataListUseCase.loadFrameMaterialList(Authorization);
    }
    public LiveData<List<String>> getFrameMaterialList(){
        return  treeDataListUseCase.getFrameMaterialList();
    }

    // 포장재 재질 리스트 조회
    public void loadPackingMaterialList(String Authorization){
        treeDataListUseCase.loadPackingMaterialList(Authorization);
    }
    public LiveData<List<String>> packingMaterialList(){
        return  treeDataListUseCase.packingMaterialList();
    }



    /* --------------------------------------------- CREATE MEHTOD --------------------------------------------- */

    public void registerTreeEnvironmentInfo(){
        treeUseCase.loadTreeEnvironmentInfo(Authorization, treeEnvironmentInfoDTO);
    }
    public LiveData<String> checkTreeEnvironmentInfo(){
        return  treeUseCase.checkTreeEnvironmentInfo();
    }



    /* --------------------------------------------- Spinner --------------------------------------------- */

    // 보호틀 스피너 선택
    public void onFrameMaterialItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        showEditText.setValue("직접 입력".equals(item));
        if(!item.equals("직접 입력")){
            treeEnvironmentInfoDTO.setFrameMaterial(item);
        }
    }


    // 경계석 스피너 선택
    public void onBoundaryStoneItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String)parent.getItemAtPosition(position);
        treeEnvironmentInfoDTO.setBoundaryStone(item.equals("있음 O") ? true : false);
    }


    // 포장재 스피너 선택
    public void onPackingMaterialItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        packingEditText.setValue("직접 입력".equals(item));
        if(!item.equals("직접 입력")){
            treeEnvironmentInfoDTO.setPackingMaterial(item);
        }
    }


    public void save(){
        insertProcess.setValue(treeEnvironmentInfoDTO.getFrameMaterial() == null || treeEnvironmentInfoDTO.getPackingMaterial() == null ? "none" : "insert");
    }


    public void setBack(){
        back.setValue("click");
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }


}
