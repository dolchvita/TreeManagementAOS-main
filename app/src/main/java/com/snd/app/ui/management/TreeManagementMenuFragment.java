package com.snd.app.ui.management;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.snd.app.R;
import com.snd.app.adapter.TreeManagementMenuAdapter;
import com.snd.app.common.TMFragment;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.ManagementMenuFrBinding;
import com.snd.app.domain.treeManagement.ManagementMenuItem;
import com.snd.app.ui.management.pest.ManagementPestActivity;

import java.util.ArrayList;
import java.util.List;



public class TreeManagementMenuFragment extends TMFragment {
    ManagementMenuFrBinding treeManagementMenuFrBinding;
    TreeManagementMenuViewModel treeManagementMenuVM;
    RecyclerView tree_pest_view;
    TreeManagementMenuAdapter manuAdapter;
    SharedPreferenceManager sharedPreferenceManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        treeManagementMenuFrBinding = DataBindingUtil.inflate(inflater, R.layout.management_menu_fr, container, false);
        treeManagementMenuFrBinding.setLifecycleOwner(this);
        treeManagementMenuVM = new ViewModelProvider(getActivity()).get(TreeManagementMenuViewModel.class);
        treeManagementMenuFrBinding.setVm(treeManagementMenuVM);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());

        treeManagementMenuVM.Authorization.setValue(sharedPreferenceManager.getString("token"));
        treeManagementMenuVM.idHex.setValue(sharedPreferenceManager.getString("idHex"));

        return treeManagementMenuFrBinding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 메뉴 채우기
        tree_pest_view = treeManagementMenuFrBinding.treePestView;
        List<ManagementMenuItem> menuList = fillData();
        manuAdapter = new TreeManagementMenuAdapter(menuList);
        tree_pest_view.setAdapter(manuAdapter);


        treeManagementMenuVM.idHex.observe(getActivity(), s -> {
            treeManagementMenuVM.getTreeInfoByNFCtagId();
        });


        // NFC 권한 체크 여기가 아닌가?
        treeManagementMenuVM.failCheck().observe(getActivity(), s -> {
            Toast.makeText(getContext(), "허용된 nfc 칩이 아닙니다. ", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            Log.d(TAG, "** 체킹 **");

        });


        // 병충해 페이지
        treeManagementMenuVM.click.observe(getActivity(), s -> {
            if(getContext()!=null){
                Intent intent = new Intent(getContext(), ManagementPestActivity.class);
                intent.putExtra("idHex", sharedPreferenceManager.getString("idHex"));
                startActivity(intent);
            }
        });


        manuAdapter.menuClick.observe(getActivity(), integer -> {
            ((TreeManagementActivity)getActivity()).switchFragment(integer);
        });

    }   /* ./onViewCreated */



    private List<ManagementMenuItem> fillData() {
        String[] menuName={"가지치기", "맹아제거", "병충해방제", "외과수술", "생육환경개선", "비료주기", "기타관리정보"};
        List<ManagementMenuItem> menuList = new ArrayList<>();
        for(String name: menuName){
            menuList.add(new ManagementMenuItem(name));
        }
        return menuList;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        treeManagementMenuFrBinding = null;
        sharedPreferenceManager = null;
        tree_pest_view =  null;
        manuAdapter = null;
    }


}
