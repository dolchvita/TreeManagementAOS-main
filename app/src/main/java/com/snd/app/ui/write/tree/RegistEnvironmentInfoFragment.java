package com.snd.app.ui.write.tree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.dataUtil.TextManager;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.domain.tree.dto.TreeEnvironmentInfoDTO;
import com.snd.app.databinding.RegistEnvironmentInfoFrBinding;
import com.snd.app.ui.write.RegistTreeInfoActivity;

import java.util.List;


public class RegistEnvironmentInfoFragment extends TMFragment {
    RegistEnvironmentInfoFrBinding registEnvironmentInfoFrBinding;
    RegistEnvironmentInfoViewModel registEnvironmentInfoVM;
    TreeEnvironmentInfoDTO treeEnvironmentInfoDTO;
    AlertDialog.Builder alertDialogBuilder;
    SharedPreferenceManager sharedPreferencesManager;
    private Spinner packingMaterial_spinner;
    private Spinner frameMaterial_spinner;
    private Spinner boundaryStone_spinner;
    AppCompatEditText t_frameMaterial;
    AppCompatEditText t_packingMaterial;
    TextManager textManager;
    String Authorization;
    String idHex;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registEnvironmentInfoFrBinding = DataBindingUtil.inflate(inflater, R.layout.regist_environment_info_fr, container, false);
        registEnvironmentInfoFrBinding.setLifecycleOwner(this);
        registEnvironmentInfoVM = new ViewModelProvider(getActivity()).get(RegistEnvironmentInfoViewModel.class);
        registEnvironmentInfoFrBinding.setEnvironmentVM(registEnvironmentInfoVM);
        treeEnvironmentInfoDTO = new TreeEnvironmentInfoDTO();
        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        Authorization = sharedPreferencesManager.getString("Authorization");
        idHex = sharedPreferencesManager.getString("idHex");
        textManager = new TextManager();

        return registEnvironmentInfoFrBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTreeStatusDTO();
        mappingText();

        t_frameMaterial = view.findViewById(R.id.environment_material);
        initFrameMaterialList();

        t_packingMaterial = view.findViewById(R.id.packing_material);
        initPackingMaterialList();

        // 스피너 설정 - 경계석 유무
        boundaryStone_spinner = view.findViewById(R.id.boundary_stone_tv_state);
        ArrayAdapter adapter=ArrayAdapter.createFromResource(getContext(), R.array.treeStatus_pest,  android.R.layout.simple_spinner_item);
        boundaryStone_spinner.setAdapter(adapter);

