package com.snd.app.ui.home;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.user.UserDTO;
import com.snd.app.ui.management.TreeManagementActivity;
import com.snd.app.ui.read.GetTreeInfoActivity;
import com.snd.app.ui.write.RegistTreeInfoActivity;


public class HomeViewModel extends TMViewModel {
    public static ViewModelProvider.Factory Factory;
    public ObservableField<String> name=new ObservableField<>();
    public ObservableField<String> company=new ObservableField<>();
    public MutableLiveData<String> profile=new MutableLiveData<>();
    public MutableLiveData<String> id=new MutableLiveData<>();
    public MutableLiveData<Integer> tabClick=new MutableLiveData<>();
    Intent intent;

    public static final int BT_NFC = 1;
    public static final int BT_NETWORK = 2;
    public static final int BT_GPS = 3;


    public void setUserInfo(UserDTO userDTO) {
        name.set(userDTO.getName());
        company.set(userDTO.getCompany());
    }



    public void setSettingButtons(Integer value){
        Log.d(TAG,"** 넘어오는 권한의 정체는? ** " + value);
        tabClick.setValue(value);
    }



    // 수목 등록
    public void onTextViewClicked(View view) {
        intent = new Intent(view.getContext(), RegistTreeInfoActivity.class);
        view.getContext().startActivity(intent);
    }


    // 수목 확인
    public void onCheckViewClicked(View view) {
        Log.d(TAG,"** 넘어오는 뷰의 정체는? ** "+view);
        intent = new Intent(view.getContext(), GetTreeInfoActivity.class);
        view.getContext().startActivity(intent);
    }


    // 수목 관리
    public void onManagementAct(View view){
        intent = new Intent(view.getContext(), TreeManagementActivity.class);
        view.getContext().startActivity(intent);
    }



    public void getUserInfoAct(){
        profile.setValue("evnt");
    }




}
