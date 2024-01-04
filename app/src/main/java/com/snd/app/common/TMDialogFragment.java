package com.snd.app.common;

import android.util.Log;

import androidx.fragment.app.DialogFragment;

public class TMDialogFragment extends DialogFragment {
    public String TAG = this.getClass().getName();


    public void log(String msg){
        Log.d(TAG, msg);
    }


}
