package com.snd.app.repository.tree;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.snd.app.data.dataUtil.DistanceCalculator;
import com.snd.app.domain.tree.dto.TreeBasicInfoDTO;
import com.snd.app.domain.tree.dto.TreeEnvironmentInfoDTO;
import com.snd.app.domain.tree.dto.TreeInitializingDTO;
import com.snd.app.domain.tree.dto.TreeListRangeDTO;
import com.snd.app.domain.tree.dto.TreePestInfoDTO;
import com.snd.app.domain.tree.dto.TreeSpecificLocationInfoDTO;
import com.snd.app.domain.tree.vo.TreeIntegratedVO;
import com.snd.app.domain.tree.dto.TreeLocationInfoDTO;
import com.snd.app.domain.tree.dto.TreeStatusInfoDTO;

import java.util.List;

public class TreeUseCase {
    public String TAG = this.getClass().getName();
    TreeRepository treeRepository;
    DistanceCalculator distanceCalculator;


    public TreeUseCase(TreeRepository treeRepository) {
        this.treeRepository = treeRepository;
        distanceCalculator = new DistanceCalculator();
    }


    /* ------------------------------------------------- CREATE METHODS ------------------------------------------------- */

    // 수목 기본 정보 등록
    public void loadTreeBasicInfo(String authorization, TreeInitializingDTO initializingTreeInfo){
        treeRepository.initializeTreeBasicInfoForRegister(authorization, initializingTreeInfo);
    }
    public LiveData<String> checkTreeBasicInfo(){
        return  treeRepository.checkTreeBasicInfo;
    }


    // 수목 위치(기본) 정보 등록
    public void loadSpecificLocationInfo (String authorization, TreeSpecificLocationInfoDTO treeSpecificLocationInfo){
        treeRepository.registerSpecificLocationInfo(authorization, treeSpecificLocationInfo);
    }
    public LiveData<String> checkSpecificLocationInfo(){
        return  treeRepository.checkSpecificLocationInfo();
    }


    // 수목 상태 정보 등록
    public void loadStatusInfo(String Authorization, TreeStatusInfoDTO treeStatusInfoDTO){
        treeRepository.registerTreeStatusInfo(Authorization, treeStatusInfoDTO);
    }
    public LiveData<String> checkStatusInfo(){
        return  treeRepository.checkStatusInfo;
    }


    // 수목 환경 정보 등록
    public void loadTreeEnvironmentInfo(String Authorization, TreeEnvironmentInfoDTO treeEnvironmentInfo){
        treeRepository.registerTreeEnvironmentInfo(Authorization, treeEnvironmentInfo);
    }
    public LiveData<String> checkTreeEnvironmentInfo(){
        return  treeRepository.checkTreeEnvironmentInfo();
    }


    // 수목 병해 정보 등록
    public void registerTreePestInfo(String Authorization, List<TreePestInfoDTO> pestinfoList){
        Log.d(TAG, "loadTreePestInfo"+pestinfoList);
        treeRepository.registerTreePestInfo(Authorization, pestinfoList);
    }
    public LiveData<String> checkTreePestInfo(){
        return  treeRepository.checkTreePestInfo();
    }


    // 수목 병해 정보 등록 2 (관리 페이지)
    public void loadTreePestInfo2(String Authorization, List<TreePestInfoDTO> pestinfoList){
        Log.d(TAG, "loadTreePestInfo2 "+pestinfoList);
        treeRepository.registerTreePestInfo2(Authorization, pestinfoList);
    }

    // 병해 정보 관리 페이지에서 등록
    public LiveData<String> checkTreePestManagement(){
        return  treeRepository.checkTreePestManagement();
    }


    /* ------------------------------------------------- READ METHODS ------------------------------------------------- */

    // 회원 통합 정보 조회
    public void loadTreeInfoByNFCtagId(String authorization, String tagId){
        treeRepository.getTreeInfoByNFCtagId(authorization, tagId);
    }
    public LiveData<TreeIntegratedVO> getTreeInfoByNFCtagId(){
        return  treeRepository.getTreeInfo();
    }

    // 수목 병해 정보 가져오기 by NFC tagId
    public void getTreePetsInfoByNFCtagId(String Authorization, String tagId){
        treeRepository.getTreePetsInfoByNFCtagId(Authorization, tagId);
    }
    public LiveData<List<Object>> getPestInfoList(){
        return  treeRepository.pestInfoList;
    }


    // 범위내의 수목 통합 정보 리스트 가져오기
    public void loadTreeInfoListByRange(String Authorization, TreeListRangeDTO rangeInfo){
        treeRepository.getTreeInfoListByRange(Authorization, rangeInfo);
    }
    public LiveData<List<Object>> getTreeInfoListByRange(){
        return  treeRepository.treeIntegratedList;
    }


    /* ------------------------------------------------- EXCEPTION ------------------------------------------------- */

    public LiveData<String> failCheck(){
        return treeRepository.failCheck;
    }


    /* ------------------------------------------------- UPDATE METHODS ------------------------------------------------- */

    // 수목 기본정보 수정
    public void modifyBasicInfo(String authorization, TreeBasicInfoDTO treeBasicInfo){
        treeRepository.modifyBasicInfo(authorization, treeBasicInfo);
    }
    public LiveData<String> CheckModifyBasicInfo(){
        return  treeRepository.checkModifyProcess();
    }


    // 수목 위치정보 수정
    public void modifyLocationInfo(String authorization, TreeLocationInfoDTO treeLocationInfo){
        treeRepository.modifyLocationInfo(authorization, treeLocationInfo);
    }
    public LiveData<String> checkModifyLocationInfo(){
        return  treeRepository.checkModifyLocationInfo();
    }


    // 수목 상태정보 수정
    public void modifyTreeStatusInfo(String authorization, TreeStatusInfoDTO treeStatusInfo){
        treeRepository.modifyTreeStatusInfo(authorization, treeStatusInfo);
    }


    // 수목 환경정보 수정
    public void loadEnvironmentInfo(String authorization, TreeEnvironmentInfoDTO treeEnvironmentInfo){
        treeRepository.modifyTreeEnvironmentInfo(authorization, treeEnvironmentInfo);
    }
    public LiveData<String> checkModifyEnvironmentInfo(){
        return  treeRepository.checkModifyEnvironmentInfo();
    }


    // 수목 병해정보 수정
    public void modifyTreePestInfo(String authorization, TreePestInfoDTO treePestInfo){
        treeRepository.modifyTreePestInfo(authorization, treePestInfo);
    }
    public LiveData<String> checkModifyPestInfo(){
        return  treeRepository.checkModifyTreePestInfo;
    }


}
