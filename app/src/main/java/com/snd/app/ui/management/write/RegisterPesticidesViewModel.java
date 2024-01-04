package com.snd.app.ui.management.write;

import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.treeManagement.TreePesticidesDTO;
import com.snd.app.repository.company.CompanyUseCase;
import com.snd.app.repository.tree.treeManagement.TreeManagementUseCase;

import java.util.List;

import javax.inject.Inject;

public class RegisterPesticidesViewModel extends TMViewModel {
    public ObservableField<String> nfc=new ObservableField<>();
    public ObservableField<String> pesticidesCompany=new ObservableField<>();
    public ObservableField<String> pesticidesOperator=new ObservableField<>();
    public ObservableField<String> pesticidesDate=new ObservableField<>();
    public MutableLiveData<String> Authorization=new MutableLiveData<>();
    public MutableLiveData<String> proccess=new MutableLiveData<>();
    public MutableLiveData back=new MutableLiveData<>();

    public TreePesticidesDTO treePesticidesDTO;

    @Inject
    CompanyUseCase companyUseCase;
    @Inject
    TreeManagementUseCase treeManagementUseCase;


    public RegisterPesticidesViewModel() {
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
    public void registerPesticides(){
        treeManagementUseCase.registerPesticides(Authorization.getValue(), treePesticidesDTO);
    }
    public LiveData<String> checkTreePesticides(){
        return  treeManagementUseCase.checkTreePesticides();
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

    // 진행 사업명 스피너 선택
    public void onCompanyItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        treePesticidesDTO.setPesticidesBusiness(item);
    }


    public void setTextViewModel(TreePesticidesDTO treePesticidesDTO){
        this.treePesticidesDTO=treePesticidesDTO;
        nfc.set(treePesticidesDTO.getNfc());
        pesticidesCompany.set(treePesticidesDTO.getPesticidesCompany());
        pesticidesOperator.set(treePesticidesDTO.getPesticidesOperator());
        pesticidesDate.set(treePesticidesDTO.getPesticidesDate());
    }



    // 저장 버튼
    public void save(){
        proccess.setValue("event");
    }


    public void back(){
        back.setValue("event");
    }



}
