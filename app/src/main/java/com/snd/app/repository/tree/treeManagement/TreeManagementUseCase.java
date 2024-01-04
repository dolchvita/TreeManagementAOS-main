package com.snd.app.repository.tree.treeManagement;


import androidx.lifecycle.LiveData;

import com.snd.app.domain.treeManagement.TreeDefoliationDTO;
import com.snd.app.domain.treeManagement.TreeFertilizationDTO;
import com.snd.app.domain.treeManagement.TreeHorticultureDTO;
import com.snd.app.domain.treeManagement.TreeMiscellaneousDTO;
import com.snd.app.domain.treeManagement.TreePesticidesDTO;
import com.snd.app.domain.treeManagement.TreePruningDTO;
import com.snd.app.domain.treeManagement.TreeSurgeryDTO;

import java.util.List;

public class TreeManagementUseCase {
    public String TAG = this.getClass().getName();
    TreeManagementRepository treeManagementRepository;

    public TreeManagementUseCase(TreeManagementRepository treeManagementRepository) {
        this.treeManagementRepository = treeManagementRepository;
    }




    /* ------------------------------------------------- CREATE METHODS------------------------------------------------- */

    // 수목 가지치기 정보 등록
    public void registerPruning(String authorization, TreePruningDTO pruning){
        treeManagementRepository.registerPruning(authorization, pruning);
    }
    public LiveData<String> checkPruningInfo(){
        return  treeManagementRepository.insertTreePruning;
    }


    // 수목 맹아제거 정보 등록
    public void registerDefoliation(String authorization, TreeDefoliationDTO defoliation){
        treeManagementRepository.registerDefoliation(authorization, defoliation);
    }
    public LiveData<String> checkTreeDefoliation(){
        return  treeManagementRepository.insertTreeDefoliation;
    }


    // 수목 병충해방제 정보 등록
    public void registerPesticides(String authorization, TreePesticidesDTO pesticides){
        treeManagementRepository.registerPesticides(authorization, pesticides);
    }
    public LiveData<String> checkTreePesticides(){
        return  treeManagementRepository.insertTreePesticides;
    }


    // 수목 외과수술 정보 등록
    public void registerSurgery(String authorization, TreeSurgeryDTO surgery){
        treeManagementRepository.registerSurgery(authorization, surgery);
    }
    public LiveData<String> checkTreeSurgery(){
        return  treeManagementRepository.insertTreeSurgery;
    }


    // 수목 생육환경개선 정보 등록
    public void registerHorticulture(String authorization, TreeHorticultureDTO horticulture){
        treeManagementRepository.registerHorticulture(authorization, horticulture);
    }
    public LiveData<String> checkTreeHorticulture(){
        return  treeManagementRepository.insertTreeHorticulture;
    }


    // 수목 비료주기 정보 등록
    public void registerFertilization(String authorization, TreeFertilizationDTO fertilization){
        treeManagementRepository.registerFertilization(authorization, fertilization);
    }
    public LiveData<String> checkTreeFertilization(){
        return  treeManagementRepository.insertTreeFertilization;
    }


    // 수목 기타관리 정보 등록
    public void registerMiscellaneous(String authorization, TreeMiscellaneousDTO miscellaneous){
        treeManagementRepository.registerMiscellaneous(authorization, miscellaneous);
    }
    public LiveData<String> checkTreeMiscellaneous(){
        return  treeManagementRepository.insertTreeMiscellaneous;
    }




    /* -----------------------------------------------READ METHODS----------------------------------------------- */

    // 수목 관리이력 통합리스트
    public void getManagementHistoryListAll(String Authorization, String tagId){
        treeManagementRepository.getManagementHistoryListAll(Authorization, tagId);
    }
    public LiveData<List<Object>> getManagementList(){
        return  treeManagementRepository.managementList;
    }


}
