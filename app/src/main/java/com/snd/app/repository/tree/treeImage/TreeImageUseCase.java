package com.snd.app.repository.tree.treeImage;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.io.File;
import java.util.List;


// 서버 통신 작업 직접 수행
public class TreeImageUseCase {
    public String TAG = this.getClass().getName();
    TreeImageRepository treeImageRepository;

    // Repo는 생성자 주입 - UseCase는 DI
    public TreeImageUseCase(TreeImageRepository treeImageRepository) {
        this.treeImageRepository = treeImageRepository;
    }


    /* ----------------------------------------------- CREATE METHODS ----------------------------------------------- */

    //  수목 기본정보 이미지등록
    public void registerTreeImage(String Authorization, String tagId, List<File> currentFileList){
        Log.d(TAG, "** ㅋ ** " + currentFileList);
        treeImageRepository.registerTreeImage(Authorization, tagId, currentFileList);
    }

    // 수목 기본정보 이미지 추가 등록
    public void registerTreeImage2(String Authorization, String tagId, List<File> currentFileList, int num){
        treeImageRepository.registerTreeImage2(Authorization, tagId, currentFileList, num);
    }


    /* ----------------------------------------------- READ METHODS ----------------------------------------------- */

    // 수목 저장된 파일명 리스트 반환
    public void loadTreeImageFilenameList(String Authorization, String tagId){
        treeImageRepository.getTreeImageFilenameList(Authorization, tagId);
    }
    public LiveData<List<String>> getTreeImageFilenameList(){
        return treeImageRepository.filenameList;
    }


    /* ----------------------------------------------- UPDATE METHODS ----------------------------------------------- */

    //  이미지 수정 (모두)
    public void modifyTreeImageAll(String Authorization, List<File> currentFileList, String tagIdstr){
        treeImageRepository.modifyTreeImageAll(Authorization, currentFileList, tagIdstr);
    }

    //  이미지 수정 (특정 하나만)
    public void modifyTreeParticularImage(String Authorization, String tagId, String keyword, File file){
        treeImageRepository.modifyTreeParticularImage(Authorization, tagId, keyword, file);
    }


    /* ----------------------------------------------- DELETE METHODS ----------------------------------------------- */

    // 수목 기본 정보 이미지삭제
    public void deleteTreeImage(String Authorization, String tagId, String keyword){
        treeImageRepository.deleteTreeImage(Authorization, tagId, keyword);
    }


    /* ----------------------------------------------- RESULT ----------------------------------------------- */

    // CRUD 대한 결과값 반환

    public LiveData<String> modifyResult(){
        return treeImageRepository.modifyResult;
    }


    public LiveData<String> deleteResult(){
        return treeImageRepository.deleteResult;
    }



}
