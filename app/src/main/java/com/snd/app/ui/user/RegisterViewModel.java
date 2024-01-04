package com.snd.app.ui.user;

import android.view.View;
import android.widget.AdapterView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.user.UserDTO;
import com.snd.app.repository.company.CompanyUseCase;
import com.snd.app.repository.user.UserUseCase;

import java.util.List;

import javax.inject.Inject;

public class RegisterViewModel extends TMViewModel {
    @Inject
    CompanyUseCase companyUseCase;
    @Inject
    UserUseCase userUseCase;

    // 일단 텍스트필드 값 모두 직접 참조하기
    public UserDTO userDTO;
    MutableLiveData<Boolean> isValidationCheck = new MutableLiveData<>();
    MutableLiveData<String> textFeild = new MutableLiveData<>();      // 어떤 필드에서 오류가 났는지
    MutableLiveData<Boolean> registerCheck = new MutableLiveData<>();   // 입력칸 모두 채워져 있는지 확인

    // 아이디 중복 체크하고 넘어갔는지
    private MutableLiveData<Boolean> _isIdCheck = new MutableLiveData<>(false);
    public LiveData<Boolean> isIdCheck = _isIdCheck;


    public RegisterViewModel() {
        companyUseCase = getAppComponent().companyUseCase();
        userUseCase = getAppComponent().userUseCase();
        userDTO = new UserDTO();
        loadCompanyNameList();
    }


    /* --------------------------------------------- CREATE MEHTOD --------------------------------------------- */

    // 회원가입
    public void loadRegisterUser(){
        if (isValidationCheck.getValue()) {
            userUseCase.loadRegisterUser(userDTO);
        }
    }
    public LiveData<String> appRegisterUser(){
        return userUseCase.appRegisterUser();
    }


    /* --------------------------------------------- READ MEHTOD --------------------------------------------- */

    // 업체명 리스트 가져오기
    public void loadCompanyNameList(){
        companyUseCase.loadCompanyNameList();
    }
    public LiveData<List<String>> getCompanyNameListAll() {
        return companyUseCase.getCompanyNameListAll();
    }


    // 아이디 중복체크 버튼
    public void loadIdCheck(){
        String idReg = "^[a-z][a-z0-9]{3,19}$";
        if(userDTO.getId() != null && !userDTO.getId().matches(idReg)) {
            isValidationCheck.setValue(false);
            textFeild.setValue("id");
        }else {
            isValidationCheck.setValue(true);
            textFeild.setValue("success");
            userUseCase.loadIdCheck(userDTO.getId());
            _isIdCheck.setValue(true);
        }
    }
    public LiveData<String> idCheck(){
        return userUseCase.idCheck();
    }


    // 데이터 잘 작성되었는지 유효성 체크
    LiveData<String> registerUserFromApp(){
        String passwordReg = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{10,}|(?=.*[A-Za-z])(?=.*[^A-Za-z\\d])[A-Za-z\\W]{10,}|(?=.*\\d)(?=.*[^A-Za-z\\d])\\d[\\W\\d]{9,}|(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d])[A-Za-z\\d\\W]{10,}$";
        String phoneReg = "^[0-9]{9,}$";
        String emailReg = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if(userDTO.getPassword() != null && !userDTO.getPassword().matches(passwordReg)) {
            textFeild.setValue("passwordFail");
            isValidationCheck.setValue(false);
        } else {
            isValidationCheck.setValue(true);
            textFeild.setValue("passwordSuccess");
        }
        if (userDTO.getName() != null && (userDTO.getName().length()<1)) {
            isValidationCheck.setValue(false);
            textFeild.setValue("nameFail");
        } else {
            isValidationCheck.setValue(true);
            textFeild.setValue("nameSuccess");
        }
        if(userDTO.getPhone() != null && !userDTO.getPhone().matches(phoneReg)) {
            isValidationCheck.setValue(false);
            textFeild.setValue("phoneFail");
        } else {
            isValidationCheck.setValue(true);
            textFeild.setValue("phoneSuccess");
        }
        if(userDTO.getEmail() != null && !userDTO.getEmail().matches(emailReg)) {
            isValidationCheck.setValue(false);
            textFeild.setValue("emailFail");
        } else {
            isValidationCheck.setValue(true);
            textFeild.setValue("emailSuccess");
        }
        return textFeild;
    }



    /* --------------------------------------------- Spinner --------------------------------------------- */

    public void onCompanySeleted(AdapterView<?> parent, View view, int position, long id){
        String item = (String) parent.getItemAtPosition(position);
        userDTO.setCompany(item);
    }


    // 회원등록 버튼
    public void save(){
        registerCheck.setValue(checkDTOMessage());
    }


    // 공란 확인
    private Boolean checkDTOMessage(){
        if(userDTO.getId() == null || userDTO.getPassword() == null || userDTO.getName() == null || userDTO.getPhone() == null ||
        userDTO.getPosition() == null || userDTO.getEmail() == null ){
            return false;
        }
        return true;
    }


}