        registEnvironmentInfoVM.insertProcess.observe(getActivity(), s -> {
            if(s.equals("none")){
                Toast.makeText(getContext(), "입력 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }else {
                insertProcess();
            }
        });

        registEnvironmentInfoVM.checkTreeEnvironmentInfo().observe(getActivity(), s -> {
            if(s.equals("success")){
                getView().findViewById(R.id.environment_write_cancel).setEnabled(false);
                Toast.makeText(getContext(), "수목 정보가 모두 등록되었습니다.", Toast.LENGTH_SHORT).show();
                ((RegistTreeInfoActivity)getActivity()).num = 4;
                ((RegistTreeInfoActivity)getActivity()).switchFragment();
            }else {
                Toast.makeText(getContext(), "입력 사항을 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        registEnvironmentInfoVM.back.observe(getActivity(), o -> {
            finishProccess();
        });

    }// ./onViewCreated


    public void  setTreeStatusDTO(){
        registEnvironmentInfoVM.Authorization = Authorization;
        treeEnvironmentInfoDTO.setNfc(sharedPreferencesManager.getString("idHex"));
        treeEnvironmentInfoDTO.setSubmitter(sharedPreferencesManager.getString("id"));
        treeEnvironmentInfoDTO.setVendor(sharedPreferencesManager.getString("company"));
        registEnvironmentInfoVM.setTextViewModel(treeEnvironmentInfoDTO);
    }


    /* --------------------------------------------- Spinner --------------------------------------------- */

   private void initFrameMaterialList(){
        // 스피너 설정 - 보호틀
        frameMaterial_spinner = getView().findViewById(R.id.environment_material_state);
        registEnvironmentInfoVM.getFrameMaterialList().observe(getViewLifecycleOwner(), items -> {
           settingSpinnerItem(items);
           int noItemIndex = items.indexOf("없음");
           if(noItemIndex >= 0) {
               frameMaterial_spinner.setSelection(noItemIndex);
           }
        });

        registEnvironmentInfoVM.showEditText.observe(getActivity(), show -> {
            t_frameMaterial.setVisibility(show ? View.VISIBLE : View.GONE);
            inputSpecies(t_frameMaterial);
        });
    }


    private void settingSpinnerItem(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frameMaterial_spinner.setAdapter(adapter);
    }


    // 스피너 설정 - 포장재
    public void initPackingMaterialList(){
        packingMaterial_spinner = getView().findViewById(R.id.packing_material_list);
        registEnvironmentInfoVM.packingMaterialList().observe(getViewLifecycleOwner(), items -> {
            settingSpinnerItem2(items);
            int noItemIndex = items.indexOf("없음");
            if(noItemIndex >= 0) {
                packingMaterial_spinner.setSelection(noItemIndex);
            }
        });

        registEnvironmentInfoVM.packingEditText.observe(getActivity(), show -> {
            t_packingMaterial.setVisibility(show ? View.VISIBLE : View.GONE);
            inputSpecies(t_packingMaterial);
        });
    }


    private void settingSpinnerItem2(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        packingMaterial_spinner.setAdapter(adapter);
    }


    /* --------------------------------------------- EditText --------------------------------------------- */

    public void inputSpecies(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString() == null || s.toString().equals("")){
                    Toast.makeText(getContext(), "입력 사항을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(editText == t_packingMaterial){
                    treeEnvironmentInfoDTO.setPackingMaterial(s.toString());
                } else if (editText == t_frameMaterial) {
                    treeEnvironmentInfoDTO.setFrameMaterial(s.toString());
                }
            }
        });
    }


    private void mappingText(){
        if(getView() != null){
            EditText grd_fr_width = getView().findViewById(R.id.grd_fr_width);
            EditText grd_fr_height = getView().findViewById(R.id.grd_fr_height);
            EditText road_width = getView().findViewById(R.id.road_width);
            EditText sidewalk_width = getView().findViewById(R.id.sidewalk_width);
            EditText soil_ph = getView().findViewById(R.id.soil_ph);
            EditText soil_density = getView().findViewById(R.id.soil_density);

            textManager.addTextWatcherWithValidation(grd_fr_width, value -> registEnvironmentInfoVM.treeEnvironmentInfoDTO.setFrameHorizontal(value));
            textManager.addTextWatcherWithValidation(grd_fr_height, value -> registEnvironmentInfoVM.treeEnvironmentInfoDTO.setFrameVertical(value));
            textManager.addTextWatcherWithValidation(road_width, value -> registEnvironmentInfoVM.treeEnvironmentInfoDTO.setRoadWidth(value));
            textManager.addTextWatcherWithValidation(sidewalk_width, value -> registEnvironmentInfoVM.treeEnvironmentInfoDTO.setSidewalkWidth(value));
            textManager.addTextWatcherWithValidation(soil_ph, value -> registEnvironmentInfoVM.treeEnvironmentInfoDTO.setSoilPH(value));
            textManager.addTextWatcherWithValidation(soil_density, value -> registEnvironmentInfoVM.treeEnvironmentInfoDTO.setSoilDensity(value));
        }
    }


    void insertProcess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("수목 환경 정보를 등록하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registEnvironmentInfoVM.registerTreeEnvironmentInfo();
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
        registEnvironmentInfoFrBinding = null;
        sharedPreferencesManager = null;
        treeEnvironmentInfoDTO = null;
        alertDialogBuilder = null;
        packingMaterial_spinner = null;
        frameMaterial_spinner = null;
        boundaryStone_spinner = null;
        t_packingMaterial = null;
        t_frameMaterial = null;
        textManager = null;
    }


}
