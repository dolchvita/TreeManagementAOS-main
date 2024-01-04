package com.snd.app.ui.map;


import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.vo.TreeIntegratedVO;
import com.snd.app.repository.tree.TreeUseCase;
import com.snd.app.repository.tree.treeImage.TreeImageUseCase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

public class TreeSpecificInfoViewModel extends TMViewModel {
    public ObservableField<String> NFC = new ObservableField<>();
    public ObservableField<String> species = new ObservableField<>();
    public ObservableField<String> submitter = new ObservableField<>();
    public ObservableField<String> vendor = new ObservableField<>();
    public ObservableField<String> basicInserted = new ObservableField<>();
    public MutableLiveData<String> idHex = new MutableLiveData<>();
    public MutableLiveData<String> Authorization = new MutableLiveData<>();
    public MutableLiveData<String> back = new MutableLiveData<>();
    @Inject
    TreeUseCase treeUseCase;
    @Inject
    TreeImageUseCase treeImageUseCase;


    public TreeSpecificInfoViewModel() {
        treeUseCase = getAppComponent().treeUseCase();
        treeImageUseCase = getAppComponent().treeImageUseCase();
    }


    /* ------------------------------------------------- READ METHODS------------------------------------------------- */

    public void loadTreeInfoByNFCtadId(String Authorization, String idHex){
        treeUseCase.loadTreeInfoByNFCtagId(Authorization, idHex);
    }
    public LiveData<TreeIntegratedVO> getTreeIntegratedVO(){
        return  treeUseCase.getTreeInfoByNFCtagId();
    }


    // 수목 저장된 파일명 리스트 반환
    public void loadTreeImageFilenameList(String Authorization, String tagId){
        treeImageUseCase.loadTreeImageFilenameList(Authorization, tagId);
    }
    public LiveData<List<String>> getTreeImageFilenameList(){
        return treeImageUseCase.getTreeImageFilenameList();
    }


    public void setTextViewModel(TreeIntegratedVO treeIntegratedVO){
        NFC.set(treeIntegratedVO.getNfc());
        species.set(treeIntegratedVO.getSpecies());
        submitter.set(treeIntegratedVO.getBasicSubmitter());
        vendor.set(treeIntegratedVO.getBasicVendor());
        basicInserted.set(getBasicInserted(treeIntegratedVO.getBasicInserted()));
    }


    public String getBasicInserted(List<Double> dateList) {
        String formatDateTime = null;
        if (dateList != null && dateList.size() == 6) {
            LocalDateTime dateTime = LocalDateTime.of(
                    dateList.get(0).intValue(),
                    dateList.get(1).intValue(),
                    dateList.get(2).intValue(),
                    dateList.get(3).intValue(),
                    dateList.get(4).intValue(),
                    dateList.get(5).intValue());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            formatDateTime = dateTime.format(formatter);
        } else {
            formatDateTime = "";
        }
        return formatDateTime;
    }


    public void setBack(){
        back.setValue("back");
    }


}
