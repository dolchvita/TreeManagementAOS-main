package com.snd.app.ui.management.write;

import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.treeManagement.TreeSurgeryDTO;
import com.snd.app.repository.company.CompanyUseCase;
import com.snd.app.repository.tree.treeManagement.TreeManagementUseCase;

import java.util.List;

import javax.inject.Inject;

public class RegisterSurgeryViewModel extends TMViewModel {
    public ObservableField<String> nfc=new ObservableField<>();
    public ObservableField<String> surgeryCompany=new ObservableField<>();
    public ObservableField<String> surgeryOperator=new ObservableField<>();
    public ObservableField<String> surgeryDate=new ObservableField<>();
    public MutableLiveData<String> Authorization=new MutableLiveData<>();
    public MutableLiveData<String> proccess=new MutableLiveData<>();
    public MutableLiveData back=new MutableLiveData<>();

    public TreeSurgeryDTO treeSurgeryDTO;

    @Inject
    CompanyUseCase companyUseCase;
    @Inject
    TreeManagementUseCase treeManagementUseCase;


    public RegisterSurgeryViewModel() {
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

    // 수목 병충해방제 정보 등록
    public void registerSurgery(){
        treeManagementUseCase.registerSurgery(Authorization.getValue(), treeSurgeryDTO);
    }
    public LiveData<String> checkTreeSurgery(){
        return  treeManagementUseCase.checkTreeSurgery();
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

    // 진행 사업명 스피너 선택
    public void onCompanyItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        treeSurgeryDTO.setSurgeryBusiness(item);
    }


    public void setTextViewModel(TreeSurgeryDTO treeSurgeryDTO){
        this.treeSurgeryDTO=treeSurgeryDTO;
        nfc.set(treeSurgeryDTO.getNfc());
        surgeryCompany.set(treeSurgeryDTO.getSurgeryCompany());
        surgeryOperator.set(treeSurgeryDTO.getSurgeryOperator());
        surgeryDate.set(treeSurgeryDTO.getSurgeryDate());
    }



    // 저장 버튼
    public void save(){
        proccess.setValue("event");
    }



    public void back(){
        back.setValue("event");
    }



}
