package com.snd.app;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.snd.app.common.TMActivity;
import com.snd.app.data.dataUtil.BackPressListener;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.MainActBinding;
import com.snd.app.repository.business.BusinessFragment;
import com.snd.app.repository.business.ProjectFragment;
import com.snd.app.ui.home.HomeFragment;
import com.snd.app.ui.login.LoginActivity;
import com.snd.app.ui.map.Mapfragment;

public class MainActivity extends TMActivity {
    MainActBinding mainActBinding;
    MainViewModel mainVM;
    private AlertDialog.Builder alertDialogBuilder;
    SharedPreferenceManager sharedPreferencesManager;
    ProjectFragment projectFragment;
    public AppCompatImageView backButton;   // 뒤로가기 버튼
    public boolean isNfcPermissionGranted = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActBinding = DataBindingUtil.setContentView(this, R.layout.main_act);
        mainVM = new ViewModelProvider(this).get(MainViewModel.class);
        mainActBinding.setLifecycleOwner(this);
        mainActBinding.setMainVM(mainVM);
        mainVM = mainActBinding.getMainVM();
        sharedPreferencesManager = SharedPreferenceManager.getInstance(this.getApplicationContext());

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();

        projectFragment = new ProjectFragment();
        backButton = mainActBinding.writeBack;

        mainVM.tabClick.observe(this, integer -> {
            switchFragment(integer);
        });

        mainVM.logout.observe(this, s -> {
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("로그아웃 하시겠습니까?");
            alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sharedPreferencesManager.clear();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        });
    }


    public void switchFragment(int tabClick){
        switch (tabClick){
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new Mapfragment()).commit();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new BusinessFragment()).commit();
                break;
            case 4:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, projectFragment).commit();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (projectFragment instanceof BackPressListener && ((BackPressListener) projectFragment).onBackPressed()){
        }else {
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("프로그램을 종료하시겠습니까?");
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
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferencesManager = null;
        alertDialogBuilder = null;
        projectFragment = null;
        mainActBinding = null;
        backButton = null;
    }


}