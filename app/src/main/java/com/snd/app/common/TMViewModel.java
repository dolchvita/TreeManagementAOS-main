package com.snd.app.common;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.snd.app.di.AppComponent;
import com.snd.app.di.AppModule;
import com.snd.app.di.DaggerAppComponent;


public class TMViewModel extends ViewModel {
   public String TAG = this.getClass().getName();
   private static AppComponent appComponent;


   public MutableLiveData<Boolean> isNfc = new MutableLiveData<>();
   public MutableLiveData<Boolean> isNetwork = new MutableLiveData<>();
   public MutableLiveData<Boolean> isGps = new MutableLiveData<>();

   public static AppComponent getAppComponent() {
      appComponent = DaggerAppComponent.builder()
              .appModule(new AppModule())
              .build();
      return appComponent;
   }


   public void log(String msg){
      Log.d(TAG, msg);
   }


}