package com.snd.app.ui.management.write;

import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.treeManagement.TreeDefoliationDTO;
import com.snd.app.repository.company.CompanyUseCase;
import com.snd.app.repository.tree.treeManagement.TreeManagementUseCase;

import java.util.List;

import javax.inject.Inject;


public class RegisterDefoliationViewModel extends TMViewModel {
    public ObservableField<String> nfc=new ObservableField<>();
    public ObservableField<String> defoliationCompany=new ObservableField<>();
    public ObservableField<String> defoliationOperator=new ObservableField<>();
    public ObservableField<String> defoliationDate=new ObservableField<>();
    public MutableLiveData<String> Authorization=new MutableLiveData<>();
    public MutableLiveData<String> proccess=new MutableLiveData<>();
    public MutableLiveData back=new MutableLiveData<>();
    public TreeDefoliationDTO treeDefoliationDTO;

    @Inject
    CompanyUseCase companyUseCase;
    @Inject
    TreeManagementUseCase treeManagementUseCase;



    public RegisterDefoliationViewModel() {
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

    // 수목 맹아제거 정보 등록
    public void registerDefoliation(){
        treeManagementUseCase.registerDefoliation(Authorization.getValue(), treeDefoliationDTO);
    }
    public LiveData<String> checkTreeDefoliation(){
        return  treeManagementUseCase.checkTreeDefoliation();
    }



    /* --------------------------------------------- Spinner --------------------------------------------- */

    // 진행 사업명 스피너 선택
    public void onCompanyItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        treeDefoliationDTO.setDefoliationBusiness(item);
    }


    public void setTextViewModel(TreeDefoliationDTO treeDefoliationDTO){
        this.treeDefoliationDTO=treeDefoliationDTO;
        nfc.set(treeDefoliationDTO.getNfc());
        defoliationCompany.set(treeDefoliationDTO.getDefoliationCompany());
        defoliationOperator.set(treeDefoliationDTO.getDefoliationOperator());
        defoliationDate.set(treeDefoliationDTO.getDefoliationDate());
    }



    // 저장 버튼
    public void save(){
        proccess.setValue("event");
    }


    public void back(){
        back.setValue("event");
    }



}
