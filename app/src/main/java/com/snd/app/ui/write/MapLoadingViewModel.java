package com.snd.app.ui.write;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.dto.TreeCoordinateDTO;
import com.snd.app.domain.tree.dto.TreeListRangeDTO;
import com.snd.app.repository.tree.TreeUseCase;

import java.util.List;

import javax.inject.Inject;


public class MapLoadingViewModel extends TMViewModel {
    public MutableLiveData<String> _resultLiveData = new MutableLiveData<>();   //저장버튼
    public MutableLiveData<String> _back = new MutableLiveData<>();
    public ObservableField<String> accuracy = new ObservableField<>();
    public ObservableField<String> satellite = new ObservableField<>();
    public MutableLiveData<String> mapType = new MutableLiveData<>();
    public MutableLiveData<TreeCoordinateDTO> addMarker = new MutableLiveData<>();
    public MutableLiveData<Boolean> pinnedBt = new MutableLiveData<>();
    public MutableLiveData<String> Authorization = new MutableLiveData<>();
    public int objectSize;
    Boolean flag = false;

    @Inject
    TreeUseCase treeUseCase;


    // constructor
    public MapLoadingViewModel() {
        treeUseCase = getAppComponent().treeUseCase();
    }


    /* --------------------------------------------- READ MEHTOD --------------------------------------------- */

    // 범위내의 수목 통합 정보 리스트 가져오기
    public void loadTreeInfoListByRange(TreeListRangeDTO rangeInfo) {
        treeUseCase.loadTreeInfoListByRange(Authorization.getValue(), rangeInfo);
    }
    public LiveData<List<Object>> getTreeInfoListByRange(){
        return  treeUseCase.getTreeInfoListByRange();
    }


    // 위치 고정 버튼을 누르면..
    public void setPinnedBt(){
        flag = !flag;
        pinnedBt.setValue(flag);
    }


    public void save(){
        _resultLiveData.setValue("set");
    }
    public void setMapTypeToHybrid(){
        mapType.setValue("Hybrid");
    }
    public void setMapTypeToNormal(){
        mapType.setValue("normal");
    }
    public void setBack(){
        _back.setValue("click");
    }



}
