package com.snd.app.data.singleton;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static String TAG = "RetrofitClient";
    private static final String BASE_URL = "http://125.143.174.65:9955/";
    private static Retrofit retrofit;

    public static Retrofit getInstance(String authorization) {
        Log.d(TAG, "** 넘어오는 토큰 확인 ** " + authorization);

        if (retrofit == null) {
            // 로그 확인 용
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        okhttp3.Request originalRequest = chain.request();

                        // 헤더에 토큰 추가
                        okhttp3.Request authorizedRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + authorization)
                                .build();
                        return chain.proceed(authorizedRequest);
                    })
                    .addInterceptor(loggingInterceptor)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 추가
                    .build();
        }
        return retrofit;
    }


}
