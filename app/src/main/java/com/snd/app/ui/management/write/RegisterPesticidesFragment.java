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
import com.snd.app.databinding.ManagementPesticidesBinding;
import com.snd.app.domain.treeManagement.TreePesticidesDTO;
import com.snd.app.ui.management.TreeManagementActivity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RegisterPesticidesFragment extends TMFragment implements BackPressListener {
    ManagementPesticidesBinding managementPesticidesBinding;
    RegisterPesticidesViewModel registPesticidesVM;

    private SharedPreferenceManager sharedPreferencesManager;
    private AlertDialog.Builder alertDialogBuilder;
    private TextManager textManager;
    Spinner spinner_business;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        managementPesticidesBinding = DataBindingUtil.inflate(inflater, R.layout.management_pesticides, container, false);
        managementPesticidesBinding.setLifecycleOwner(this);
        registPesticidesVM = new ViewModelProvider(getActivity()).get(RegisterPesticidesViewModel.class);
        managementPesticidesBinding.setVm(registPesticidesVM);

        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        registPesticidesVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        textManager = new TextManager();

        return managementPesticidesBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTreePesticidesDTO();


        registPesticidesVM.Authorization.observe(getActivity(), s -> {
            registPesticidesVM.loadCompanyBusinessNameList(s, sharedPreferencesManager.getString("company"));
        });

        spinner_business = managementPesticidesBinding.pesticidesBusiness;
        registPesticidesVM.getCompanyBusinessNameList().observe(getViewLifecycleOwner(), this::updateSpinner);


        mappingText();


        registPesticidesVM.proccess.observe(getActivity(), s -> {
            insertProccess();
        });


        registPesticidesVM.checkTreePesticides().observe(getActivity(), s -> {
            String text = (s.equals("success")) ? "저장되었습니다." : "네트워크가 원활하지 않습니다. 다시 시도해주세요.";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            ((TreeManagementActivity)getActivity()).switchFragment(7);
        });


        registPesticidesVM.back.observe(getActivity(), o -> {
            finishProccess();
        });

    }



    void insertProccess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("등록하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registPesticidesVM.registerPesticides();
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



    void setTreePesticidesDTO(){
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        TreePesticidesDTO treePesticidesDTO = new TreePesticidesDTO();
        treePesticidesDTO.setNfc(sharedPreferencesManager.getString("idHex"));
        treePesticidesDTO.setPesticidesCompany(sharedPreferencesManager.getString("company"));
        treePesticidesDTO.setPesticidesOperator(sharedPreferencesManager.getString("id"));
        treePesticidesDTO.setPesticidesDate(currentDateTime.toString());
        registPesticidesVM.setTextViewModel(treePesticidesDTO);
    }


    void mappingText(){
        if(getView()!=null){
            EditText text_pruning = getView().findViewById(R.id.pesticides);
            EditText text_pruning_note = getView().findViewById(R.id.pesticides_note);

            text_pruning.addTextChangedListener(textManager.createTextWatcher((text) -> registPesticidesVM.treePesticidesDTO.setPesticides(text)));
            text_pruning_note.addTextChangedListener(textManager.createTextWatcher((text) -> registPesticidesVM.treePesticidesDTO.setPesticidesNote((text))));
        }
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

    private void updateSpinner(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_business.setAdapter(adapter);
    }


    @Override
    public boolean onBackPressed() {
        ((TreeManagementActivity)getActivity()).switchFragment(7);
        return true;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        managementPesticidesBinding = null;
        sharedPreferencesManager = null;
        alertDialogBuilder = null;
        spinner_business = null;
        textManager = null;
    }



}
