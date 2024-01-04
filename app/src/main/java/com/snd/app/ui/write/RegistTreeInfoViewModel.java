package com.snd.app.ui.write;


import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;

// 메인 액티비티에 해당하는 뷰모델
public class RegistTreeInfoViewModel extends TMViewModel {

    private MutableLiveData _back = new MutableLiveData<>();

    public LiveData back = getBack();
    public  ObservableField<String> registTitle = new ObservableField<>();
    public MutableLiveData<String> _resultLiveData = new MutableLiveData<>();


    // 뒤로 가기
    public void setBack(){
        _back.setValue("click");
    }
    public LiveData getBack(){
        return _back;
    }




}
