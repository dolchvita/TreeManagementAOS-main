package com.snd.app.ui.read;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.dataUtil.DatePickerHelper;
import com.snd.app.data.dataUtil.TextManager;
import com.snd.app.data.singleton.SingletonVO;
import com.snd.app.databinding.GetTreeStatusInfoFrBinding;

public class GetTreeStatusFragment extends TMFragment {
    GetTreeStatusInfoFrBinding getTreeStatusInfoFrBinding;
    GetTreeStatusViewModel getTreeStatusVM;
    private AlertDialog.Builder alertDialogBuilder;
    private SharedPreferences sharedPreferences;
    private Spinner spinner_creation;
    TextManager textManager;
    SingletonVO singletonVO;
    Spinner spinner;

    public String idHex;
    String Authorization;

    ConstraintLayout get_status_read_form;
    ConstraintLayout get_status_eidt_form;
    ConstraintLayout get_status_text_form;
    AppCompatButton get_status_write_cancel;
    TextView t_creation;    // 나무 조성일자


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getTreeStatusInfoFrBinding = DataBindingUtil.inflate(inflater, R.layout.get_tree_status_info_fr, container, false);
        getTreeStatusInfoFrBinding.setLifecycleOwner(this);
        getTreeStatusVM = new ViewModelProvider(this).get(GetTreeStatusViewModel.class);
        getTreeStatusInfoFrBinding.setTreeStatusInfoVM(getTreeStatusVM);
        // 참조할 필수 데이터 (인증 토큰, 태그 아이디)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Authorization = sharedPreferences.getString("Authorization", null);
        idHex = sharedPreferences.getString("idHex", null);
        singletonVO = SingletonVO.getInstance();
        textManager = new TextManager();

        return getTreeStatusInfoFrBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        get_status_read_form = getTreeStatusInfoFrBinding.getStatusReadForm;
        get_status_eidt_form = getTreeStatusInfoFrBinding.getStatusEditForm;
        get_status_text_form = getTreeStatusInfoFrBinding.getStatusTextForm;
        get_status_write_cancel = getTreeStatusInfoFrBinding.getTreeStatusWriteCancel;

        initPestSpinner();
        inputText();
        initCreationSpinner();


        // 저장된 객체 가져오기
        singletonVO.treeIntegratedVO.observe(getViewLifecycleOwner(), treeIntegratedVO -> {
            getTreeStatusVM.Authorization = Authorization;
            getTreeStatusVM.setTextViewModel(treeIntegratedVO);

            if(treeIntegratedVO.getStatusSubmitter() == null){
                getTreeStatusVM.insertProcess.setValue(true);
            }
        });


        // 수정모드 전환하기
        getTreeStatusVM.readOrEdit.observe(getActivity(), aBoolean -> {
            if(aBoolean){
                editMode(get_status_read_form, get_status_eidt_form, get_status_write_cancel, get_status_text_form);
            }else {
                modifyProcess();
            }
        });


        getTreeStatusVM.cancel.observe(getActivity(), aBoolean -> {
            if(aBoolean){
                readMode(get_status_read_form, get_status_eidt_form, get_status_write_cancel, get_status_text_form);
                getTreeStatusVM.flag = false;
            }else {
                back();
            }
        });


        getTreeStatusVM.checkStatusInfo().observe(getActivity(), s -> {
            String text = (s.equals("StatusInfo Successfully Updated") || s.equals("success")) ? "수정되었습니다" : "입력 정보가 올바르지 않습니다.";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

            getActivity().finish();
        });

    }/* ./onViewCreated */


    private void initPestSpinner(){
        spinner = getTreeStatusInfoFrBinding.getTreeStatusTrState;
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.treeStatus_pest,  android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
    }


    private void initCreationSpinner(){
        spinner_creation = getTreeStatusInfoFrBinding.getTreeStatusCreation;
        t_creation = getTreeStatusInfoFrBinding.getTreeStatusCreationText;
        ArrayAdapter creationAdapter = ArrayAdapter.createFromResource(getContext(), R.array.treeStatus_creation,  android.R.layout.simple_spinner_item);
        spinner_creation.setAdapter(creationAdapter);
        getTreeStatusVM.setCreation.observe(getActivity(), s -> {
            if(s.equals("일자 선택")){
                DatePickerHelper.showDatePickerDialog(getContext(), selectedDate -> {
                    getTreeStatusVM.treeStatusInfoDTO.setCreation(selectedDate);
                    t_creation.setText(selectedDate);
                });
            }
        });
    }


    void inputText(){
        if(getView() != null){
            EditText t_dbh = getView().findViewById(R.id.get_treeStatus_scarlet_diam);    // 흉고직경
            EditText t_rcc = getView().findViewById(R.id.get_treeStatus_tr_height);      // 근원직경
            EditText t_height = getView().findViewById(R.id.get_treeStatus_crw_height);        // 수고
            EditText t_length = getView().findViewById(R.id.get_treeStatus_crw_diam);      // 지하고
            EditText t_width = getView().findViewById(R.id.get_treeStatus_pest_dmg_state);      // 수관폭

            textManager.addTextWatcherWithValidation(t_dbh, value -> getTreeStatusVM.treeStatusInfoDTO.setDbh(value));
            textManager.addTextWatcherWithValidation(t_rcc, value -> getTreeStatusVM.treeStatusInfoDTO.setRcc(value));
            textManager.addTextWatcherWithValidation(t_height, value -> getTreeStatusVM.treeStatusInfoDTO.setHeight(value));
            textManager.addTextWatcherWithValidation(t_length, value -> getTreeStatusVM.treeStatusInfoDTO.setLength(value));
            textManager.addTextWatcherWithValidation(t_width, value -> getTreeStatusVM.treeStatusInfoDTO.setWidth(value));
        }
    }


    void modifyProcess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("수정하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTreeStatusVM.modifyTreeStatusInfo();
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
        getTreeStatusInfoFrBinding = null;
        alertDialogBuilder = null;
        sharedPreferences = null;
        spinner_creation = null;
        textManager = null;
        singletonVO = null;
        spinner = null;
        get_status_read_form = null;
        get_status_eidt_form = null;
        get_status_text_form = null;
        get_status_write_cancel = null;
        t_creation = null;
    }


}
