package com.snd.app.ui.read;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.R;
import com.snd.app.common.TMActivity;
import com.snd.app.data.camera.CameraManager;
import com.snd.app.databinding.ReadActBinding;

import java.io.File;


public class GetTreeInfoActivity extends TMActivity {
    GetTreeInfoViewModel getTreeInfoVM;
    Spinner spinner;
    GetTreeBasicInfoFragment getTreeBasicInfoFr;
    GetTreeBasicInfoViewModel getTreeBasicInfoVM;
    CameraManager cameraManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReadActBinding readActBinding = DataBindingUtil.setContentView(this, R.layout.read_act);
        readActBinding.setLifecycleOwner(this);
        getTreeInfoVM = new GetTreeInfoViewModel();
        readActBinding.setGetTreeInfoVM(getTreeInfoVM);
        getSupportFragmentManager().beginTransaction().replace(R.id.read_content, new NfcReadFragment()).commit();
        getTreeBasicInfoFr = new GetTreeBasicInfoFragment();
        getTreeBasicInfoVM = new ViewModelProvider(this).get(GetTreeBasicInfoViewModel.class);

        // 스피너 설정
        spinner = findViewById(R.id.read_sipnner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.read_act_menu, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        cameraManager = new CameraManager(this);
    }// ./onCreate


    @Override
    protected void onResume() {
        super.onResume();
        getTreeInfoVM.back.observe(this, s -> {
            back();
        });
        getTreeInfoVM.fragmentPageName.observe(this, s -> {
            switchFragment(s);
        });
    }


    public void switchFragment(String fragmentPageName){
        spinner.setVisibility(View.VISIBLE);
        switch (fragmentPageName){
            case "수목 기본 정보":
                getSupportFragmentManager().beginTransaction().replace(R.id.read_content, getTreeBasicInfoFr).commit();
                getTreeInfoVM.readTitle.set("수목 기본 정보");
                break;
            case "위치 상세 정보":
                getSupportFragmentManager().beginTransaction().replace(R.id.read_content, new GetTreeSpecificLocationFragment()).commit();
                getTreeInfoVM.readTitle.set("위치 상세 정보");
                break;
            case "수목 상태 정보":
                getTreeInfoVM.readTitle.set("수목 상태 정보");
                getSupportFragmentManager().beginTransaction().replace(R.id.read_content, new GetTreeStatusFragment()).commit();
                break;
            case "수목 환경 정보":
                getTreeInfoVM.readTitle.set("수목 환경 정보");
                getSupportFragmentManager().beginTransaction().replace(R.id.read_content, new GetEnvironmentFragment()).commit();
                break;
            case "수목 이력 내역":
                getTreeInfoVM.readTitle.set("수목 이력 내역");
                getSupportFragmentManager().beginTransaction().replace(R.id.read_content, new GetManagementHistoryFragment()).commit();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            //Log.d(TAG, "여기서 받니~?? ** " + selectedImageUri);     // content://media/external/images/media/1000001473

            // 잠시 막기 (갤러리 적용시 필요함)
            /*
            File file = cameraManager.uriToFile(this, selectedImageUri);
            getTreeBasicInfoVM.addImageList(file);

             */
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getTreeBasicInfoFr = null;
        if (cameraManager != null) {
            cameraManager.releaseResources();
        }
        cameraManager = null;
    }


}