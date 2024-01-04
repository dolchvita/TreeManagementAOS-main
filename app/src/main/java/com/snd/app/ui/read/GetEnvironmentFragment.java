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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.dataUtil.TextManager;
import com.snd.app.data.singleton.SingletonVO;
import com.snd.app.databinding.GetEnvironmentInfoFrBinding;
import com.snd.app.domain.tree.dto.TreeEnvironmentInfoDTO;

import java.util.List;


public class GetEnvironmentFragment extends TMFragment {
    GetEnvironmentInfoFrBinding getEnvironmentInfoFrBinding;
    GetEnvironmentViewModel getEnvironmentVM;
    SingletonVO singletonVO;
    private SharedPreferences sharedPreferences;
    public String idHex;
    String Authorization;
    private Spinner packingMaterial_spinner;
    AppCompatEditText t_packingMaterial;
    private Spinner frameMaterial_spinner;
    AppCompatEditText t_frameMaterial;
    private Spinner boundaryStone_spinner;
    // 스피너 관련 데이터 처리 객체
    private AlertDialog.Builder alertDialogBuilder;
    TreeEnvironmentInfoDTO treeEnvironmentInfoDTO;
    TextManager textManager;

    ConstraintLayout get_enviroment_text_form;
    ConstraintLayout get_enviroment_read_form;
    ConstraintLayout get_enviroment_eidt_form;
    AppCompatButton get_environment_write_cancel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getEnvironmentInfoFrBinding = DataBindingUtil.inflate(inflater, R.layout.get_environment_info_fr, container, false);
        getEnvironmentInfoFrBinding.setLifecycleOwner(this);
        getEnvironmentVM = new ViewModelProvider(this).get(GetEnvironmentViewModel.class);
        getEnvironmentInfoFrBinding.setGetEnvironmentVM(getEnvironmentVM);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Authorization = sharedPreferences.getString("Authorization", null);
        idHex = sharedPreferences.getString("idHex", null);
        singletonVO = SingletonVO.getInstance();
        treeEnvironmentInfoDTO = new TreeEnvironmentInfoDTO();
        textManager = new TextManager();

        return  getEnvironmentInfoFrBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        get_enviroment_text_form = getEnvironmentInfoFrBinding.getEnviromentTextForm;
        get_enviroment_read_form = getEnvironmentInfoFrBinding.getEnviromentReadForm;
        get_enviroment_eidt_form = getEnvironmentInfoFrBinding.getEnviromentEidtForm;
        get_environment_write_cancel = getEnvironmentInfoFrBinding.getEnvironmentWriteCancel;

        // 스피너 설정 - 보호틀
        t_frameMaterial = view.findViewById(R.id.get_environment_material);
        initFrameMaterialList();

        // 스피너 설정 - 포장재
        t_packingMaterial = view.findViewById(R.id.get_packing_material);
        initPackingMaterialList();

        // 스피너 설정 - 경계석 (유무)
        initBoundaryStone();

        // 입력값 받기
        inputText();


        /* treeIntegratedVO */
        singletonVO.treeIntegratedVO.observe(getViewLifecycleOwner(), treeIntegratedVO -> {
            getEnvironmentVM.Authorization = Authorization;

            // 저장된 객체를 디자인 요소에 매핑
            getEnvironmentVM.setTextViewModel(treeIntegratedVO);

            // 경계석 스피너
            if(treeIntegratedVO.getBoundaryStone() != null && treeIntegratedVO.getBoundaryStone()){
                boundaryStone_spinner.setSelection(0);    // 없음 표시
            }

        });/* ./treeIntegratedVO */


        // 수정모드로 전환하기
        getEnvironmentVM.readOrEdit.observe(getActivity(), aBoolean -> {
            if(aBoolean){
                editMode(get_enviroment_read_form, get_enviroment_eidt_form, get_environment_write_cancel, get_enviroment_text_form);
            }else {
                modifyProcess();
            }
        });


        getEnvironmentVM.cancel.observe(getActivity(), aBoolean -> {
            if(aBoolean){
                readMode(get_enviroment_read_form, get_enviroment_eidt_form, get_environment_write_cancel, get_enviroment_text_form);
                getEnvironmentVM.flag = false;
            }else {
                back();
            }
        });


