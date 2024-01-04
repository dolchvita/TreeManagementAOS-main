package com.snd.app.ui.management;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;

public class TreeManagementViewModel extends TMViewModel {
    public ObservableField<String> registTitle = new ObservableField<>();

    public MutableLiveData<String> back = new MutableLiveData<>();

    public void back(){
        back.setValue("event");
    }

}
