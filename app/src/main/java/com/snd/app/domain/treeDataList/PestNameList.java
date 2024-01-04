package com.snd.app.domain.treeDataList;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snd.app.data.singleton.RetrofitClient;
import com.snd.app.domain.tree.vo.ResponseVO;
import com.snd.app.repository.tree.TreeService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PestNameList {

    public String TAG = this.getClass().getName();
    private String Authorization;
    private final MutableLiveData<List<String>> sppinerItems = new MutableLiveData<>();
    ArrayList<String> pestList=new ArrayList<>();


    public PestNameList( String Authorization) {
        this.Authorization=Authorization;
        loadItems();
    }


    private void loadItems() {
        List<String> spinnerItems = new ArrayList<>();
        // Load your data. This could be from a database, an API, or wherever your data comes from

        getPestNameList();
        pestList.add("없음 X");
        pestList.add("직접 입력");
        sppinerItems.setValue(pestList);
    }



    public LiveData<List<String>> getItems() {
        return sppinerItems;
    }



    public void getPestNameList(){
        TreeService service = RetrofitClient.getInstance(Authorization).create(TreeService.class);

        Call<ResponseVO> call = service.getPestNameList();

        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response);
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());

                    ResponseVO responseVO = response.body();
                    Object data=responseVO.getData();
                    Log.d(TAG,"** 데이터 추출 ** " + data);

                    String jsonString = data.toString();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<TreeDataList>>() {}.getType();

                    List<TreeDataList> treeDataListData = gson.fromJson(jsonString, listType);

                    for (TreeDataList treeDataList : treeDataListData) {
                        pestList.add(treeDataList.getPestName());
                    }

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
