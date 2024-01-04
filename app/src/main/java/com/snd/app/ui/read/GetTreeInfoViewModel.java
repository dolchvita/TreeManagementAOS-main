package com.snd.app.ui.read;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import com.snd.app.common.TMViewModel;

public class GetTreeInfoViewModel extends TMViewModel {
    private String TAG = this.getClass().getName();
    public ObservableField<String> readTitle = new ObservableField<>();
    public MutableLiveData<String> fragmentPageName = new MutableLiveData<>();
    public MutableLiveData<String> back = new MutableLiveData<>();

    public void setBack(){
        back.setValue("click");
    }

    // 1-1) 스피너 선택
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        Log.d(TAG, ""+item);
        fragmentPageName.setValue(item);
    }





}