        getEnvironmentVM.modifyProcess.observe(getActivity(), s -> {
            if(s.equals("none")){
                Toast.makeText(getContext(), "입력 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });


        // 1-2) 수정이 완료되었다면
        getEnvironmentVM.checkModifyEnvironmentInfo().observe(getActivity(), s -> {
            result(s);
            getActivity().finish();
        });


        // 등록되었다면
        getEnvironmentVM.checkTreeEnvironmentInfo().observe(getActivity(), s -> {
            result(s);
        });


    }// ./onViewCreated


    /* --------------------------------------------- Spinner --------------------------------------------- */

    public void initFrameMaterialList(){
        /* 스피너 초기 설정 */
        frameMaterial_spinner = getView().findViewById(R.id.get_environment_material_state);
        getEnvironmentVM.getFrameMaterialList().observe(getViewLifecycleOwner(), this::updateSpinner2);
        t_frameMaterial.addTextChangedListener(textManager.createTextWatcher((text) -> {
            if (!text.isEmpty()) {
                getEnvironmentVM.treeEnvironmentInfoDTO.setFrameMaterial(text);
            }
        }));
    }


    public void initPackingMaterialList(){  // 포장재
        packingMaterial_spinner = getView().findViewById(R.id.get_packing_material_list);
        getEnvironmentVM.packingMaterialList().observe(getViewLifecycleOwner(), this::updateSpinner);
        t_packingMaterial.addTextChangedListener(textManager.createTextWatcher((text) -> {
            if (!text.isEmpty()) {
                getEnvironmentVM.treeEnvironmentInfoDTO.setPackingMaterial(text);
            }
        }));
    }


    private void initBoundaryStone(){   // 경계석 유무
        boundaryStone_spinner = getEnvironmentInfoFrBinding.getBoundaryStoneTvState;
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.treeStatus_pest, android.R.layout.simple_spinner_item);
        boundaryStone_spinner.setAdapter(adapter);
    }


    private void updateSpinner2(List<String> items) {   // 보호틀
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frameMaterial_spinner.setAdapter(adapter);
    }


    private void updateSpinner(List<String> items) {    // 포장재
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        packingMaterial_spinner.setAdapter(adapter);
    }


    /* --------------------------------------------- EditText --------------------------------------------- */

    void inputText(){
        if(getView()!=null){
            EditText grd_fr_width = getView().findViewById(R.id.get_grd_fr_width);          // 보호틀 크기 (가로)
            EditText grd_fr_height = getView().findViewById(R.id.get_grd_fr_height);        // 보호틀 크기 (세로)
            EditText road_width = getView().findViewById(R.id.get_road_width);               // 도로폭
            EditText sidewalk_width = getView().findViewById(R.id.get_sidewalk_width);      // 인도폭
            EditText soil_ph = getView().findViewById(R.id.get_soil_ph);                     // 토양 산성도
            EditText soil_density = getView().findViewById(R.id.get_soil_density);           // 토양 견밀도

            grd_fr_width.addTextChangedListener(textManager.createTextWatcher((text) -> {
                if (!text.isEmpty()) {
                    getEnvironmentVM.treeEnvironmentInfoDTO.setFrameHorizontal(Double.parseDouble(text));
                }
            }));

            grd_fr_height.addTextChangedListener(textManager.createTextWatcher((text) -> {
                if (!text.isEmpty()) {
                    getEnvironmentVM.treeEnvironmentInfoDTO.setFrameVertical(Double.parseDouble(text));
                }
            }));

            road_width.addTextChangedListener(textManager.createTextWatcher((text) -> {
                if (!text.isEmpty()) {
                    getEnvironmentVM.treeEnvironmentInfoDTO.setRoadWidth(Double.parseDouble(text));
                }
            }));

            sidewalk_width.addTextChangedListener(textManager.createTextWatcher((text) -> {
                if (!text.isEmpty()) {
                    getEnvironmentVM.treeEnvironmentInfoDTO.setSidewalkWidth(Double.parseDouble(text));
                }
            }));

            soil_ph.addTextChangedListener(textManager.createTextWatcher((text) -> {
                if (!text.isEmpty()) {
                    getEnvironmentVM.treeEnvironmentInfoDTO.setSoilPH(Double.parseDouble(text));
                }
            }));

            soil_density.addTextChangedListener(textManager.createTextWatcher((text) -> {
                if (!text.isEmpty()) {
                    getEnvironmentVM.treeEnvironmentInfoDTO.setSoilDensity(Double.parseDouble(text));
                }
            }));
        }
    }


    /* --------------------------------------------- Process --------------------------------------------- */

    void modifyProcess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("수정하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getEnvironmentVM.modifyEnvironmentInfo();
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


    void result(String s){
        Toast.makeText(getContext(), s.equals("success")? "수정되었습니다." : "입력 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getEnvironmentInfoFrBinding = null;
        sharedPreferences = null;
        singletonVO = null;
        packingMaterial_spinner = null;
        frameMaterial_spinner = null;
        boundaryStone_spinner = null;
        t_packingMaterial = null;
        t_frameMaterial = null;
        alertDialogBuilder = null;
        treeEnvironmentInfoDTO = null;
        textManager = null;
        get_enviroment_text_form = null;
        get_enviroment_read_form = null;
        get_enviroment_eidt_form = null;
        get_environment_write_cancel = null;
    }


}
