package com.snd.app.ui.management.write;

import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.treeManagement.TreePruningDTO;
import com.snd.app.repository.company.CompanyUseCase;
import com.snd.app.repository.tree.treeManagement.TreeManagementUseCase;

import java.util.List;

import javax.inject.Inject;

public class RegisterPruningViewModel extends TMViewModel {
    public ObservableField<String> nfc=new ObservableField<>();
    public ObservableField<String> pruningCompany=new ObservableField<>();
    public ObservableField<String> pruningOperator=new ObservableField<>();
    public ObservableField<String> pruningDate=new ObservableField<>();

    public MutableLiveData<String> Authorization=new MutableLiveData<>();
    public MutableLiveData<String> proccess=new MutableLiveData<>();
    public MutableLiveData back=new MutableLiveData<>();

    public TreePruningDTO treePruningDTO;

    @Inject
    TreeManagementUseCase treeManagementUseCase;
    @Inject
    CompanyUseCase companyUseCase;


    public RegisterPruningViewModel() {
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

    public void registerPruning(){
        treeManagementUseCase.registerPruning(Authorization.getValue(), treePruningDTO);
    }
    public LiveData<String> checkPruningInfo(){
        return  treeManagementUseCase.checkPruningInfo();
    }



    /* --------------------------------------------- Spinner --------------------------------------------- */

    // 진행 사업명 스피너 선택
    public void onCompanyItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        treePruningDTO.setPruningBusiness(item);
    }



    // 화면에 표시되는 메서드
        public void setTextViewModel(TreePruningDTO treePruningDTO){
        this.treePruningDTO=treePruningDTO;
        nfc.set(treePruningDTO.getNfc());
        pruningCompany.set(treePruningDTO.getPruningCompany());
        pruningOperator.set(treePruningDTO.getPruningOperator());
        pruningDate.set(treePruningDTO.getPruningDate());
    }



    // 저장 버튼
    public void save(){
        proccess.setValue("event");
    }


    public void back(){
        back.setValue("event");
    }



}
