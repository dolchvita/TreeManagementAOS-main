package com.snd.app.ui.management.write;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.snd.app.databinding.ManagementHorticultureBinding;
import com.snd.app.domain.treeManagement.TreeHorticultureDTO;
import com.snd.app.domain.treeManagement.TreeSurgeryDTO;
import com.snd.app.ui.management.TreeManagementActivity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RegisterHorticultureFragment extends TMFragment implements BackPressListener {
    ManagementHorticultureBinding managementHorticultureBinding;
    RegisterHorticultureViewModel registerHorticultureVM;

    private SharedPreferenceManager sharedPreferencesManager;
    private AlertDialog.Builder alertDialogBuilder;
    private TextManager textManager;
    Spinner spinner_business;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        managementHorticultureBinding = DataBindingUtil.inflate(inflater, R.layout.management_horticulture, container, false);
        managementHorticultureBinding.setLifecycleOwner(this);
        registerHorticultureVM = new ViewModelProvider(getActivity()).get(RegisterHorticultureViewModel.class);
        managementHorticultureBinding.setVm(registerHorticultureVM);

        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        registerHorticultureVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        textManager = new TextManager();

        return managementHorticultureBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTreeSurgeryDTO();


        registerHorticultureVM.Authorization.observe(getActivity(), s -> {
            registerHorticultureVM.loadCompanyBusinessNameList(s, sharedPreferencesManager.getString("company"));
        });

        spinner_business = managementHorticultureBinding.horticultureBusiness;
        registerHorticultureVM.getCompanyBusinessNameList().observe(getViewLifecycleOwner(), this::updateSpinner);


        mappingText();


        registerHorticultureVM.proccess.observe(getActivity(), s -> {
            insertProccess();
        });


        registerHorticultureVM.checkTreeHorticulture().observe(getActivity(), s -> {
            String text = (s.equals("success")) ? "저장되었습니다." : "네트워크가 원활하지 않습니다. 다시 시도해주세요.";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            ((TreeManagementActivity)getActivity()).switchFragment(7);
        });


        registerHorticultureVM.back.observe(getActivity(), o -> {
            finishProccess();
        });


    }


    void insertProccess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("등록하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerHorticultureVM.registerHorticulture();
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
            EditText text_pruning = getView().findViewById(R.id.horticulture);
            EditText text_pruning_note = getView().findViewById(R.id.horticulture_note);

            text_pruning.addTextChangedListener(textManager.createTextWatcher((text) -> registerHorticultureVM.treeHorticultureDTO.setHorticulture(text)));
            text_pruning_note.addTextChangedListener(textManager.createTextWatcher((text) -> registerHorticultureVM.treeHorticultureDTO.setHorticultureNote((text))));
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
        TreeHorticultureDTO treeHorticultureDTO = new TreeHorticultureDTO();
        treeHorticultureDTO.setNfc(sharedPreferencesManager.getString("idHex"));
        treeHorticultureDTO.setHorticultureCompany(sharedPreferencesManager.getString("company"));
        treeHorticultureDTO.setHorticultureOperator(sharedPreferencesManager.getString("id"));
        treeHorticultureDTO.setHorticultureDate(currentDateTime.toString());
        registerHorticultureVM.setTextViewModel(treeHorticultureDTO);
    }


    @Override
    public boolean onBackPressed() {
        ((TreeManagementActivity)getActivity()).switchFragment(7);
        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        managementHorticultureBinding = null;
        sharedPreferencesManager = null;
        alertDialogBuilder = null;
        spinner_business = null;
        textManager = null;
    }


}
