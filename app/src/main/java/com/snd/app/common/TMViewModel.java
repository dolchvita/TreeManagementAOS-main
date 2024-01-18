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


   // 앱 컴포넌트 최초 생성 및 설정
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