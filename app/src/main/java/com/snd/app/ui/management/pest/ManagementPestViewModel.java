package com.snd.app.ui.management.pest;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.dto.TreePestInfoDTO;
import com.snd.app.repository.tree.TreeUseCase;
import com.snd.app.repository.tree.treeDataList.TreeDataListUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class ManagementPestViewModel extends TMViewModel {
    public ObservableField<String> pest = new ObservableField<>();
    public ObservableField<String> occurred = new ObservableField<>();
    public ObservableField<String> recovered = new ObservableField<>();
    MutableLiveData<String> Authorization = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEditText = new MutableLiveData<>(false);
    public MutableLiveData<String> datePicker = new MutableLiveData<>();
    public MutableLiveData<String> back=new MutableLiveData<>();

    TreePestInfoDTO treePestInfoDTO = new TreePestInfoDTO();
    List<TreePestInfoDTO> treePestInfoList;

    @Inject
    TreeUseCase treeUseCase;
    @Inject
    TreeDataListUseCase treeDataListUseCase;


    public ManagementPestViewModel() {
        treeUseCase = getAppComponent().treeUseCase();
        treeDataListUseCase = getAppComponent().treeDataListUseCase();
    }



    /* ------------------------------------------------- READ METHODS ------------------------------------------------- */

    // 수목 병해 정보 가져오기 by NFC tagId
    public void getTreePetsInfoByNFCtagId(String Authorization, String tagId){
        treeUseCase.getTreePetsInfoByNFCtagId(Authorization, tagId);
    }
    public LiveData<List<Object>> getPestInfoList(){
        return  treeUseCase.getPestInfoList();
    }


    // 수목 상태정보 - 병충해명 리스트 가져오기
    public void loadPestNameList(String Authorization){
        treeDataListUseCase.loadPestNameList(Authorization);
    }
    public LiveData<List<String>> getTreePestNameLists() {
        return treeDataListUseCase.getTreePestNameLists();
    }



    /* ------------------------------------------------- CREATE METHODS ------------------------------------------------- */

    // 수목 병해 정보 등록
    public void registerTreePestInfo(TreePestInfoDTO treePestInfoDTO){
        treePestInfoList = new ArrayList<>();
        treePestInfoList.add(treePestInfoDTO);
        treeUseCase.registerTreePestInfo(Authorization.getValue(), treePestInfoList);
    }
    public LiveData<String> checkTreePestInfo(){
        return  treeUseCase.checkTreePestInfo();
    }



    /* ------------------------------------------------- UPDATE METHODS ------------------------------------------------- */

    // 수목 병해정보 수정
    public void modifyTreePestInfo(String authorization, TreePestInfoDTO treePestInfo){
        treeUseCase.modifyTreePestInfo(authorization, treePestInfo);
    }
    public LiveData<String> checkModifyPestInfo(){
        return  treeUseCase.checkModifyPestInfo();
    }



    /* ------------------------------------------------- EXCEPTION ------------------------------------------------- */

    public LiveData<String> failCheck(){
        return treeUseCase.failCheck();
    }



    /* ------------------------------------------------- Spinner ------------------------------------------------- */

    // 병충해명 스피너 선택
    public void onPestItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        showEditText.setValue("직접 입력".equals(item));
        if(!item.equals("직접 입력")){
            treePestInfoDTO.setPest(item);
            pest.set(item);
        }
    }


    // 심각도 스피너 선택 - 기본값(낮음)이 있으므로 가장 먼저 호출됨..
    public void onSeverityItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);

        if(item.equals("완치 (일 선택)")){
            Log.d(TAG, "완치시 " + treePestInfoDTO); // 로그 추가
            treePestInfoDTO.setSeverity("완치");
            datePicker.setValue("recovered");
            showEditText.setValue("완치 (일 선택)".equals(item));

        }else {
            Log.d(TAG, "심각도 1 " + item); // 로그 추가
           if(treePestInfoDTO != null){
               treePestInfoDTO.setSeverity(item);
           }
        }
    }


    public void back(){
        back.setValue("click");
    }


    public void onDatePick(){
        datePicker.setValue("occurred");
    }


}
