package com.snd.app.ui.write;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.MainActivity;
import com.snd.app.R;
import com.snd.app.common.TMActivity;
import com.snd.app.data.camera.PhotoFileManager;
import com.snd.app.databinding.RegisterActBinding;
import com.snd.app.ui.write.pest.RegistTreePestInfoFragment;
import com.snd.app.ui.write.tree.RegistEnvironmentInfoFragment;
import com.snd.app.ui.write.tree.RegistTreeBasicInfoFragment;
import com.snd.app.ui.write.tree.RegistTreeBasicInfoViewModel;
import com.snd.app.ui.write.tree.RegistTreeSpecificLocationInfoFragment;
import com.snd.app.ui.write.tree.RegistTreeStatusInfoFragment;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.File;


public class RegistTreeInfoActivity extends TMActivity implements MapView.POIItemEventListener {
    RegisterActBinding writeActBinding;
    RegistTreeInfoViewModel treeInfoVM;
    RegistTreeBasicInfoViewModel treeBasicInfoVM;
    RegistTreeBasicInfoFragment registTreeBasicInfoFr;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    String submitter;
    String vendor;
    public int num = 0;
    File file;

    PhotoFileManager photoFileManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        writeActBinding = DataBindingUtil.setContentView(this, R.layout.register_act);
        writeActBinding.setLifecycleOwner(this);
        treeInfoVM = new RegistTreeInfoViewModel();
        writeActBinding.setTreeInfoVM(treeInfoVM);
        submitter = sharedPreferences.getString("id", null);
        vendor = sharedPreferences.getString("company", null);
        builder = new AlertDialog.Builder(RegistTreeInfoActivity.this);

        treeBasicInfoVM = new ViewModelProvider(this).get(RegistTreeBasicInfoViewModel.class);
        treeInfoVM.registTitle.set("기본 정보 입력");
        getSupportFragmentManager().beginTransaction().replace(R.id.write_content, new NfcLoadingFragment()).commit();

        registTreeBasicInfoFr = new RegistTreeBasicInfoFragment();
        photoFileManager = new PhotoFileManager();


        // 뒤로가기
        treeInfoVM.back.observe(this, o -> {
            back();
        });

    }   /* ./onCreate */


    public void initMapLoadingFr(){
        //getSupportFragmentManager().beginTransaction().replace(R.id.write_content, new MapLoadingFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.write_content, registTreeBasicInfoFr).commit();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage message = (NdefMessage) rawMessages[0];
                String messageContent = new String(message.getRecords()[0].getPayload());
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // 테스트 위해 잠시 막긍

            // 아 이거 모듈화 각인데
            file = photoFileManager.uriToFile(this, selectedImageUri);
            treeBasicInfoVM.addImageList2(file);
        }
    }


    public void AlertDialog(String title, String message){
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchFragment();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder = new AlertDialog.Builder(RegistTreeInfoActivity.this);
                builder.setTitle("입력을 중단하시겠습니까?");
                builder.setMessage("지금까지 진행 중인 내용만 저장됩니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switchFragment();
                    }
                });
                AlertDialog dialog2 = builder.create();
                dialog2.show();
            }
        });
        dialog = builder.create();
        dialog.show();
    }


    public void switchFragment(){
        switch (num){
            case BASIC :
                getSupportFragmentManager().beginTransaction().replace(R.id.write_content, new RegistTreeSpecificLocationInfoFragment()).commit();
                treeInfoVM.registTitle.set("위치 상세 정보 입력"); break;
            case SPACIFICLOCATION :
                getSupportFragmentManager().beginTransaction().replace(R.id.write_content, new RegistTreeStatusInfoFragment()).commit();
                treeInfoVM.registTitle.set("수목 상태 정보 입력"); break;
            case STATUS :
                getSupportFragmentManager().beginTransaction().replace(R.id.write_content, new RegistEnvironmentInfoFragment()).commit();
                treeInfoVM.registTitle.set("환경 정보 입력"); break;
            case ENVIRONMENT :
                // 메인화면으로 인텐트 전환!!
                Intent intent = new Intent(RegistTreeInfoActivity.this, MainActivity.class);
                startActivity(intent); break;
            case PEST :
                getSupportFragmentManager().beginTransaction().replace(R.id.write_content, new RegistTreePestInfoFragment()).commit();
                treeInfoVM.registTitle.set("병해 정보 입력"); break;
        }
    }


    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
    }
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
    }
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
    }
    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //log("** 액티비티 - onDestroy 호출 **");
        registTreeBasicInfoFr = null;
        writeActBinding = null;
        builder = null;
        if(dialog != null) {
            dialog.dismiss();
        }
        photoFileManager = null;
        file = null;
    }


}