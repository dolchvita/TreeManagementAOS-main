package com.snd.app.ui.management.write;

import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.treeManagement.TreeHorticultureDTO;
import com.snd.app.repository.company.CompanyUseCase;
import com.snd.app.repository.tree.treeManagement.TreeManagementUseCase;

import java.util.List;

import javax.inject.Inject;

public class RegisterHorticultureViewModel extends TMViewModel {
    public ObservableField<String> nfc=new ObservableField<>();
    public ObservableField<String> horticultureCompany=new ObservableField<>();
    public ObservableField<String> horticultureOperator=new ObservableField<>();
    public ObservableField<String> horticultureDate=new ObservableField<>();
    public MutableLiveData<String> Authorization=new MutableLiveData<>();
    public MutableLiveData<String> proccess=new MutableLiveData<>();
    public MutableLiveData back=new MutableLiveData<>();

    public TreeHorticultureDTO treeHorticultureDTO;

    @Inject
    CompanyUseCase companyUseCase;
    @Inject
    TreeManagementUseCase treeManagementUseCase;


    public RegisterHorticultureViewModel() {
        companyUseCase = getAppComponent().companyUseCase();
        treeManagementUseCase = getAppComponent().treeManagementUseCase();
    }


    /* --------------------------------------------- READ MEHTOD --------------------------------------------- */

    // 특정 업체 진행중 사업명 리스트
    public void loadCompanyBusinessNameList(String authorization, String companyName){
        companyUseCase.loadCompanyBusinessNameList(authorization, companyName);
    }
    public LiveData<List<String>> getCompanyBusinessNameList() {
        return companyUseCase.getCompanyBusinessNameList();
    }


    /* --------------------------------------------- CREATE MEHTOD --------------------------------------------- */

    // 수목 생육환경개선 정보 등록
    public void registerHorticulture(){
        treeManagementUseCase.registerHorticulture(Authorization.getValue(), treeHorticultureDTO);
    }
    public LiveData<String> checkTreeHorticulture(){
        return  treeManagementUseCase.checkTreeHorticulture();
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

    // 진행 사업명 스피너 선택
    public void onCompanyItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        treeHorticultureDTO.setHorticultureBusiness(item);
    }


    public void setTextViewModel(TreeHorticultureDTO treeHorticultureDTO){
        this.treeHorticultureDTO=treeHorticultureDTO;
        nfc.set(treeHorticultureDTO.getNfc());
        horticultureCompany.set(treeHorticultureDTO.getHorticultureCompany());
        horticultureOperator.set(treeHorticultureDTO.getHorticultureOperator());
        horticultureDate.set(treeHorticultureDTO.getHorticultureDate());
    }



    // 저장 버튼
    public void save(){
        proccess.setValue("event");
    }


    public void back(){
        back.setValue("event");
    }



}
