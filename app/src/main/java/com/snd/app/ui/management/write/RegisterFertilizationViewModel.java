package com.snd.app.ui.management.write;

import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.treeManagement.TreeFertilizationDTO;
import com.snd.app.repository.company.CompanyUseCase;
import com.snd.app.repository.tree.treeManagement.TreeManagementUseCase;

import java.util.List;

import javax.inject.Inject;

public class RegisterFertilizationViewModel extends TMViewModel {
    public ObservableField<String> nfc=new ObservableField<>();
    public ObservableField<String> fertilizationCompany=new ObservableField<>();
    public ObservableField<String> fertilizationOperator=new ObservableField<>();
    public ObservableField<String> fertilizationDate=new ObservableField<>();
    public MutableLiveData<String> Authorization=new MutableLiveData<>();
    public MutableLiveData<String> proccess=new MutableLiveData<>();
    public MutableLiveData back=new MutableLiveData<>();
    public TreeFertilizationDTO treeFertilizationDTO;
    @Inject
    CompanyUseCase companyUseCase;
    @Inject
    TreeManagementUseCase treeManagementUseCase;


    public RegisterFertilizationViewModel() {
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
    public void registerFertilization(){
        treeManagementUseCase.registerFertilization(Authorization.getValue(), treeFertilizationDTO);
    }
    public LiveData<String> checkTreeFertilization(){
        return  treeManagementUseCase.checkTreeFertilization();
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

    // 진행 사업명 스피너 선택
    public void onCompanyItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        treeFertilizationDTO.setFertilizationBusiness(item);
    }


    public void setTextViewModel(TreeFertilizationDTO treeFertilizationDTO){
        this.treeFertilizationDTO = treeFertilizationDTO;
        nfc.set(treeFertilizationDTO.getNfc());
        fertilizationCompany.set(treeFertilizationDTO.getFertilizationCompany());
        fertilizationOperator.set(treeFertilizationDTO.getFertilizationOperator());
        fertilizationDate.set(treeFertilizationDTO.getFertilizationDate());
    }


    // 저장 버튼
    public void save(){
        proccess.setValue("event");
    }


    public void back(){
        back.setValue("event");
    }



}
