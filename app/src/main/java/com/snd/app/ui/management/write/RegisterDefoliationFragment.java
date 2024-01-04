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
import com.snd.app.databinding.ManagementDefoliationBinding;
import com.snd.app.domain.treeManagement.TreeDefoliationDTO;
import com.snd.app.ui.management.TreeManagementActivity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RegisterDefoliationFragment extends TMFragment implements BackPressListener {
    ManagementDefoliationBinding managementDefoliationBinding;
    RegisterDefoliationViewModel registDefoliationVM;
    private SharedPreferenceManager sharedPreferencesManager;
    private TextManager textManager;
    private AlertDialog.Builder alertDialogBuilder;
    Spinner spinner_business;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        managementDefoliationBinding = DataBindingUtil.inflate(inflater, R.layout.management_defoliation, container, false);
        managementDefoliationBinding.setLifecycleOwner(this);
        registDefoliationVM = new ViewModelProvider(getActivity()).get(RegisterDefoliationViewModel.class);
        managementDefoliationBinding.setVm(registDefoliationVM);

        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        registDefoliationVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        textManager = new TextManager();

        return managementDefoliationBinding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTreeDefoliationDTO();

        registDefoliationVM.Authorization.observe(getActivity(), s -> {
            registDefoliationVM.loadCompanyBusinessNameList(s, sharedPreferencesManager.getString("company"));
        });

        spinner_business = managementDefoliationBinding.defoliationBusiness;
        registDefoliationVM.getCompanyBusinessNameList().observe(getViewLifecycleOwner(), this::updateSpinner);

        mappingText();

        registDefoliationVM.proccess.observe(getActivity(), s -> {
            insertProccess();
        });

        registDefoliationVM.checkTreeDefoliation().observe(getActivity(), s -> {
            String text = (s.equals("success")) ? "저장되었습니다." : "네트워크가 원활하지 않습니다. 다시 시도해주세요.";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            ((TreeManagementActivity)getActivity()).switchFragment(7);
        });

        registDefoliationVM.back.observe(getActivity(), o -> {
            finishProccess();
        });
    }



    void insertProccess(){
        if(getContext() != null){
            alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("등록하시겠습니까?");
            alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    registDefoliationVM.registerDefoliation();
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



    /* --------------------------------------------- EditText --------------------------------------------- */

    void setTreeDefoliationDTO(){
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        TreeDefoliationDTO treeDefoliationDTO = new TreeDefoliationDTO();
        treeDefoliationDTO.setNfc(sharedPreferencesManager.getString("idHex"));
        treeDefoliationDTO.setDefoliationCompany(sharedPreferencesManager.getString("company"));
        treeDefoliationDTO.setDefoliationOperator(sharedPreferencesManager.getString("id"));
        treeDefoliationDTO.setDefoliationDate(currentDateTime.toString());
        registDefoliationVM.setTextViewModel(treeDefoliationDTO);
    }

    void mappingText(){
        if(getView()!=null){
            EditText text_pruning = getView().findViewById(R.id.defoliation);
            EditText text_pruning_note = getView().findViewById(R.id.defoliation_note);

            text_pruning.addTextChangedListener(textManager.createTextWatcher((text) -> registDefoliationVM.treeDefoliationDTO.setDefoliation(text)));
            text_pruning_note.addTextChangedListener(textManager.createTextWatcher((text) -> registDefoliationVM.treeDefoliationDTO.setDefoliationNote((text))));
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
        managementDefoliationBinding = null;
        sharedPreferencesManager = null;
        alertDialogBuilder = null;
        textManager = null;
    }


}
