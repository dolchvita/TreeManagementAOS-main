package com.snd.app.repository.tree;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class TreeViewModel extends ViewModel {
    private MutableLiveData _back=new MutableLiveData<>();
    public LiveData back=getBack();

    public MutableLiveData<Boolean> tagSettingLock = new MutableLiveData<>(false);



    public void setBack(){
        _back.setValue("click");
    }

    public LiveData getBack(){
        return _back;
    }



}
