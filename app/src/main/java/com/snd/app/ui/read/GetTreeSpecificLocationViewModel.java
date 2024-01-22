package com.snd.app.ui.read;

import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.dto.TreeLocationInfoDTO;
import com.snd.app.domain.tree.vo.TreeIntegratedVO;
import com.snd.app.repository.tree.TreeUseCase;

import net.daum.mf.map.api.MapPoint;

import javax.inject.Inject;


public class GetTreeSpecificLocationViewModel extends TMViewModel {
    String Authorization;
    public ObservableField<String> NFC = new ObservableField<>();
    public ObservableField<String> carriageway = new ObservableField<>();
    public ObservableField<String> distance = new ObservableField<>();
    public ObservableField<String> sidewalk = new ObservableField<>();
    public MutableLiveData<String> back = new MutableLiveData<>();
    public MutableLiveData<MapPoint.GeoCoordinate> modifyLocation = new MutableLiveData<>();
    public MutableLiveData<String> mapType = new MutableLiveData<>();

    public MutableLiveData<Boolean> readOrEdit = new MutableLiveData<>();
    public MutableLiveData<Boolean> cancel = new MutableLiveData<>();
    boolean flag = false;   // 이 변수 역할 써놓기

    TreeIntegratedVO treeIntegratedVO;
    TreeLocationInfoDTO treeLocationInfoDTO;

    @Inject
    TreeUseCase treeUseCase;


    // Constructor
    public GetTreeSpecificLocationViewModel() {
        treeUseCase = getAppComponent().treeUseCase();
        treeLocationInfoDTO = new TreeLocationInfoDTO();
    }


    /* ------------------------------------------------- UPDATE METHODS ------------------------------------------------- */

    // 위치 수정 버튼 클릭
    public void modifyLocation(){
        MapPoint.GeoCoordinate coordinate = new MapPoint.GeoCoordinate(treeLocationInfoDTO.getLatitude(), treeLocationInfoDTO.getLongitude());
        modifyLocation.setValue(coordinate);
    }

    // DTO 수정
    public void modifyLocationInfo(){
        log("수정되기 전 디티오 확인" + treeLocationInfoDTO);
        treeUseCase.modifyLocationInfo(Authorization, treeLocationInfoDTO);
    }


    public LiveData<String> checkModifyLocationInfo(){
        return  treeUseCase.checkModifyLocationInfo();
    }


    public void onSidewalkItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        if(item.equals("있음 O")){
            treeLocationInfoDTO.setSidewalk(true);
        } else if (item.equals("없음 X")) {
            treeLocationInfoDTO.setSidewalk(false);
        }
    }


    public void save(){
        flag = !flag;
        if(readOrEdit != null){
            readOrEdit.setValue(flag);
        }
    }


    public void cancel(){
        cancel.setValue(flag);
    }


    public void setTextView(TreeIntegratedVO treeIntegratedVO){
        this.treeIntegratedVO = treeIntegratedVO;
        NFC.set(treeIntegratedVO.getNfc());
        setValueOrEmpty(treeIntegratedVO.getCarriageway(), carriageway);
        if(treeIntegratedVO.getSidewalk() != null){
            sidewalk.set((treeIntegratedVO.getSidewalk()) ? "있음 O" : "없음 X");
        }
        setValueOrEmpty(treeIntegratedVO.getDistance(), distance);
        mappingDTO();
    }


    private <T> void setValueOrEmpty(T value, ObservableField<String> target) {
        target.set(value != null ? value.toString() : "");
    }


    public void setMapTypeToHybrid(){
        mapType.setValue("Hybrid");
    }
    public void setMapTypeToNormal(){
        mapType.setValue("normal");
    }


    public void mappingDTO(){
        if(treeLocationInfoDTO == null) {
            treeLocationInfoDTO = new TreeLocationInfoDTO();
        }
        treeLocationInfoDTO.setNfc(treeIntegratedVO.getNfc());
        if(treeIntegratedVO.getSidewalk() != null){
            treeLocationInfoDTO.setSidewalk(treeIntegratedVO.getSidewalk());    // 보도 유무
        }
        treeLocationInfoDTO.setLatitude(treeIntegratedVO.getLatitude());
        treeLocationInfoDTO.setLongitude(treeIntegratedVO.getLongitude());
        treeLocationInfoDTO.setSubmitter(treeIntegratedVO.getLocationSubmitter());
        treeLocationInfoDTO.setVendor(treeIntegratedVO.getLocationVendor());
    }


    public void setBack(){
        back.setValue("뒤로가기");
    }


}