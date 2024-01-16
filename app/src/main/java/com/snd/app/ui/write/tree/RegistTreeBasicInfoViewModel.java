package com.snd.app.ui.write.tree;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.repository.tree.TreeUseCase;
import com.snd.app.domain.tree.dto.TreeInitializingDTO;
import com.snd.app.repository.tree.treeDataList.TreeDataListUseCase;
import com.snd.app.repository.tree.treeImage.TreeImageUseCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;



public class RegistTreeBasicInfoViewModel extends TMViewModel{
    public TreeInitializingDTO treeInitializingDTO;
    // 디자인에 표시되는 요소들
    public ObservableField<String> NFC = new ObservableField<>();
    public ObservableField<String> submitter = new ObservableField<>();
    public ObservableField<String> species = new ObservableField<>();
    public ObservableField<String> vendor = new ObservableField<>();
    public ObservableField<String> latitude = new ObservableField<>();
    public ObservableField<String> longitude = new ObservableField<>();
    public MutableLiveData<Integer> countText = new MutableLiveData<>(0);

    public MutableLiveData<Boolean> showEditText = new MutableLiveData<>(false);
    public MutableLiveData<String> insertProcess = new MutableLiveData<>();
    public MutableLiveData<String> isImage = new MutableLiveData<>();
    public MutableLiveData<String> Authorization = new MutableLiveData<>();
    // 액티비티로부터 반응형 결과 얻을 객체

    public MutableLiveData<String> onHashtagEvent = new MutableLiveData<>();

    // 카메라 실행
    public MutableLiveData camera = new MutableLiveData();

    //private List<Bitmap> _currentList = new ArrayList<>();     // 파일이 들어올 때마다 객체를 담고
    //public MutableLiveData<List<Bitmap>> currentList = new MutableLiveData<>();  // PhotoAdapter로 전달할 객체

    public List<File> _currentFileList = new ArrayList<>();  // 서버로 전달할 Multipart 객체
    public MutableLiveData<List<File>> currentFileList = new MutableLiveData<>();  // PhotoAdapter로 전달할 객체


    public MutableLiveData back = new MutableLiveData<>();
    @Inject
    TreeDataListUseCase treeDataListUseCase;
    @Inject
    TreeImageUseCase treeImageUseCase;
    @Inject
    TreeUseCase treeUseCase;


    /* Constructor */
    public RegistTreeBasicInfoViewModel(){
        treeUseCase = getAppComponent().treeUseCase();
        treeImageUseCase = getAppComponent().treeImageUseCase();
        treeDataListUseCase = getAppComponent().treeDataListUseCase();
    }


    /* ------------------------------------------------- VIEWS ------------------------------------------------- */

    // 사진 추가하는 메서드
    public void addImageList(Bitmap bitmap) {
        /* 이 메서드에 대한 설명 :
        * 사진이 추가될 때마다 비트맵을 추가하고 그 결과를 실시간으로 알리기.
        * 그 개수도 실시간으로 렌더링하고,
        * 그리고 그 결과를 반영하여 포토어댑터의 이미지 표기를 보여줌 */
        /*
        * */
    }

    public void addImageList2(File file) {
        if(_currentFileList.size() < 2){
            _currentFileList.add(file);
            currentFileList.setValue(_currentFileList);
        }
        countText.setValue(_currentFileList.size());
    }



    // 1 화면에 표시되는 메서드
    public void setTextViewModel(TreeInitializingDTO treeInitializingDTO){
        this.treeInitializingDTO = treeInitializingDTO;

        NFC.set(treeInitializingDTO.getNfc());
        submitter.set(treeInitializingDTO.getSubmitter());
        vendor.set(treeInitializingDTO.getVendor());

        double latitudeValue = treeInitializingDTO.getLatitude();
        double longitudeValue = treeInitializingDTO.getLongitude();

        String formattedLatitude = String.format("%.7f", latitudeValue);
        String formattedLongitude = String.format("%.7f", longitudeValue);

        latitude.set(formattedLatitude);
        longitude.set(formattedLongitude);
    }



    /* --------------------------------------------- READ MEHTOD --------------------------------------------- */

    public void setSpiciesSpinner(){
        treeDataListUseCase.loadTreeSpeciesList(Authorization.getValue());
    }
    public LiveData<List<String>> getSpiciesSpinner(){
        return  treeDataListUseCase.getTreeSpeciesList();
    }



    /* --------------------------------------------- CREATE MEHTOD --------------------------------------------- */

    // 1) 이미지 등록
    public void registerTreeImage(){
        if(currentFileList != null && _currentFileList.size() > 0){
            treeImageUseCase.registerTreeImage(Authorization.getValue(), treeInitializingDTO.getNfc(), _currentFileList);    // Images
            initializeTreeBasicInfoForRegister();   // DTO
        }else {
            isImage.setValue("emptyImageList");
        }
    }


    // 2) 기본 정보 등록 - 어차피 거쳐야 함
    public void initializeTreeBasicInfoForRegister(){
        // 전달받은 리스트가 있을 경우 그 수만큼 꺼내서 해시태그 매칭하기
        treeUseCase.loadTreeBasicInfo(Authorization.getValue(), treeInitializingDTO);
    }
    public LiveData<String> checkTreeBasicInfo(){
        return  treeUseCase.checkTreeBasicInfo();
    }



    /* ------------------------------------------------- EXCEPTION ------------------------------------------------ */

    public LiveData<String> throwFailCheck(){
        return  treeDataListUseCase.throwFailCheck();
    }



    /* ------------------------------------------------- SPINNER ------------------------------------------------- */

    // 수목명 스피너 선택
    public void onSpeciesItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        showEditText.setValue("직접 입력".equals(item));
        if(item != "직접 입력"){
            treeInitializingDTO.setSpecies(item);
        }
    }


    // 저장 버튼 - 데이터 공란 확인
    public void save(){
        insertProcess.setValue(treeInitializingDTO.getSpecies() == null ? "none" : "insert");
    }


    public void setCamera(){
        camera.setValue("click");
    }
    public void setBack(){
        back.setValue("click");
    }
    public ObservableField getLatitude(){
        return latitude;
    }
    public ObservableField getLongitude(){
        return longitude;
    }


    public void onHashtagEvent(){
        onHashtagEvent.setValue("onHashtagEvent");
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        //currentList = null;
        /*
        if(_currentList != null) {
            for(Bitmap bitmap: _currentList) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            _currentList.clear();
            _currentList = null;
        }
         */

        if(currentFileList != null) {
            _currentFileList.clear();
            _currentFileList = null;
        }
        treeDataListUseCase = null;
    }


}