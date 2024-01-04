package com.snd.app.ui.management.write;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.dataUtil.BackPressListener;
import com.snd.app.data.dataUtil.TextManager;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.ManagementSurgeryBinding;
import com.snd.app.domain.treeManagement.TreeSurgeryDTO;
import com.snd.app.ui.management.TreeManagementActivity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

// 외과수술
public class RegisterSurgeryFragment extends TMFragment implements BackPressListener {
    ManagementSurgeryBinding managementSurgeryBinding;
    RegisterSurgeryViewModel registerSurgeryVM;

    private SharedPreferenceManager sharedPreferencesManager;
    private AlertDialog.Builder alertDialogBuilder;
    private TextManager textManager;
    Spinner spinner_business;
    EditText text_pruning;
    EditText text_pruning_note;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        managementSurgeryBinding = DataBindingUtil.inflate(inflater, R.layout.management_surgery, container, false);
        managementSurgeryBinding.setLifecycleOwner(this);
        registerSurgeryVM = new ViewModelProvider(getActivity()).get(RegisterSurgeryViewModel.class);
        managementSurgeryBinding.setVm(registerSurgeryVM);

        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        registerSurgeryVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        textManager = new TextManager();

        return managementSurgeryBinding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTreeSurgeryDTO();

        registerSurgeryVM.Authorization.observe(getActivity(), s -> {
            registerSurgeryVM.loadCompanyBusinessNameList(s, sharedPreferencesManager.getString("company"));
        });

        spinner_business = managementSurgeryBinding.surgeryBusiness;
        registerSurgeryVM.getCompanyBusinessNameList().observe(getViewLifecycleOwner(), this::updateSpinner);


        mappingText();


        registerSurgeryVM.proccess.observe(getActivity(), s -> {
            insertProccess();
        });


        registerSurgeryVM.checkTreeSurgery().observe(getActivity(), s -> {
            String text = (s.equals("success")) ? "저장되었습니다." : "네트워크가 원활하지 않습니다. 다시 시도해주세요.";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            ((TreeManagementActivity)getActivity()).switchFragment(7);
        });


        registerSurgeryVM.back.observe(getActivity(), o -> {
            finishProccess();
        });

    }



    void insertProccess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("등록하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerSurgeryVM.registerSurgery();
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

    void mappingText(){
        if(getView()!=null){
            text_pruning = getView().findViewById(R.id.surgery);
            text_pruning_note = getView().findViewById(R.id.surgery_note);

            text_pruning.addTextChangedListener(textManager.createTextWatcher((text) -> registerSurgeryVM.treeSurgeryDTO.setSurgery(text)));
            text_pruning_note.addTextChangedListener(textManager.createTextWatcher((text) -> registerSurgeryVM.treeSurgeryDTO.setSurgeryNote((text))));
        }
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

    private void updateSpinner(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_business.setAdapter(adapter);
    }



    void setTreeSurgeryDTO(){
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        TreeSurgeryDTO treeSurgeryDTO = new TreeSurgeryDTO();
        treeSurgeryDTO.setNfc(sharedPreferencesManager.getString("idHex"));
        treeSurgeryDTO.setSurgeryCompany(sharedPreferencesManager.getString("company"));
        treeSurgeryDTO.setSurgeryOperator(sharedPreferencesManager.getString("id"));
        treeSurgeryDTO.setSurgeryDate(currentDateTime.toString());
        Log.d(TAG, "시간 포맷 " + currentDateTime);
        registerSurgeryVM.setTextViewModel(treeSurgeryDTO);
    }



    // 입력폼 초기화
    private void resetInputFields() {
        if(getView() != null) {
            text_pruning.setText("");
            text_pruning_note.setText("");
            // 다른 초기화해야 하는 위젯들도 여기에 추가
            spinner_business.setSelection(0);
        }
    }



    @Override
    public boolean onBackPressed() {
        ((TreeManagementActivity)getActivity()).switchFragment(7);
        return true;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        managementSurgeryBinding = null;
        sharedPreferencesManager = null;
        alertDialogBuilder = null;
        spinner_business = null;
        textManager = null;
    }


}
