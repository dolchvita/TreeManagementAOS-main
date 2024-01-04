package com.snd.app;

import androidx.lifecycle.MutableLiveData;
import com.snd.app.common.TMViewModel;


public class MainViewModel extends TMViewModel {
    private String TAG=this.getClass().getName();

    public static final int FRAGMENT_HOME = 1;
    public static final int FRAGMENT_MAP = 2;
    public static final int FRAGMENT_RECENT = 3;
    public MutableLiveData<String> logout = new MutableLiveData<>();
    public MutableLiveData<String> back = new MutableLiveData<>();


    public MutableLiveData<Integer> tabClick=new MutableLiveData<>();  // setter


    public void setTabClick(Integer value){
        tabClick.setValue(value);
    }

    public void logout(){
        logout.setValue("logout");
    }


    public void setBack(){
        back.setValue("back");
    }



}
