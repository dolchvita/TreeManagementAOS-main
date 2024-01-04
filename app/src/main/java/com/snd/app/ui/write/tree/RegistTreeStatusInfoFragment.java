package com.snd.app.ui.write.tree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.dataUtil.DatePickerHelper;
import com.snd.app.data.dataUtil.TextManager;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.RegistTreeStatusInfoFrBinding;
import com.snd.app.domain.tree.dto.TreeStatusInfoDTO;
import com.snd.app.ui.write.RegistTreeInfoActivity;


public class RegistTreeStatusInfoFragment extends TMFragment{
    RegistTreeStatusInfoFrBinding registTreeStatusInfoFrBinding;
    RegistTreeStatusInfoViewModel treeStatusInfoVM;
    SharedPreferenceManager sharedPreferencesManager;
    private AlertDialog.Builder alertDialogBuilder;
    TreeStatusInfoDTO treeStatusInfoDTO;
    String Authorization;
    String idHex;

    private Spinner spinner_creation;
    private Spinner spinner_pest;
    TextManager textManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registTreeStatusInfoFrBinding = DataBindingUtil.inflate(inflater, R.layout.regist_tree_status_info_fr, container, false);
        registTreeStatusInfoFrBinding.setLifecycleOwner(this);
        treeStatusInfoVM = new ViewModelProvider(getActivity()).get(RegistTreeStatusInfoViewModel.class);
        registTreeStatusInfoFrBinding.setTreeStatusInfoVM(treeStatusInfoVM);
        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        Authorization = sharedPreferencesManager.getString("Authorization");
        idHex = sharedPreferencesManager.getString("idHex");
        treeStatusInfoDTO = new TreeStatusInfoDTO();
        textManager = new TextManager();

        return registTreeStatusInfoFrBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTreeStatusDTO();
        mappingText();

        spinner_pest = getView().findViewById(R.id.treeStatus_tr_state);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.treeStatus_pest,  android.R.layout.simple_spinner_item);
        spinner_pest.setAdapter(adapter);

        spinner_creation = registTreeStatusInfoFrBinding.treeStatusCreation;
        ArrayAdapter creationAdapter = ArrayAdapter.createFromResource(getContext(), R.array.treeStatus_creation,  android.R.layout.simple_spinner_item);
        spinner_creation.setAdapter(creationAdapter);

        TextView t_creation = registTreeStatusInfoFrBinding.treeStatusCreationText;


        treeStatusInfoVM.insertProcess.observe(getActivity(), s -> {
            if (treeStatusInfoVM.pestProccess.getValue()){
                insertProcess();
            }else {
                registerTreeStatusInfo();
            }
        });


        treeStatusInfoVM.setCreation.observe(getActivity(), s -> {
            if(s.equals("일자 선택")){
                DatePickerHelper.showDatePickerDialog(getContext(), selectedDate -> {
                    treeStatusInfoVM.treeStatusInfoDTO.setCreation(selectedDate);
                    t_creation.setVisibility(View.VISIBLE);
                    t_creation.setText(selectedDate);
                });
            }
        });


        // 상세 정보가 등록되었다면..
        treeStatusInfoVM.checkStatusInfo().observe(getActivity(), s -> {
            if(s.equals("success")){
                Toast.makeText(getContext(), "위치 상태 정보가 등록되었습니다.", Toast.LENGTH_LONG).show();
                ((RegistTreeInfoActivity)getActivity()).num = (treeStatusInfoVM.pestProccess.getValue() ? 5 : 3);
                ((RegistTreeInfoActivity)getActivity()).switchFragment();
            }else {
                Toast.makeText(getContext(), "오류가 발생했습니다. /n 처음부터 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            }
        });


        // 프로세스 종료
        treeStatusInfoVM.getBack().observe(getActivity(), o -> {
            finishProccess();
        });

    }// ./onViewCreated


    void registerTreeStatusInfo(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("병충해 정보 추가없이 바로 등록하시겠습니까?");
        alertDialogBuilder.setMessage("병충해 정보는 현재 페이지에서만 추가가 가능합니다.");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                treeStatusInfoVM.registerTreeStatusInfo();
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


    void insertProcess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("수목 상태 정보를 등록하시겠습니까?");
        alertDialogBuilder.setMessage("이어서 병해정보가 추가됩니다.");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                treeStatusInfoVM.registerTreeStatusInfo();
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


    public void  setTreeStatusDTO(){
        treeStatusInfoDTO.setNfc(sharedPreferencesManager.getString("idHex"));
        treeStatusInfoDTO.setSubmitter(sharedPreferencesManager.getString("id"));
        treeStatusInfoDTO.setVendor(sharedPreferencesManager.getString("company"));
        treeStatusInfoVM.Authorization = Authorization;
        treeStatusInfoVM.setTextViewModel(treeStatusInfoDTO);
    }


    void mappingText(){
        if(getView() != null){
            EditText treeStatus_scarlet_diam = getView().findViewById(R.id.treeStatus_scarlet_diam);    // 흉고직경
            EditText treeStatus_tr_height = getView().findViewById(R.id.treeStatus_tr_height);  //근원직경
            EditText treeStatus_crw_height = getView().findViewById(R.id.treeStatus_crw_height);    // 수고
            EditText treeStatus_crw_diam = getView().findViewById(R.id.treeStatus_crw_diam);    // 지하고
            EditText treeStatus_pest_dmg_state = getView().findViewById(R.id.treeStatus_pest_dmg_state);    // 수관폭

            textManager.addTextWatcherWithValidation(treeStatus_scarlet_diam, value -> treeStatusInfoVM.treeStatusInfoDTO.setDbh(value));
            textManager.addTextWatcherWithValidation(treeStatus_tr_height, value -> treeStatusInfoVM.treeStatusInfoDTO.setRcc(value));
            textManager.addTextWatcherWithValidation(treeStatus_crw_height, value -> treeStatusInfoVM.treeStatusInfoDTO.setHeight(value));
            textManager.addTextWatcherWithValidation(treeStatus_crw_diam, value -> treeStatusInfoVM.treeStatusInfoDTO.setLength(value));
            textManager.addTextWatcherWithValidation(treeStatus_pest_dmg_state, value -> treeStatusInfoVM.treeStatusInfoDTO.setWidth(value));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        registTreeStatusInfoFrBinding = null;
        sharedPreferencesManager = null;
        alertDialogBuilder = null;
        treeStatusInfoDTO = null;
        spinner_creation = null;
        spinner_pest = null;
        textManager = null;
    }


}
