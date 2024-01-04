package com.snd.app.ui.management.pest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.snd.app.R;
import com.snd.app.adapter.ManagementPestInfoAdapter;
import com.snd.app.common.TMActivity;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.ManagementPestActBinding;
import com.snd.app.domain.tree.dto.TreePestInfoDTO;
import com.snd.app.ui.management.TreeManagementActivity;


public class ManagementPestActivity extends TMActivity implements InputPestInfoDialogFragment.InputDialogListener {
    ManagementPestActBinding managementPestActBinding;
    ManagementPestViewModel managementPestVM;

    SharedPreferenceManager sharedPreferenceManager;
    ManagementPestInfoAdapter pestInfoAdapter;
    RecyclerView reView_management_pest_info;
    TreePestInfoDTO treePestInfoDTO;
    private AlertDialog.Builder alertDialogBuilder;
    TextView textView;

    String idHex;
    String Authorization;
    boolean flag = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        managementPestActBinding = DataBindingUtil.setContentView(this, R.layout.management_pest_act);
        managementPestActBinding.setLifecycleOwner(this);
        managementPestVM = new ViewModelProvider(this).get(ManagementPestViewModel.class);
        managementPestActBinding.setVm(managementPestVM);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(this.getApplicationContext());
        Authorization = sharedPreferenceManager.getString("token");
        managementPestVM.Authorization.setValue(Authorization);
        idHex = sharedPreferenceManager.getString("idHex");
        reView_management_pest_info = managementPestActBinding.managementPestInfo;
        AppCompatTextView bt_management_pest_add = managementPestActBinding.btManagementPestAdd;
        treePestInfoDTO = new TreePestInfoDTO();
        textView = managementPestActBinding.noPestText;

        managementPestVM.Authorization.observe(this, s -> {
            managementPestVM.getTreePetsInfoByNFCtagId(s, idHex);
            managementPestVM.loadPestNameList(Authorization);
        });


        managementPestVM.failCheck().observe(this, s -> {
            // 등록된 병해정보가 없다면
            textView.setVisibility(View.VISIBLE);
        });


        /* Adapter */
        /* 병해 정보 리스트를 가져오는 메서드 -> 등록된 병해가 없으면 아예 호출이 안됨 ! */
        managementPestVM.getPestInfoList().observe(this, objects -> {
            if (pestInfoAdapter == null) {
                pestInfoAdapter = new ManagementPestInfoAdapter(objects);
                reView_management_pest_info.setAdapter(pestInfoAdapter);

            } else {
                // 이것은 기존이 아니라 추가되었을때?
                pestInfoAdapter.notifyDataChange.setValue(objects);
            }

            managementPestVM.getTreePestNameLists().observe(this, strings -> {
                pestInfoAdapter.initTreePestNameList(strings);
            });

            pestInfoAdapter.notifyDataChange.observe(this, objects2 -> {
                if(flag){
                    pestInfoAdapter.addPestInfoItem(objects2);
                    flag = false;
                }
            });

            pestInfoAdapter.modifyProcess.observe(this, dto -> {
                managementPestVM.modifyTreePestInfo(Authorization, dto);
            });
        }); /* ./Adapter */


        // 병해 정보 추가하기 버튼 클릭
        bt_management_pest_add.setOnClickListener(v -> {
            InputPestInfoDialogFragment inputPestInfoDialogFragment = new InputPestInfoDialogFragment();
            inputPestInfoDialogFragment.show(getSupportFragmentManager(), "inputDialog");
        });


        managementPestVM.checkTreePestInfo().observe(this, s -> {
            String text = (s.equals("success")) ? "병해 정보가 등록되었습니다." : "네트워크가 원활하지 않습니다. 다시 시도해주세요.";
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            managementPestVM.getTreePetsInfoByNFCtagId(Authorization, idHex);
        });


        managementPestVM.checkModifyPestInfo().observe(this, s -> {
            String text = (s.equals("success")) ? "수정되었습니다." : "네트워크가 원활하지 않습니다. 다시 시도해주세요.";
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        });


        managementPestVM.back.observe(this, s -> {
            finish();
        });
    }


    @Override
    public void onInputReceived(TreePestInfoDTO treePestInfoDTO) {
        this.treePestInfoDTO = treePestInfoDTO;
        if(checkPestInfoData()){
            insertProcess();
        }else {
            Toast.makeText(this, "입력정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    // 공란 체크
    private boolean checkPestInfoData(){
        return (treePestInfoDTO.getPest() == null || treePestInfoDTO.getOccurred() == null) ? false : true;
    }


    void insertProcess(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("병해 정보를 등록하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                managementPestVM.registerTreePestInfo(treePestInfoDTO);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, TreeManagementActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("flag", true);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        managementPestActBinding = null;
        sharedPreferenceManager = null;
        pestInfoAdapter = null;
        reView_management_pest_info = null;
        alertDialogBuilder = null;
        managementPestVM = null;
        treePestInfoDTO = null;
        textView = null;
    }



}
