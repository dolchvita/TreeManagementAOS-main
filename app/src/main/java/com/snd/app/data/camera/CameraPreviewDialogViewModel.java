package com.snd.app.data.camera;

import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;

public class CameraPreviewDialogViewModel extends TMViewModel {
    public MutableLiveData<String> onCameraBt = new MutableLiveData<>();

    public void takePhoto(){
        onCameraBt.setValue("click");
    }


}
