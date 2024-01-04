package com.snd.app.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.MainActivity;
import com.snd.app.R;
import com.snd.app.common.TMActivity;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.UserInfoActBinding;


public class UserInfoActivity extends TMActivity {
    UserInfoActBinding getUserInfoActBinding;
    UserInfoViewModel getUserInfoVM;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserInfoActBinding = DataBindingUtil.setContentView(this, R.layout.user_info_act);
        getUserInfoActBinding.setLifecycleOwner(this);
        getUserInfoVM = new ViewModelProvider(this).get(UserInfoViewModel.class);
        getUserInfoActBinding.setGetUserInfoVM(getUserInfoVM);

        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(this);
        String id = sharedPreferenceManager.getString("id");

        getUserInfoVM.Authorization.setValue(sharedPreferenceManager.getString("token"));
        getUserInfoVM.id.setValue(id);

        TextView textView = getUserInfoActBinding.getUserInfo;
        String text = getString(R.string.user_name, id);
        textView.setText(text);


        getUserInfoVM.Authorization.observe(this, s -> {
            getUserInfoVM.loadUserDTO();
        });


        getUserInfoVM.getUserDTO().observe(this, userDTO -> {
            getUserInfoVM.setTextViewByUserInfo(userDTO);
        });
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getUserInfoActBinding = null;

    }


}
