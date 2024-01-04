package com.snd.app.data.singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferenceManager {
    private static SharedPreferenceManager instance;
    private static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // Singleton
    private SharedPreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceManager(context);
        }
        return instance;
    }

    public Boolean saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
        return true;
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }


    public void saveLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }
    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0);
    }



    // 저장된 모든 데이터 지우기
    public void clear(){
        editor.clear();
        editor.apply();
    }



}
