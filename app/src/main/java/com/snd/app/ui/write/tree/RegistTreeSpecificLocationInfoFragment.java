package com.snd.app.ui.write.tree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.snd.app.data.dataUtil.SpinnerValueListener;
import com.snd.app.data.dataUtil.TextManager;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.RegistTreeSpecificLocationFrBinding;
import com.snd.app.domain.tree.dto.TreeSpecificLocationInfoDTO;
import com.snd.app.ui.write.RegistTreeInfoActivity;


public class RegistTreeSpecificLocationInfoFragment extends TMFragment implements SpinnerValueListener {
    RegistTreeSpecificLocationFrBinding registTreeSpecificLocationFrBinding;
    RegistTreeSpecificLocationInfoViewModel specificLocationInfoVM;
    TreeSpecificLocationInfoDTO treeSpecificLocationInfoDTO;
    SharedPreferenceManager sharedPreferencesManager;
    AlertDialog.Builder alertDialogBuilder;
    String Authorization;
    String idHex;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registTreeSpecificLocationFrBinding = DataBindingUtil.inflate(inflater, R.layout.regist_tree_specific_location_fr, container, false);
        registTreeSpecificLocationFrBinding.setLifecycleOwner(this);
        specificLocationInfoVM = new ViewModelProvider(getActivity()).get(RegistTreeSpecificLocationInfoViewModel.class);
        registTreeSpecificLocationFrBinding.setSpecificLocationVM(specificLocationInfoVM);
        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        Authorization = sharedPreferencesManager.getString("Authorization");
        idHex = sharedPreferencesManager.getString("idHex");
        treeSpecificLocationInfoDTO = new TreeSpecificLocationInfoDTO();
        // 프로세스 종료
        specificLocationInfoVM.getBack().observe(getActivity(), o -> {
            finishProccess();
        });

        return registTreeSpecificLocationFrBinding.getRoot();
    }// ./onCreateView


    void mappingText(){
        TextManager textManager = new TextManager();
        if(getView() != null){
            EditText carriagewayEditText = getView().findViewById(R.id.specificLocation_carriageway);
            EditText distanceEditText = getView().findViewById(R.id.specificLocation_distance);
            // 예외 처리
            carriagewayEditText.addTextChangedListener(textManager.createTextWatcher((text) -> {
                try {
                    specificLocationInfoVM.treeSpecificLocationInfoDTO.setCarriageway(Integer.parseInt(text));
                } catch (NumberFormatException e) {
                    specificLocationInfoVM.treeSpecificLocationInfoDTO.setCarriageway(0);
                }
            }));
            distanceEditText.addTextChangedListener(textManager.createTextWatcher((text) -> {
                try {
                    specificLocationInfoVM.treeSpecificLocationInfoDTO.setDistance(Integer.parseInt(text));
                } catch (NumberFormatException e) {
                    specificLocationInfoVM.treeSpecificLocationInfoDTO.setDistance(0);
                }
            }));
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSpecificLocationDTO();
        mappingText();
        specificLocationInfoVM.checkSpecificLocationInfo().observe(getActivity(), s -> {
            if(s.equals("success")){
                getView().findViewById(R.id.specificLocation_write_cancel2).setEnabled(false);
                Toast.makeText(getContext(), "위치 상세 정보가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                ((RegistTreeInfoActivity)getActivity()).num = 2;
                ((RegistTreeInfoActivity)getActivity()).switchFragment();

            }else {
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        specificLocationInfoVM.insertProcess.observe(getActivity(), s -> {
            insertProcess();
        });
    }


    public void  setSpecificLocationDTO(){
        if(treeSpecificLocationInfoDTO != null){
            // 값이 채워진 상태에서 가져오기
            treeSpecificLocationInfoDTO.setNfc(idHex);
            specificLocationInfoVM.Authorization = Authorization;
            specificLocationInfoVM.setTextViewModel(treeSpecificLocationInfoDTO);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // 스피너 설정
        Spinner spinner = (Spinner) getView().findViewById(R.id.specificLocation_tr_state);
        ArrayAdapter adapter=ArrayAdapter.createFromResource(getContext(), R.array.treeStatus_pest,  android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = spinner.getSelectedItem().toString();
                onSpinnerValueChanged(selectedValue);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    void insertProcess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("위치 상세 정보를 등록하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                specificLocationInfoVM.loadSpecificLocationInfo();
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
    public void onSpinnerValueChanged(String value) {
        treeSpecificLocationInfoDTO.setSidewalk((value.equals("없음 X")) ? false : true);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        registTreeSpecificLocationFrBinding = null;
        treeSpecificLocationInfoDTO = null;
        sharedPreferencesManager = null;
        alertDialogBuilder = null;
    }


}
