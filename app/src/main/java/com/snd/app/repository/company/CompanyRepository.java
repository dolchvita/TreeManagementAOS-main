package com.snd.app.repository.company;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.data.singleton.RetrofitClient;
import com.snd.app.data.singleton.RetrofitClient2;
import com.snd.app.domain.company.CompanyNameListVO;
import com.snd.app.domain.tree.vo.ResponseVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyRepository {
    public String TAG = this.getClass().getName();
    private final MutableLiveData<List<String>> companyNameList = new MutableLiveData<>();
    public MutableLiveData<List<String>> projectNameList = new MutableLiveData<>();
    public MutableLiveData<List<Object>> projectList = new MutableLiveData<>();



    /* ------------------------------------------------- READ METHODS------------------------------------------------- */

    public LiveData<List<String>> getCompanyNameList() {
        return companyNameList;
    }

    public void getCompanyNameListAll(){
        CompanyService companyService = RetrofitClient2.getInstance().create(CompanyService.class);
        Call<ResponseVO> call = companyService.getCompanyNameListAll();

        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());
                    ResponseVO responseVO = response.body();
                    ArrayList<CompanyNameListVO> dataList=(ArrayList<CompanyNameListVO>)responseVO.getData();
                    ArrayList<String> companyNameData=new ArrayList<>();

                    companyNameData.add("소속을 선택하여 주세요.");
                    try {
                        JSONArray jsonArray = new JSONArray(dataList);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String companyName = jsonObject.getString("companyName");
                            companyNameData.add(companyName);
                        }
                        companyNameList.setValue(companyNameData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG,"** dhfb? ** " + e.getMessage());
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




    // 특정 업체 진행중 사업명 리스트
    public void getCompanyBusinessNameList(String authorization, String companyName){
        Log.d(TAG, ""+companyName);
        CompanyService service = RetrofitClient.getInstance(authorization).create(CompanyService.class);
        Call<ResponseVO> call = service.getCompanyBusinessNameList(companyName);

        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());
                    ResponseVO responseVO = response.body();
                    //Object data=responseVO.getData();

                    List<String> dataList=(List<String>) responseVO.getData();
                    projectNameList.setValue(dataList);


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



    // 특정 업체 진행중 사업객체 리스트
    public void getCompanyBusinessList(String authorization, String companyName){
        Log.d(TAG, ""+companyName);
        CompanyService service = RetrofitClient.getInstance(authorization).create(CompanyService.class);
        Call<ResponseVO> call = service.getCompanyBusinessList(companyName);

        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());
                    ResponseVO responseVO = response.body();
                    //Object data=responseVO.getData();
                    ArrayList dtoList = new ArrayList();

                    List<Object> dataList=(List<Object>) responseVO.getData();

                    for(Object vo: dataList){
                        dtoList.add(vo);
                    }
                    projectList.setValue(dataList);

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
