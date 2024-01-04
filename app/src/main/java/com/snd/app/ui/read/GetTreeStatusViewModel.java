package com.snd.app.ui.read;

import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.data.dataUtil.FormatDataTime;
import com.snd.app.domain.tree.dto.TreeStatusInfoDTO;
import com.snd.app.domain.tree.vo.TreeIntegratedVO;
import com.snd.app.repository.tree.TreeUseCase;

import javax.inject.Inject;

public class GetTreeStatusViewModel extends TMViewModel {
    String Authorization;

    public ObservableField<String> NFC = new ObservableField<>();
    public ObservableField<String> DBH = new ObservableField<>();
    public ObservableField<String> RCC = new ObservableField<>();
    public ObservableField<String> height = new ObservableField<>();
    public ObservableField<String> length = new ObservableField<>();
    public ObservableField<String> width = new ObservableField<>();
    public ObservableField<String> pest = new ObservableField<>();
    public ObservableField<String> inserted = new ObservableField<>();
    public MutableLiveData<Boolean> insertProcess = new MutableLiveData<>(false);
    public MutableLiveData<String> setCreation = new MutableLiveData<>();

    public MutableLiveData<Boolean> readOrEdit = new MutableLiveData<>();
    public MutableLiveData<Boolean> cancel = new MutableLiveData<>();
    boolean flag = false;

    TreeIntegratedVO treeIntegratedVO;
    TreeStatusInfoDTO treeStatusInfoDTO;
    @Inject
    TreeUseCase treeUseCase;
    FormatDataTime formatDataTime;


    public GetTreeStatusViewModel() {
        treeStatusInfoDTO = new TreeStatusInfoDTO();
        treeUseCase = getAppComponent().treeUseCase();
        formatDataTime = new FormatDataTime();
    }


    /* --------------------------------------------- UPDATE MEHTOD --------------------------------------------- */

    // 수목 상태 정보 등록 (최초 수정시)
    public void modifyTreeStatusInfo(){
        if(insertProcess.getValue()){
            treeUseCase.loadStatusInfo(Authorization, treeStatusInfoDTO);
        }else {
            treeUseCase.modifyTreeStatusInfo(Authorization, treeStatusInfoDTO);
        }
    }


    public LiveData<String> checkStatusInfo(){
        return  treeUseCase.checkStatusInfo();
    }


    public void save(){
        flag = !flag;
        readOrEdit.setValue(flag);
    }


    public void cancel(){
        cancel.setValue(flag);
    }


    /* --------------------------------------------- EditText --------------------------------------------- */

    // 화면에 데이터 조회
    public void setTextViewModel(TreeIntegratedVO treeIntegratedVO){
        this.treeIntegratedVO = treeIntegratedVO;
        NFC.set(treeIntegratedVO.getNfc());
        setValueOrEmpty(treeIntegratedVO.getDbh(), DBH);
        setValueOrEmpty(treeIntegratedVO.getRcc(), RCC);
        setValueOrEmpty(treeIntegratedVO.getHeight(), height);
        setValueOrEmpty(treeIntegratedVO.getLength(), length);
        setValueOrEmpty(treeIntegratedVO.getWidth(), width);

        if(treeIntegratedVO.getPest() != null){
            pest.set((treeIntegratedVO.getPest() ? "있음O" : "없음X"));
        }
        if (treeIntegratedVO.getCreation() != null){    // 조성일자
            inserted.set(formatDataTime.localDateFormat(treeIntegratedVO.getCreation()));
            treeStatusInfoDTO.setCreation(formatDataTime.localDateFormat(treeIntegratedVO.getCreation()));
        }

        mappingDTO();
    }


    // 데이터가 없으면 공백으로 처리
    private <T> void setValueOrEmpty(T value, ObservableField<String> target) {
        target.set(value != null ? value.toString() : "");
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

    public void onCreationItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        setCreation.setValue(item);
    }


    public void mappingDTO(){
        if(treeStatusInfoDTO == null) {
            treeStatusInfoDTO = new TreeStatusInfoDTO();
        }
        treeStatusInfoDTO.setNfc(treeIntegratedVO.getNfc());
        treeStatusInfoDTO.setSubmitter(treeIntegratedVO.getBasicSubmitter());
        treeStatusInfoDTO.setVendor(treeIntegratedVO.getBasicVendor());
    }


}
