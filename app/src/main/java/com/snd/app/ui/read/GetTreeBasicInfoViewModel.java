package com.snd.app.ui.read;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.data.singleton.SingletonVO;
import com.snd.app.domain.tree.dto.TreeBasicInfoDTO;
import com.snd.app.domain.tree.dto.TreeHashtagDTO;
import com.snd.app.domain.tree.dto.TreeHashtagModifyDTO;
import com.snd.app.domain.tree.vo.TreeIntegratedVO;
import com.snd.app.repository.tree.TreeUseCase;
import com.snd.app.repository.tree.treeDataList.TreeDataListUseCase;
import com.snd.app.repository.tree.treeHashtag.TreeHashtagUseCase;
import com.snd.app.repository.tree.treeImage.TreeImageUseCase;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class GetTreeBasicInfoViewModel extends TMViewModel {
    public String Authorization;
    public String idHex;
    TreeBasicInfoDTO treeBasicInfoDTO;
    SingletonVO singletonVO;
    public Boolean editPhoto;
    public Integer num;     // 새로 추가시 자리 배치하기
    public ObservableField<String> NFC = new ObservableField<>();
    public ObservableField<String> species = new ObservableField<>();
    public ObservableField<String> submitter = new ObservableField<>();
    public ObservableField<String> vendor = new ObservableField<>();
    public ObservableField<String> basicInserted = new ObservableField<>();
    /* 이미지 관련 */
    public MutableLiveData<String> camera = new MutableLiveData<>();
    public MutableLiveData<Integer> imageProcess = new MutableLiveData<>();     // 수정시 사진 개수 확인용
    public MutableLiveData<Boolean> showEditText = new MutableLiveData<>(false);
    public MutableLiveData<File> addFile = new MutableLiveData<>(); // 사진 추가 확인 여부
    List<String> currentFileUrl = new ArrayList<>();    // 어댑터에 보낼 문자열
    List<File> currentFileList = new ArrayList<>(); // 서버에 보낼 파일 리스트
    List<String> fileNameList = new ArrayList<>();  // 서버에서 표현되고 있는 기존 이미지 리스트
    /* 이미지 삭제 관련 */
    public Boolean deletePhoto = false;
    public Integer initFileNameListSize;
    String keyword;
    /* 해시태그 관련 */
    public MutableLiveData<String> onHashtagEvent = new MutableLiveData<>();

    public MutableLiveData<Boolean> readOrEdit = new MutableLiveData<>();
    public MutableLiveData<Boolean> cancel = new MutableLiveData<>();
    boolean flag = false;   // true -> Edit Mode

    @Inject
    TreeUseCase treeUseCase;
    @Inject
    TreeImageUseCase treeImageUseCase;
    @Inject
    TreeDataListUseCase treeDataListUseCase;
    @Inject
    TreeHashtagUseCase treeHashtagUseCase;


    public GetTreeBasicInfoViewModel() {
        treeUseCase = getAppComponent().treeUseCase();
        treeImageUseCase = getAppComponent().treeImageUseCase();
        treeDataListUseCase = getAppComponent().treeDataListUseCase();
        treeHashtagUseCase = getAppComponent().treeHashtagUseCase();
    }


    /* --------------------------------------------- READ --------------------------------------------- */

    // 수목 통합 정보 가져오기
    public void loadTreeIntegratedVO(String Authorization, String idHex){
        this.Authorization = Authorization;
        this.idHex = idHex;
        treeUseCase.loadTreeInfoByNFCtagId(Authorization, idHex);
    }
    public LiveData<TreeIntegratedVO> getTreeIntegratedVO(){
        return  treeUseCase.getTreeInfoByNFCtagId();
    }


    // 수목 기본정보 - 수종 리스트 가져오기
    public void setSpiciesSpinner(){
        treeDataListUseCase.loadTreeSpeciesList(Authorization);
    }
    public LiveData<List<String>> getSpiciesSpinner(){
        return  treeDataListUseCase.getTreeSpeciesList();
    }


    // 수목 저장된 파일명 리스트 반환
    public void loadTreeImageFilenameList(String Authorization, String tagId){
        treeImageUseCase.loadTreeImageFilenameList(Authorization, tagId);
    }
    public LiveData<List<String>> getTreeImageFilenameList(){
        return treeImageUseCase.getTreeImageFilenameList();
    }


    /* --------------------------------------------- UPDATE --------------------------------------------- */

    /** 서버에 보낼 파일을 담아서 반응을 일으킴 **/
    public void addImageList(File file){
        // 사진이 화면에 보일 객체
        if(currentFileUrl.size() < 2){
            currentFileUrl.add(file.getPath());
            addFile.setValue(file);     // 저장 단일 객체
            currentFileList.add(file);
        }
    }


    // 기본 정보 수정
    public void modifyTreeBasicInfo(){
        // 삭제 프로세스
        if(deletePhoto){
            if(fileNameList.size() == 0){
                deleteTreeImage("a");
            }else {
                keyword = extractImageSequence(fileNameList.get(0));
                deleteTreeImage(keyword);
            }
        }else {
            if(currentFileList.size() > 0){
                modifyTreeImage();
            }
            treeUseCase.modifyBasicInfo(Authorization, treeBasicInfoDTO);
        }
    }


    // 이미지 수정
    public void modifyTreeImage(){
        switch (imageProcess.getValue()){
            case 0:
                treeImageUseCase.registerTreeImage(Authorization, idHex, currentFileList); break;

            case 1:
                if(editPhoto){
                    treeImageUseCase.modifyTreeParticularImage(Authorization, idHex, "1", currentFileList.get(0));  // 1장 수정
                }else {
                    treeImageUseCase.registerTreeImage2(Authorization, idHex, currentFileList, num);
                } break;

            case 2:
                treeImageUseCase.modifyTreeImageAll(Authorization, currentFileList, idHex); break;
        }
    }


    // 해시태그 수정
    public void updateTreeHashtag(TreeHashtagModifyDTO treeHashtagModify){
        treeHashtagUseCase.updateTreeHashtag(Authorization, treeHashtagModify);
    }


    // 해시태그 추가
    public void appendTreeHashtag(TreeHashtagModifyDTO treeHashtagModify){
        treeHashtagUseCase.appendTreeHashtag(Authorization, treeHashtagModify);
    }


    /* --------------------------------------------- DELETE --------------------------------------------- */

    // 삭제 프로세스 적용하는 메서드
    public void checkDeleteProcess(List<String> currentFilenameList){
        deletePhoto = (currentFilenameList.size() < initFileNameListSize) ? true : false;
    }


    // 사진의 이름을 조사하여 자리 선정하기 #2
    private String extractImageSequence(String imageUrl) {
        return (imageUrl.contains("_1")) ? "2" : "1";
    }


    // 수목 기본 정보 이미지삭제
    public void deleteTreeImage(String keyword){
        treeImageUseCase.deleteTreeImage(Authorization, idHex, keyword);
    }


    // 해시태그 삭제
    public void deleteTreeHashtag(TreeHashtagModifyDTO treeHashtagModify){
        treeHashtagUseCase.deleteTreeHashtag(Authorization, treeHashtagModify);
    }


    /* ----------------------------------------------- RESULT ----------------------------------------------- */

    // 디티오 수정에 대한 결과값
    public LiveData<String> checkModifyBasicInfo(){
        return  treeUseCase.CheckModifyBasicInfo();
    }

    // 이미지 수정에 대한 결과값
    public LiveData<String> modifyResult(){
        return treeImageUseCase.modifyResult();
    }

    // 삭제에 대한 결과값 반환
    public LiveData<String> deleteResult(){
        return treeImageUseCase.deleteResult();
    }

    // 해시태그 수정에 대한 결과
    public LiveData<String> checkTreeHashtag(){
        return  treeHashtagUseCase.checkTreeHashtag();
    }


    /* --------------------------------------------- EXCEPTION --------------------------------------------- */

    // 등록된 칩인지 확인
    public LiveData<String> failCheck() {
        return treeUseCase.failCheck();
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

    // 보호틀 스피너 선택
    public void onSpeciesItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        showEditText.setValue("직접 입력".equals(item));
        if(item != "직접 입력"){
            treeBasicInfoDTO.setSpecies(item);
        }
    }


    // 사진 추가시 갤러리로 접근할 버튼
    public void onGalleryOpen(){
        Log.d(TAG, "갤러리 오픈");

    }


    // 2 데이터바인딩시 참조할 변수 매핑 - 서버로부터 데이터 받아온 후 UI에서 호출
    public void setTextViewModel(TreeIntegratedVO treeIntegratedVO){
        singletonVO = SingletonVO.getInstance();
        singletonVO.setData(treeIntegratedVO);
        NFC.set(treeIntegratedVO.getNfc());
        species.set(treeIntegratedVO.getSpecies());
        submitter.set(treeIntegratedVO.getBasicSubmitter());
        vendor.set(treeIntegratedVO.getBasicVendor());
        basicInserted.set(getBasicInserted(treeIntegratedVO.getBasicInserted()));
        mappingBasicInfoDTO(treeIntegratedVO);
    }


    public void mappingBasicInfoDTO(TreeIntegratedVO treeIntegratedVO){
        treeBasicInfoDTO = new TreeBasicInfoDTO();
        treeBasicInfoDTO.setNfc(treeIntegratedVO.getNfc());
        treeBasicInfoDTO.setSpecies(treeIntegratedVO.getSpecies());
        treeBasicInfoDTO.setSubmitter(treeIntegratedVO.getBasicSubmitter());
        treeBasicInfoDTO.setVendor(treeIntegratedVO.getBasicVendor());
    }


    public void inputSpecies(String species){
        if (treeBasicInfoDTO != null){
            treeBasicInfoDTO.setInserted(null);
            treeBasicInfoDTO.setSpecies(species);
        }
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


    public void save(){
        flag = !flag;
        if(readOrEdit != null){
            readOrEdit.setValue(flag);
        }
    }


    public void setCamera(){
        camera.setValue((flag) ? "camera" : "null");
    }


    public void onHashtagEvent(){
        onHashtagEvent.setValue("onHashtagEvent");
    }


    public void setBack(){
        cancel.setValue(flag);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        treeUseCase = null;
        treeImageUseCase = null;
        treeDataListUseCase = null;
    }


}