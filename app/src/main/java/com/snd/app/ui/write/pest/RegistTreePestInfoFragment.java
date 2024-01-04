package com.snd.app.ui.write.pest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.adapter.PestFormAdapter;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.RegistTreePsetInfoFrBinding;
import com.snd.app.domain.tree.dto.TreePestInfoDTO;
import com.snd.app.ui.write.RegistTreeInfoActivity;

import java.util.ArrayList;


public class RegistTreePestInfoFragment extends TMFragment {
    RegistTreePsetInfoFrBinding registTreePsetInfoFrBinding;
    RegistTreePestInfoViewModel registTreePestInfoVM;
    SharedPreferenceManager sharedPreferenceManager;
    RecyclerView tree_pest_recyclerView;
    PestFormAdapter pestFormAdapter;

    TreePestInfoDTO treePestInfoDTO;
    public MutableLiveData<TreePestInfoDTO> treePestInfoData = new MutableLiveData<>();
    private AlertDialog.Builder alertDialogBuilder;
    public Boolean flag = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registTreePsetInfoFrBinding = DataBindingUtil.inflate(inflater, R.layout.regist_tree_pset_info_fr, container, false);
        registTreePsetInfoFrBinding.setLifecycleOwner(this);
        registTreePestInfoVM = new ViewModelProvider(getActivity()).get(RegistTreePestInfoViewModel.class);
        registTreePsetInfoFrBinding.setVm(registTreePestInfoVM);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getContext());
        registTreePestInfoVM.Authorization.setValue(sharedPreferenceManager.getString("token"));
        tree_pest_recyclerView = registTreePsetInfoFrBinding.treePestView;

        return registTreePsetInfoFrBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registTreePestInfoVM.Authorization.observe(getActivity(), s -> {
            registTreePestInfoVM.loadPestNameList();
        });


        pestFormAdapter();


        // 리스트 추가
        treePestInfoData.observe(getActivity(), dto -> {
            Log.d(TAG, "이거 뭐인지 확인 " + dto);
            if(!registTreePestInfoVM.treePestInfoList.contains(dto)){
                registTreePestInfoVM.treePestInfoList.add(dto);
                pestFormAdapter.treePestInfoList.add(dto);
            }
        });


        registTreePestInfoVM.checkTreePestInfo().observe(getActivity(), s -> {
            if(s.equals("success")){
                getView().findViewById(R.id.treeStatus_write_cancel).setEnabled(false);
                Toast.makeText(getContext(), "병해정보가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                ((RegistTreeInfoActivity)getActivity()).num = 3;
                ((RegistTreeInfoActivity)getActivity()).switchFragment();
            }else {
                Toast.makeText(getContext(), "오류가 발생했습니다. 처음으로 돌아가주세요.", Toast.LENGTH_SHORT).show();
            }
        });


        // 뒤로가기
        registTreePestInfoVM.back.observe(getActivity(), s -> {
            // 다시 돌아가기 or 다음 단계로 넘어가기
            cancelProccess();
        });

    }// ./onViewCreated


    private void pestFormAdapter(){
        registTreePestInfoVM.getTreePestNameLists().observe(getActivity(), strings -> {
            pestFormAdapter = new PestFormAdapter(strings);
            tree_pest_recyclerView.setAdapter(pestFormAdapter);

            // 입력폼 추가 버튼
            registTreePestInfoVM.addPestInfoBox.observe(getActivity(), aBoolean -> {
                if(aBoolean) {
                    // nfc 매핑하여 넘겨주기
                    treePestInfoDTO = mappingDTO();
                    pestFormAdapter.addPestItemBox("", treePestInfoDTO);
                }
            });

            // 등록 버튼을 누르면
            registTreePestInfoVM.insertProcess.observe(getActivity(), s -> {
                if(!checkDTOData()){
                    Toast.makeText(getContext(), "입력사항을 모두 기재해주세요. ", Toast.LENGTH_SHORT).show();
                }else {
                    insertProcess();
                }
            });

        });
    }


    private boolean checkDTOData(){
        boolean flag = false;

        ArrayList<TreePestInfoDTO> collectedData = pestFormAdapter.getCurrentData();
        Log.d(TAG, "저장할 데이터 리스트 " + collectedData);
        registTreePestInfoVM.treePestInfoList = collectedData;

        for (TreePestInfoDTO dto : collectedData){
            Log.d(TAG, "pestInfoList 꺼내기 "+ dto);
            flag = (dto.getPest() == null || dto.getOccurred() == null ? false : true);
        }
        return flag;
    }


    private TreePestInfoDTO mappingDTO(){
        TreePestInfoDTO treePestInfoDTO = new TreePestInfoDTO();
        treePestInfoDTO.setNfc(sharedPreferenceManager.getString("idHex"));
        treePestInfoDTO.setSubmitter(sharedPreferenceManager.getString("id"));
        treePestInfoDTO.setVendor(sharedPreferenceManager.getString("company"));
        return treePestInfoDTO;
    }


    void insertProcess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("병해 정보를 등록하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registTreePestInfoVM.registerTreePestInfo();
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


    private void cancelProccess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("입력 중인 내용을 취소하시겠습니까?");
        alertDialogBuilder.setMessage("현재 페이지에서 입력 중인 내용은 저장되지 않습니다.");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((RegistTreeInfoActivity)getActivity()).num = 3;
                ((RegistTreeInfoActivity)getActivity()).switchFragment();
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
    public void onDestroyView() {
        super.onDestroyView();
        registTreePsetInfoFrBinding = null;
        sharedPreferenceManager = null;
        tree_pest_recyclerView = null;
        pestFormAdapter = null;
        treePestInfoDTO = null;
        alertDialogBuilder = null;
    }



}
