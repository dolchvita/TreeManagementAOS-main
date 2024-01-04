package com.snd.app.ui.management.pest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.R;
import com.snd.app.common.TMDialogFragment;
import com.snd.app.data.dataUtil.DatePickerHelper;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.ManagementPestDialogBinding;
import com.snd.app.domain.tree.dto.TreePestInfoDTO;

import java.util.List;

public class InputPestInfoDialogFragment extends TMDialogFragment {
   ManagementPestDialogBinding managementPestDialogBinding;
    private InputDialogListener listener;
    ManagementPestViewModel managementPestVM;
    SharedPreferenceManager sharedPreferenceManager;
    private Spinner pest_dialog_spinner;
    private Spinner pest_dialog_severity_spinner;
    AppCompatEditText pest_dialog_name;
    AppCompatTextView pest_dialog_recovered_tv; // 완치일
    AppCompatEditText pest_dialog_recovered;
    private TreePestInfoDTO treePestInfoDTO;


    public interface InputDialogListener {
        void onInputReceived(TreePestInfoDTO treePestInfoDTO);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        managementPestDialogBinding = DataBindingUtil.inflate(inflater, R.layout.management_pest_dialog, null, false);
        managementPestVM = new ViewModelProvider(this).get(ManagementPestViewModel.class);
        managementPestDialogBinding.setVm(managementPestVM);
        managementPestDialogBinding.setLifecycleOwner(this);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        String Authorization = sharedPreferenceManager.getString("token");
        managementPestVM.Authorization.setValue(Authorization);
        managementPestVM.loadPestNameList(Authorization);

        treePestInfoDTO = new TreePestInfoDTO();
        mappingDTO();

        builder.setView(managementPestDialogBinding.getRoot())
                .setTitle("병충해 추가 입력")
                .setNegativeButton("취소", (dialog, which) -> {})
                .setPositiveButton("확인", (dialog, which) -> {
                    listener.onInputReceived(treePestInfoDTO);
                });


        initPestList();
        initSeverityList();


        managementPestVM.datePicker.observe(getActivity(), s -> {
            setOnDatePicker(s);
        });

        return builder.create();
    }


    private void mappingDTO(){
        treePestInfoDTO.setNfc(sharedPreferenceManager.getString("idHex"));
        treePestInfoDTO.setSubmitter(sharedPreferenceManager.getString("id"));
        treePestInfoDTO.setVendor(sharedPreferenceManager.getString("company"));
        Log.d(TAG, "매핑된 디티오 " + treePestInfoDTO);
        managementPestVM.treePestInfoDTO = treePestInfoDTO;
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

    private void initPestList() {
        pest_dialog_spinner = managementPestDialogBinding.pestDialogSpinner;
        pest_dialog_name = managementPestDialogBinding.pestDialogName;
        managementPestVM.getTreePestNameLists().observe(this, this::settingSpinnerItem);
        managementPestVM.showEditText.observe(getActivity(), aBoolean -> {
            inputSpecies();
        });
    }


    private void initSeverityList() {
        pest_dialog_severity_spinner = managementPestDialogBinding.pestDialogSeveritySpinner;   // 심각도
        pest_dialog_recovered_tv = managementPestDialogBinding.pestDialogRecoveredTv;
        pest_dialog_recovered = managementPestDialogBinding.pestDialogRecovered;
        String[] severityArray = getContext().getResources().getStringArray(R.array.treePest_severity);
        ArrayAdapter<String> severityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, severityArray);
        severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pest_dialog_severity_spinner.setAdapter(severityAdapter);
    }


    private void settingSpinnerItem(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pest_dialog_spinner.setAdapter(adapter);
    }


    private void setOnDatePicker(String s){
        // 발생일
        if(s.equals("occurred")){
            DatePickerHelper.showDatePickerDialog(getContext(), selectedDate -> {
                treePestInfoDTO.setOccurred(selectedDate);
                managementPestVM.occurred.set(selectedDate);
            });


        // 완치일
        } else if (s.equals("recovered")) {
            DatePickerHelper.showDatePickerDialog(getContext(), selectedDate -> {
                treePestInfoDTO.setRecovered(selectedDate);
                managementPestVM.recovered.set(selectedDate);
            });
        }


        managementPestVM.showEditText.observe(getActivity(), show -> {
            pest_dialog_recovered_tv.setVisibility(show ? View.VISIBLE : View.GONE);
            pest_dialog_recovered.setVisibility(show ? View.VISIBLE : View.GONE);
        });
    }


    /* --------------------------------------------- EditText --------------------------------------------- */

    public void inputSpecies(){
        pest_dialog_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString()==null || s.toString().equals("")){
                    Toast.makeText(getContext(), "수목명을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                treePestInfoDTO.setPest(s.toString());
            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (InputDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement InputDialogListener");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        managementPestDialogBinding = null;
        sharedPreferenceManager = null;
        listener = null;
        pest_dialog_severity_spinner = null;
        pest_dialog_spinner = null;
        pest_dialog_recovered_tv = null;
        pest_dialog_recovered = null;
        pest_dialog_name = null;
        treePestInfoDTO = null;
    }


}
