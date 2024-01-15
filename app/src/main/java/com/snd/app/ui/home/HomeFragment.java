package com.snd.app.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.MainActivity;
import com.snd.app.MainViewModel;
import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.domain.user.UserDTO;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.MainHomeFrBinding;
import com.snd.app.ui.login.LoginActivity;
import com.snd.app.ui.user.UserInfoActivity;
import com.snd.app.ui.write.RegistTreeInfoActivity;


public class HomeFragment extends TMFragment {
    SharedPreferenceManager sharedPreferenceManager;
    MainHomeFrBinding homeFrBinding;
    HomeViewModel homeVM;
    MainViewModel mainVM;
    boolean isNfcPermissionGranted;
    AlertDialog.Builder builder;
    AlertDialog dialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeFrBinding = DataBindingUtil.inflate(inflater, R.layout.main_home_fr, container, false);
        homeFrBinding.setLifecycleOwner(this);
        homeVM = new ViewModelProvider(this).get(HomeViewModel.class);
        homeFrBinding.setHomeVM(homeVM);    //홈뷰모델 연동
        mainVM = new ViewModelProvider(this).get(MainViewModel.class);
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        isNfcPermissionGranted = ((MainActivity)getActivity()).isNfcPermissionGranted;
        setUserInfo();

        AppCompatTextView name = homeFrBinding.name;
        String text = getString(R.string.user_name2, sharedPreferenceManager.getString("name"));
        name.setText(text);

        return homeFrBinding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 회원정보 조회 페이지
        homeVM.profile.observe(getActivity(), s -> {
            startActivity(new UserInfoActivity());
        });

        homeVM.tabClick.observe(getActivity(), integer -> {
            switchFragment(integer);
        });
    }





    public void switchFragment(int tabClick){
        switch (tabClick){
            case 1:
                Toast.makeText(getContext(), "NFC를 항상 켜주세요. (기본모드로 설정)", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getContext(), "인터넷 연결을 항상 유지해주세요. (기본모드로 설정)", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getContext(), "GPS를 항상 켜주세요. (기본모드로 설정)", Toast.LENGTH_SHORT).show();
                break;
        }
    }



    void setUserInfo(){
        UserDTO userDTO = new UserDTO();
        userDTO.setName(sharedPreferenceManager.getString("name"));
        userDTO.setCompany(sharedPreferenceManager.getString("company"));

        if(sharedPreferenceManager.getString("name") == null){
            showMessage();
        }
        homeVM.setUserInfo(userDTO);
    }


    void showMessage(){
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("로그인 오류");
        builder.setMessage("재로그인 해주세요.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = builder.create();
        dialog.show();

        // 확인을 누르건, 취소를 누르건 이 메시지가 뜬다면 로그인 화면으로 튕겨버리기
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();

    }


    public void startActivity(Activity activity){
        Intent intent = new Intent(getContext(), activity.getClass());
        startActivity(intent);
        getActivity().finish();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(dialog != null){
            dialog.dismiss();
        }
    }



}