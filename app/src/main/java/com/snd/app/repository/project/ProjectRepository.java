package com.snd.app.repository.project;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.snd.app.data.singleton.RetrofitClient;
import com.snd.app.domain.tree.vo.ResponseVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectRepository {
    public String TAG = this.getClass().getName();
    public final MutableLiveData<List<Object>> managementList = new MutableLiveData<>();  // 반환할 결과


    // 사업 이력조회
    public void getProjectManagementHistoryList(String authorization, String projectName){
        ProjectService service = RetrofitClient.getInstance(authorization).create(ProjectService.class);
        Call<ResponseVO> call = service.getProjectManagementHistoryList(projectName);
        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());
                    ResponseVO responseVO = response.body();


                    ArrayList dtoList = new ArrayList();
                    List<Object> dataList=(List<Object>) responseVO.getData();

                    for(Object vo: dataList){
                        dtoList.add(vo);
                    }

                    managementList.setValue(dataList);


                }else {
                    try {
                        Log.d(TAG,"** 오류 / 응답 ** " + response.body());
                        Log.d(TAG,"** 오류 / 응답 ** " + response.toString());
                        Log.d(TAG,"** 오류 / 응답 ** "+response.errorBody().string());
                        Log.d(TAG,"** 오류 / 응답 ** "+response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseVO> call, Throwable t) {
                Log.d(TAG,"** 통신 실패 ** "+t.getMessage());
            }
        });
    }




}
