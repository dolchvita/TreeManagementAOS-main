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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.dataUtil.BackPressListener;
import com.snd.app.data.dataUtil.TextManager;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.ManagementPruningBinding;
import com.snd.app.databinding.ManagementPruningBindingImpl;
import com.snd.app.domain.treeManagement.TreePruningDTO;
import com.snd.app.ui.management.TreeManagementActivity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;



// 가지치기
public class RegisterPruningFragment extends TMFragment implements BackPressListener {
    ManagementPruningBinding managementPruningBinding;
    RegisterPruningViewModel registPruningVM;
    private SharedPreferenceManager sharedPreferencesManager;
    private TreePruningDTO treePruningDTO;
    Spinner spinner_business;
    TextManager textManager;
    private AlertDialog.Builder alertDialogBuilder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        managementPruningBinding = DataBindingUtil.inflate(inflater, R.layout.management_pruning, container, false);
        managementPruningBinding.setLifecycleOwner(this);
        registPruningVM = new ViewModelProvider(getActivity()).get(RegisterPruningViewModel.class);
        managementPruningBinding.setVm(registPruningVM);

        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        registPruningVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        textManager = new TextManager();


        return managementPruningBinding.getRoot();
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTreePruningDTO();

        if(sharedPreferencesManager != null){
            registPruningVM.Authorization.observe(getActivity(), s -> {
                registPruningVM.loadCompanyBusinessNameList(s, sharedPreferencesManager.getString("company"));
            });
        }


        spinner_business = managementPruningBinding.pruningBusiness;
        registPruningVM.getCompanyBusinessNameList().observe(getViewLifecycleOwner(), this::updateSpinner);


        mappingText();


        registPruningVM.proccess.observe(getActivity(), s -> {
           insertProccess();
        });


        registPruningVM.checkPruningInfo().observe(getActivity(), s -> {
            String text = (s.equals("success")) ? "저장되었습니다." : "네트워크가 원활하지 않습니다. 다시 시도해주세요.";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            ((TreeManagementActivity)getActivity()).switchFragment(7);
        });


        registPruningVM.back.observe(getActivity(), o -> {
            finishProccess();
        });

        onBackPress();

    }


    void insertProccess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("등록하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registPruningVM.registerPruning();
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



    /* --------------------------------------------- EditText --------------------------------------------- */

    void setTreePruningDTO(){
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        treePruningDTO = new TreePruningDTO();
        treePruningDTO.setNfc(sharedPreferencesManager.getString("idHex"));
        treePruningDTO.setPruningCompany(sharedPreferencesManager.getString("company"));
        treePruningDTO.setPruningOperator(sharedPreferencesManager.getString("id"));
        treePruningDTO.setPruningDate(currentDateTime.toString());
        registPruningVM.setTextViewModel(treePruningDTO);
    }


    void mappingText(){
        if(getView()!=null){
            EditText text_pruning = getView().findViewById(R.id.pruning);
            EditText text_pruning_note = getView().findViewById(R.id.pruning_note);

            text_pruning.addTextChangedListener(textManager.createTextWatcher((text) -> registPruningVM.treePruningDTO.setPruning(text)));
            text_pruning_note.addTextChangedListener(textManager.createTextWatcher((text) -> registPruningVM.treePruningDTO.setPruningNote((text))));
        }
    }



    /* --------------------------------------------- Spinner --------------------------------------------- */

    private void updateSpinner(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_business.setAdapter(adapter);
    }



    void onBackPress(){

        /* Callback을 추가합니다.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "뒤로가기버튼");
                ((TreeManagementActivity)getActivity()).switchFragment(7);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

         */
    }



    @Override
    public boolean onBackPressed() {
        ((TreeManagementActivity)getActivity()).switchFragment(7);
        return true;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        managementPruningBinding = null;
        sharedPreferencesManager = null;
        textManager = null;
        alertDialogBuilder=null;
    }





}
