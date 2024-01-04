package com.snd.app.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.R;
import com.snd.app.common.TMActivity;
import com.snd.app.databinding.MainRegisterActBinding;
import com.snd.app.ui.login.LoginActivity;

import java.util.List;
import java.util.function.Consumer;


public class RegisterActivity extends TMActivity {
    MainRegisterActBinding mainRegisterActBinding;
    RegisterViewModel registerVM;
    private Spinner register_commpany_spinner;
    private AlertDialog.Builder alertDialogBuilder;
    AppCompatEditText id_input;
    AppCompatEditText passwordInput;
    AppCompatEditText passwordCheck;
    AppCompatEditText nameInput;
    AppCompatEditText userPhoneInput;
    AppCompatEditText userEmailInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainRegisterActBinding = DataBindingUtil.setContentView(this, R.layout.main_register_act);
        mainRegisterActBinding.setLifecycleOwner(this);
        registerVM = new ViewModelProvider(this).get(RegisterViewModel.class);
        mainRegisterActBinding.setRegisterVM(registerVM);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initUserCompanySpinner();
        mappingTextInput();

        // 아이디 중복 체크 버튼 실행
        registerVM.idCheck().observe(this, s -> {
            alertMessage((s.equals("Welcome")) ? "사용 가능한 아이디입니다." : "이미 사용중인 아이디입니다.");
        });


        // 어떤 텍스트필드에서 오류가 났는지
        registerVM.registerUserFromApp().observe(this, s -> {
            isValidationForm(s);
        });


        // 공란 체크 && 아이디 중복체크 여부
        registerVM.registerCheck.observe(this, aBoolean -> {
            if(!registerVM.isIdCheck.getValue()){
                Toast.makeText(this, "아이디 중복체크를 먼저 진행해주세요.", Toast.LENGTH_SHORT).show();
            }else {
                if(!aBoolean){
                    Toast.makeText(this, "입력 사항을 모두 기재해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    registerVM.loadRegisterUser();
                }
            }
        });


        // 회원가입이 완료되었다면..
        registerVM.appRegisterUser().observe(this, s -> {
            insertProcessAlert((s.equals("success")) ? "성공적으로 가입되었습니다" : s);
        });

    }// ./onResume


    // 화면에 오류 메시지 띄우기
    void isValidationForm(String userInput){
        mainRegisterActBinding.idInputLayout.setError((userInput.equals("id")) ? "띄어쓰기 없이 4 ~ 19자 이내로 알파벳 소문자와 숫자로만 작성해주세요." : null);
        mainRegisterActBinding.passInputLayout.setError((userInput.equals("passwordFail")) ? "비밀번호는 최소 10자리 이상의 영어 대문자, 소문자, 숫자, 특수문자 중 2종류 조합 입니다." : null);
        mainRegisterActBinding.nameInputLayout.setError((userInput.equals("nameFail")) ? "이름은 한글자 이상이어야 합니다." : null);
        mainRegisterActBinding.userPhoneInputLayout.setError((userInput.equals("phoneFail")) ? "전화번호는 9자리 이상의 숫자여야합니다." : null);
        mainRegisterActBinding.userEmailInputLayout.setError((userInput.equals("emailFail")) ? "잘못된 형식의 이메일입니다." : null);
    }


    // 매핑만 하고, 유효성 검사는 뷰모델에서 진행할 것
    void mappingTextInput(){
        initTextView();

        // 아이디 입력 후 다음 버튼 눌렀을 때
        id_input.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    if(!registerVM.isIdCheck.getValue()){
                        alertMessage("중복체크를 먼저 확인해주세요");
                        return true;
                    }
                }
                return false;
            }
        });


        id_input.addTextChangedListener(createTextWatcher((text) -> {
            registerVM.userDTO.setId(text);
        }));


        passwordInput.addTextChangedListener(createTextWatcher((text) -> {
            registerVM.userDTO.setPassword(text);
        }));


        passwordCheck.addTextChangedListener(createTextWatcher((text) -> {
            if(registerVM.userDTO.getPassword() != null && !registerVM.userDTO.getPassword().equals(text)){
                passwordCheck.setError("비밀번호가 일치하지 않습니다.");
            }else {
                passwordCheck.setError(null);
            }
        }));


        nameInput.addTextChangedListener(createTextWatcher((text) -> {
            registerVM.userDTO.setName(text);
        }));


        userPhoneInput.addTextChangedListener(createTextWatcher((text) -> {
            registerVM.userDTO.setPhone(text);
        }));


        userEmailInput.addTextChangedListener(createTextWatcher((text) -> {
            registerVM.userDTO.setEmail(text);
        }));


        registerVM.userDTO.setPosition("손님");


        AppCompatButton registerBt = mainRegisterActBinding.registerBt;
        CheckBox checkBox = mainRegisterActBinding.checkbox;
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    registerBt.setEnabled(false);
                }else {
                    registerBt.setEnabled(true);
                }
            }
        });

    }


    private void initTextView(){
        // 텍스트 필드 초기화
        id_input = mainRegisterActBinding.idInput;
        passwordInput = mainRegisterActBinding.passwordInput;
        passwordCheck = mainRegisterActBinding.passwordCheck;
        nameInput = mainRegisterActBinding.nameInput;
        userPhoneInput = mainRegisterActBinding.userPhoneInput;
        userEmailInput = mainRegisterActBinding.userEmailInput;
    }


    private TextWatcher createTextWatcher(final Consumer<String> onUpdate) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != null&&charSequence != ""){
                    if(charSequence != null && !charSequence.toString().isEmpty()) {
                        String text = charSequence.toString();
                        onUpdate.accept(text);
                        registerVM.registerUserFromApp();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }


    void alertMessage(String msg){
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(msg);
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }


    private void insertProcessAlert(String msg){
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(msg);
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                convertToLogin();
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }


    void initUserCompanySpinner(){
        register_commpany_spinner = mainRegisterActBinding.registerCommpanySpinner;
        registerVM.getCompanyNameListAll().observe(this, strings -> {
            updateSpinner(strings);
        });
    }


    private void updateSpinner(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        register_commpany_spinner.setAdapter(adapter);
    }


    @Override
    public DragAndDropPermissions requestDragAndDropPermissions(DragEvent event) {
        return super.requestDragAndDropPermissions(event);
    }


    @Override
    public void onBackPressed() {
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("입력을 취소하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                convertToLogin();
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }


    private void convertToLogin(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainRegisterActBinding = null;
        alertDialogBuilder = null;
        register_commpany_spinner = null;
        id_input = null;
        passwordInput = null;
        passwordCheck = null;
        nameInput = null;
        userPhoneInput = null;
        userEmailInput = null;
    }


}
