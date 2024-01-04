package com.snd.app.ui.management;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.R;
import com.snd.app.common.TMActivity;
import com.snd.app.data.dataUtil.BackPressListener;
import com.snd.app.databinding.ManagementActBinding;
import com.snd.app.ui.management.write.NfcReadingFragment;
import com.snd.app.ui.management.write.RegisterDefoliationFragment;
import com.snd.app.ui.management.write.RegisterFertilizationFragment;
import com.snd.app.ui.management.write.RegisterHorticultureFragment;
import com.snd.app.ui.management.write.RegisterMiscellaneousFragment;
import com.snd.app.ui.management.write.RegisterPesticidesFragment;
import com.snd.app.ui.management.write.RegisterPruningFragment;
import com.snd.app.ui.management.write.RegisterSurgeryFragment;

import java.util.List;

public class TreeManagementActivity extends TMActivity {
    ManagementActBinding treeManagementActBinding;
    TreeManagementViewModel treeManagementVM;
    boolean _back = false;
    public MutableLiveData <Boolean> back = new MutableLiveData<>(false);

    // 모든 프레그먼트를 스택에 존재시켜야 오류없이 잘 가동
    RegisterPruningFragment registerPruningFr;
    RegisterDefoliationFragment registerDefoliationFr;
    RegisterPesticidesFragment registerPesticidesFr;
    RegisterSurgeryFragment registerSurgeryFr;
    RegisterHorticultureFragment registerHorticultureFr;
    RegisterFertilizationFragment registerFertilizationFr;
    RegisterMiscellaneousFragment registerMiscellaneousFr;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        treeManagementActBinding = DataBindingUtil.setContentView(this, R.layout.management_act);
        treeManagementActBinding.setLifecycleOwner(this);
        treeManagementVM = new ViewModelProvider(this).get(TreeManagementViewModel.class);
        treeManagementActBinding.setVm(treeManagementVM);

        boolean flag = getIntent().getBooleanExtra("flag", false);
        if(flag){
            getSupportFragmentManager().beginTransaction().replace(R.id.tree_management_content, new TreeManagementMenuFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.tree_management_content, new NfcReadingFragment()).commit();
        }
        treeManagementVM.registTitle.set("수목 이력 관리");

        initFragment();

        treeManagementVM.back.observe(this, s -> {
            finishProccess();
        });
    }


    public void switchFragment(int num){
        _back = false;
        switch (num){
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.tree_management_content, registerPruningFr).commit();
                treeManagementVM.registTitle.set("가지치기 내역 등록"); break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.tree_management_content, registerDefoliationFr).commit();
                treeManagementVM.registTitle.set("맹아제거 내역 등록"); break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.tree_management_content, registerPesticidesFr).commit();
                treeManagementVM.registTitle.set("병충해 방제 내역 등록"); break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.tree_management_content, registerSurgeryFr).commit();
                treeManagementVM.registTitle.set("외과수술 내역 등록"); break;
            case 4:
                getSupportFragmentManager().beginTransaction().replace(R.id.tree_management_content, registerHorticultureFr).commit();
                treeManagementVM.registTitle.set("생육환경개선 내역 등록"); break;
            case 5:
                getSupportFragmentManager().beginTransaction().replace(R.id.tree_management_content, registerFertilizationFr).commit();
                treeManagementVM.registTitle.set("비료주기 내역 등록"); break;
            case 6:
                getSupportFragmentManager().beginTransaction().replace(R.id.tree_management_content, registerMiscellaneousFr).commit();
                treeManagementVM.registTitle.set("기타관리 정보 내역 등록"); break;

            // 메인 메뉴
            case 7:
                getSupportFragmentManager().beginTransaction().replace(R.id.tree_management_content, new TreeManagementMenuFragment()).commit();
                treeManagementVM.registTitle.set("수목 이력 관리");
                _back = true; break;
        }
    }


    private void initFragment(){
        registerPruningFr = new RegisterPruningFragment();
        registerDefoliationFr = new RegisterDefoliationFragment();
        registerPesticidesFr = new RegisterPesticidesFragment();
        registerSurgeryFr = new RegisterSurgeryFragment();
        registerHorticultureFr = new RegisterHorticultureFragment();
        registerFertilizationFr = new RegisterFertilizationFragment();
        registerMiscellaneousFr = new RegisterMiscellaneousFragment();
    }


    private void finishProccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(TreeManagementActivity.this);
        builder.setTitle("뒤로 나가시겠습니까?");
        builder.setMessage("");
        builder.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof BackPressListener && ((BackPressListener) fragment).onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        treeManagementActBinding = null;
        if (treeManagementVM != null && treeManagementVM.back != null) {
            treeManagementVM.back.removeObservers(this);
        }
        registerPruningFr = null;
        registerDefoliationFr = null;
        registerPesticidesFr = null;
        registerSurgeryFr = null;
        registerHorticultureFr = null;
        registerFertilizationFr = null;
        registerMiscellaneousFr = null;
    }


}
