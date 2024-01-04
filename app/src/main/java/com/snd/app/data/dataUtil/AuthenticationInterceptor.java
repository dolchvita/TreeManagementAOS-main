package com.snd.app.data.dataUtil;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    public String TAG = this.getClass().getName();

    private String authToken;

    public AuthenticationInterceptor(String token) {
        this.authToken = token;
        Log.d(TAG, "** ffff ** " + authToken);
        Log.d(TAG, "** fffsdsdsf ** " + token);
    }

    // Retrofit이 사용할 인증 토큰을 헤더에 미리 담는 객체
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder()
                .header("Authorization", "Bearer " + authToken);

        Log.d(TAG, "** ffff 토큰 확인 ** " + authToken);

        Request request = builder.build();
        return chain.proceed(request);
    }

}
