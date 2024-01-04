package com.snd.app.ui.map;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.dto.TreeListRangeDTO;
import com.snd.app.repository.tree.TreeUseCase;

import java.util.List;
import javax.inject.Inject;


public class MapViewModel extends TMViewModel {
    public MutableLiveData<String> Authorization = new MutableLiveData<>();
    public MutableLiveData<String> mapType = new MutableLiveData<>();
    @Inject
    TreeUseCase treeUseCase;


    public MapViewModel() {
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


    /* ------------------------------------------------- EXCEPTION ------------------------------------------------- */

    // 데이터 여부 체크
    public LiveData<String> failCheck(){
        return treeUseCase.failCheck();
    }


    // 리팩토링 부분
    public void setMapTypeToHybrid(){
        mapType.setValue("Hybrid");
    }
    public void setMapTypeToNormal(){
        mapType.setValue("normal");
    }


}
