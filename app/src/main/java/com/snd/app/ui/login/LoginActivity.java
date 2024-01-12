package com.snd.app.ui.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.MainActivity;
import com.snd.app.R;
import com.snd.app.common.TMActivity;
import com.snd.app.databinding.MainLoginBinding;
import com.snd.app.domain.user.UserDTO;
import com.snd.app.domain.login.UserCredentialsVO;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.ui.user.RegisterActivity;


public class LoginActivity extends TMActivity {
    MainLoginBinding mainLoginBinding;
    private String APK_VERSION = "1.0.8";       // 안드로이드 스튜디오 전역에서 참조 가능한 문자열이 있지 않을까? (xml, java)
    AlertDialog dialog;
    SharedPreferenceManager sharedPreferenceManager;
    UserCredentialsVO userCredentialsVO;
    LoginViewModel loginVM;
    AlertDialog.Builder alertDialogBuilder;

    EditText t_id;
    EditText t_pass;
    String token;
    String id;
    ImageView imageView;
    Bitmap bitmap;
    Button bt_login;
    Button bt_register;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLoginBinding = DataBindingUtil.setContentView(this, R.layout.main_login);
        mainLoginBinding.setLifecycleOwner(this);
        loginVM = new ViewModelProvider(this).get(LoginViewModel.class);
        mainLoginBinding.setLoginVM(loginVM);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getApplicationContext());
        alertDialogBuilder = new AlertDialog.Builder(this);
        /* 로그인 화면 */
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_login_logo);
        imageView = findViewById(R.id.login_image);
        imageView.setImageBitmap(bitmap);
        /* 입력 데이터 */
        t_id = mainLoginBinding.idInput;
        t_pass = mainLoginBinding.passInput;
        bt_login = mainLoginBinding.btLogin;
        bt_register = mainLoginBinding.btRegister;


        if (checkAutoLogin()) {
            startActivity("MainActivity");
        }


        // AOS 앱 버전 정보 확인
        loginVM.loadCurrentApkInfo();


        loginVM.getCurrentApkInfo().observe(this, s -> {
            if(!s.equals(APK_VERSION)){
                loginVM.loadCurrentDownloadLink();
            }
        });


        loginVM.getCurrentDownloadLink().observe(this, s -> {
            popUpMessageToCurrentDownloadLink(s);
        });


        bt_login.setOnClickListener((v) ->{
            loginRequest();
        });


        // 사용자 등록 버튼
        bt_register.setOnClickListener((v) ->{
            startActivity("RegisterActivity");
        });


        // 로그인 시도 Callback
        loginVM.getToken().observe(this, s -> {
            log("로그인 시도 콜백 ** " + s);
            if(s.equals("fail")) {
                Toast.makeText(this, "로그인 정보가 올바르지 않습니다.\n올바른 정보를 다시 입력하세요.", Toast.LENGTH_SHORT).show();
            }else {
                validationCheckRequest(s);
            }
        });


        // 토큰 유효성 Callback
        loginVM.getValidation().observe(this, s -> {
            loginVM.loadUserDTO(token, id);
        });


        loginVM.getUserDTO().observe(this, userDTO -> {
            log("유저 콜백 ** " +userDTO);

            saveUserInfo(userDTO);
        });

    } // ./onCreate


    public boolean checkAutoLogin() {
        long loginTime = sharedPreferenceManager.getLong("loginTime");
        long currentTime = System.currentTimeMillis();
        // Login Auto Sataus (15m)
        return ((currentTime - loginTime) <= 900000) ? true : false;
    }


    // 로그인 요청
    public void loginRequest() {
        userCredentialsVO = new UserCredentialsVO();
        id = t_id.getText().toString();

        userCredentialsVO.setId(id);
        userCredentialsVO.setPassword(t_pass.getText().toString());
        loginVM.loadLoginRequest(userCredentialsVO);
    }


    // 토큰 유효성 체크
    public void validationCheckRequest(String token){
        this.token = token;
        loginVM.loadValidation(token);

        long currentTime = System.currentTimeMillis();
        if (sharedPreferenceManager != null){
            sharedPreferenceManager.saveString("token", token);
            sharedPreferenceManager.saveLong("loginTime", currentTime);
        }
    }


    public void saveUserInfo(UserDTO user){
        if(sharedPreferenceManager != null){
            sharedPreferenceManager.saveString("company", user.getCompany());
            sharedPreferenceManager.saveString("name",user.getName());
            sharedPreferenceManager.saveString("id",user.getId());
        }
        startActivity("MainActivity");
    }


    public void startActivity(String activity){
        if(activity.equals("MainActivity")){
            intent = new Intent(this, MainActivity.class);
        } else if (activity.equals("RegisterActivity")) {
            intent = new Intent(this, RegisterActivity.class);
        }
        startActivity(intent);
        finish();
    }


    void popUpMessageToCurrentDownloadLink(String url){
        alertDialogBuilder.setTitle("수목관리 시스템이 업데이트 되었습니다.");
        alertDialogBuilder.setMessage("기존 앱 삭제 후 최신 버전으로 다운받으세요.");
        alertDialogBuilder.setNeutralButton("다운로드 바로가기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                System.exit(0);
            }
        });

        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        if(dialog != null) {
            dialog = alertDialogBuilder.create();
            dialog.show();
        }
    }


    @Override
    public void onBackPressed() {
        alertDialogBuilder.setTitle("프로그램을 종료하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginActivity.super.onBackPressed();
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
            dialog = alertDialogBuilder.create();
            dialog.show();
        //dismissDialog();
    }


    void dismissDialog(){
        alertDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialog = null;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginVM.setDisposables();   //리소스 해제
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        mainLoginBinding = null;            // 바인딩 객체는 null할당하기
        imageView = null;
        bt_register = null;
        bt_login = null;
        bitmap = null;
        intent = null;
        t_pass = null;
        t_id = null;
    }


}