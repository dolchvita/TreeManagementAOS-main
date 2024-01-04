package com.snd.app.repository.config;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.snd.app.data.singleton.RetrofitClient2;
import com.snd.app.domain.config.AosConfigDTO;
import com.snd.app.domain.tree.vo.ResponseVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AosConfigRepository {
    public String TAG = this.getClass().getName();
    MutableLiveData<String> _currentVersion = new MutableLiveData<>();
    LiveData<String> currentVersion = _currentVersion;
    MutableLiveData<String> _currentDownloadLink = new MutableLiveData<>();
    LiveData<String> currentDownloadLink = _currentDownloadLink;


    /* ------------------------------------------------- READ ------------------------------------------------- */

    // 현재 APK 정보 가져오기
    public void getCurrentApkInfo() {
        AosConfigService service = RetrofitClient2.getInstance().create(AosConfigService.class);
        service.getCurrentApkInfo()
                .enqueue(new Callback<ResponseVO>() {
                    @Override
                    public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                        if (response.isSuccessful()) {

                            Gson gson = new Gson();
                            ResponseVO data = response.body();
                            String jsonData = gson.toJson(data.getData());
                            AosConfigDTO dto = gson.fromJson(jsonData, AosConfigDTO.class);

                            _currentVersion.setValue(dto.getCurrentVersion());

                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseVO> call, Throwable t) {

                    }
                });
    }



    // 현재 APK 다운로드 URL 가져오기
    public void getCurrentDownloadLink() {
        AosConfigService service = RetrofitClient2.getInstance().create(AosConfigService.class);
        service.getCurrentDownloadLink()
                .enqueue(new Callback<ResponseVO>() {
                    @Override
                    public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                        if (response.isSuccessful()) {

                            ResponseVO responseVO = response.body();
                            Object data = responseVO.getData();
                            _currentDownloadLink.setValue(data.toString());

                        } else {
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseVO> call, Throwable t) {

                    }
                });
    }


}
