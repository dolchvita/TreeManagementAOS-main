package com.snd.app.ui.read;


import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.dto.TreeEnvironmentInfoDTO;
import com.snd.app.domain.tree.vo.TreeIntegratedVO;
import com.snd.app.repository.tree.TreeUseCase;
import com.snd.app.repository.tree.treeDataList.TreeDataListUseCase;

import java.util.List;

import javax.inject.Inject;


public class GetEnvironmentViewModel extends TMViewModel {
    String Authorization;
    TreeIntegratedVO treeIntegratedVO;
    public TreeEnvironmentInfoDTO treeEnvironmentInfoDTO;

    public ObservableField<String> nfc = new ObservableField<>();
    public ObservableField<String> frameHorizontal = new ObservableField<>();
    public ObservableField<String> frameVertical = new ObservableField<>();
    public ObservableField<String> frameMaterial = new ObservableField<>();
    public ObservableField<String> boundaryStone = new ObservableField<>();
    public ObservableField<String> roadWidth = new ObservableField<>();
    public ObservableField<String> sideWalkWidth = new ObservableField<>();
    public ObservableField<String> packingMaterial = new ObservableField<>();
    public ObservableField<String> soilPH = new ObservableField<>();
    public ObservableField<String> soilDensity = new ObservableField<>();
    /* 수정시 적용될 데이터 */
    public MutableLiveData<String> modifyProcess = new MutableLiveData<>();
    public MutableLiveData<Boolean> readOrEdit = new MutableLiveData<>();
    public MutableLiveData<Boolean> cancel = new MutableLiveData<>();
    boolean flag = false;

    @Inject
    TreeDataListUseCase treeDataListUseCase;
    @Inject
    TreeUseCase treeUseCase;


    public GetEnvironmentViewModel() {
        treeEnvironmentInfoDTO = new TreeEnvironmentInfoDTO();
        treeDataListUseCase = getAppComponent().treeDataListUseCase();
        treeUseCase = getAppComponent().treeUseCase();
    }


    /* --------------------------------------------- READ MEHTOD --------------------------------------------- */

    public void loadFrameMaterialList(String Authorization){
        treeDataListUseCase.loadFrameMaterialList(Authorization);
    }

    public LiveData<List<String>> getFrameMaterialList(){
        return  treeDataListUseCase.getFrameMaterialList();
    }

    public void loadPackingMaterialList(String Authorization){
        treeDataListUseCase.loadPackingMaterialList(Authorization);
    }

    public LiveData<List<String>> packingMaterialList(){
        return  treeDataListUseCase.packingMaterialList();
    }


    /* --------------------------------------------- UPDATE MEHTOD --------------------------------------------- */

    // 환경 정보 수정
    public void modifyEnvironmentInfo(){
        treeUseCase.loadEnvironmentInfo(Authorization, treeEnvironmentInfoDTO);
    }


    public LiveData<String> checkModifyEnvironmentInfo(){
        return  treeUseCase.checkModifyEnvironmentInfo();
    }


    // 환경 정보 등록 (체크)
    public LiveData<String> checkTreeEnvironmentInfo(){
        return  treeUseCase.checkTreeEnvironmentInfo();
    }


    // 저장 버튼
    public void save(){
        flag = !flag;
        readOrEdit.setValue(flag);
        if(!flag){
            modifyProcess.setValue(treeEnvironmentInfoDTO.getFrameMaterial() == null || treeEnvironmentInfoDTO.getPackingMaterial() == null ? "none" : "insert");
        }
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

    public void onFrameMaterialItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        if(!item.equals("직접 입력")){
            treeEnvironmentInfoDTO.setFrameMaterial(item);
        }
        if(!item.equals("없음") && !item.equals("직접 입력")){
            frameMaterial.set(item);
        }
    }


    // 경계석 스피너 선택
    public void onBoundaryStoneItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String)parent.getItemAtPosition(position);
        if(item.equals("있음 O")){
            treeEnvironmentInfoDTO.setBoundaryStone(true);
        } else if (item.equals("없음 X")) {
            treeEnvironmentInfoDTO.setBoundaryStone(false);
        }
    }


    // 포장재 스피너 선택
    public void onPackingMaterialItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        if(!item.equals("직접 입력")){
            treeEnvironmentInfoDTO.setPackingMaterial(item);
        }
        if(!item.equals("없음") && !item.equals("직접 입력")){
            packingMaterial.set(item);
        }
    }


    /* --------------------------------------------- EditText --------------------------------------------- */

    // 화면에 데이터 조회
    public void setTextViewModel(TreeIntegratedVO treeIntegratedVO){
        this.treeIntegratedVO = treeIntegratedVO;
        loadFrameMaterialList(Authorization);
        loadPackingMaterialList(Authorization);
        nfc.set(treeIntegratedVO.getNfc());
        setValueOrEmpty(treeIntegratedVO.getFrameHorizontal(), frameHorizontal);
        setValueOrEmpty(treeIntegratedVO.getFrameVertical(), frameVertical);
        setValueOrEmpty(treeIntegratedVO.getBoundaryStone(), boundaryStone);
        if(treeIntegratedVO.getBoundaryStone() != null){
            boundaryStone.set((treeIntegratedVO.getBoundaryStone()) ? "있음 O" : "없음 X");
        }
        setValueOrEmpty(treeIntegratedVO.getRoadWidth(), roadWidth);
        setValueOrEmpty(treeIntegratedVO.getSideWalkWidth(), sideWalkWidth);
        setValueOrEmpty(treeIntegratedVO.getSoilPH(), soilPH);
        setValueOrEmpty(treeIntegratedVO.getSoilDensity(), soilDensity);
        setValueOrEmpty(treeIntegratedVO.getFrameMaterial(), frameMaterial);    // 보호틀  -> 텍스트 필드에 매핑됨
        setValueOrEmpty(treeIntegratedVO.getPackingMaterial(), packingMaterial);    // 포장재 -> 텍스트 필드에 매핑됨
        mappingDTO();
    }


    private <T> void setValueOrEmpty(T value, ObservableField<String> target) {
        target.set(value != null ? value.toString() : "");
    }


    public void mappingDTO(){
        if(treeEnvironmentInfoDTO == null) {
            treeEnvironmentInfoDTO = new TreeEnvironmentInfoDTO();
        }
        treeEnvironmentInfoDTO.setNfc(treeIntegratedVO.getNfc());
        if(treeIntegratedVO.getBoundaryStone() != null){
            treeEnvironmentInfoDTO.setBoundaryStone(treeIntegratedVO.getBoundaryStone());   // 경계석 유무
        }
        treeEnvironmentInfoDTO.setSubmitter(treeIntegratedVO.getBasicSubmitter());
        treeEnvironmentInfoDTO.setVendor(treeIntegratedVO.getBasicVendor());
        treeEnvironmentInfoDTO.setFrameMaterial(treeIntegratedVO.getFrameMaterial());
        treeEnvironmentInfoDTO.setPackingMaterial(treeIntegratedVO.getPackingMaterial());
    }


    public void cancel(){
        cancel.setValue(flag);
    }


}