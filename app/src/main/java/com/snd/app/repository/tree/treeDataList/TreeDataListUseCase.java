package com.snd.app.repository.tree.treeDataList;



import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TreeDataListUseCase {
    public String TAG = this.getClass().getName();
    TreeDataListRepository treeDataListRepository;


    // 생성자
    public TreeDataListUseCase(TreeDataListRepository treeDataListRepository) {
        this.treeDataListRepository = treeDataListRepository;
    }



    /* ------------------------------------------------- READ METHODS------------------------------------------------- */

    // 수목 기본정보 - 수종 리스트 가져오기
    public void loadTreeSpeciesList(String Authorization){
        treeDataListRepository.getTreeSpeciesList(Authorization);
    }
    public LiveData<List<String>> getTreeSpeciesList(){
        Log.d(TAG, "*skskls**" + treeDataListRepository.getTreeSpeciesList());
        return  treeDataListRepository.getTreeSpeciesList();
    }



    // 수목 환경정보 - 보호틀 재질 리스트 가져오기
    public void loadFrameMaterialList(String Authorization){
        treeDataListRepository.getFrameMaterialList(Authorization);
    }
    public LiveData<List<String>> getFrameMaterialList(){
        return  treeDataListRepository.getFrameMaterialSppinerItems();
    }



    // 수목 환경정보 - 포장재 재질 리스트 가져오기
    public void loadPackingMaterialList(String Authorization){
        treeDataListRepository.getPackingMaterialList(Authorization);
    }
    public LiveData<List<String>> packingMaterialList(){
        return  treeDataListRepository.getPackingMaterialSppinerItems();
    }



    // 수목 상태정보 - 병충해명 리스트 가져오기
    public void loadPestNameList(String Authorization){
        Log.d(TAG, "loadPestNameList 호 출  " + treeDataListRepository.getTreePestNameLists());
        treeDataListRepository.getPestNameList(Authorization);
    }
    public LiveData<List<String>> getTreePestNameLists() {
        Log.d(TAG, "" + treeDataListRepository.getTreePestNameLists());
        return treeDataListRepository.getTreePestNameLists();
    }




    /* ------------------------------------------------- EXCEPTION------------------------------------------------- */

    public LiveData<String> throwFailCheck(){
        return  treeDataListRepository.failCheck;
    }




}
