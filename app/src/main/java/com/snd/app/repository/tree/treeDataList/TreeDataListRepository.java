package com.snd.app.repository.tree.treeDataList;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.data.singleton.RetrofitClient;
import com.snd.app.domain.tree.vo.ResponseVO;
import com.snd.app.domain.treeDataList.PackingMaterialDTO;
import com.snd.app.domain.treeDataList.TreePestNameDTO;
import com.snd.app.domain.treeDataList.TreeSpeciesDTO;
import com.snd.app.domain.treeDataList.frameMaterialDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 수목 정보 모두 가져오는 리스트
public class TreeDataListRepository {
    public String TAG = this.getClass().getName();
    private MutableLiveData<List<String>> treeSpeciesList = new MutableLiveData<>();
    ArrayList<String> speciesList = new ArrayList<>();
    private final MutableLiveData<List<String>> frameMaterialSppinerItems = new MutableLiveData<>();  // 반환할 결과
    ArrayList<String> frameMaterialList = new ArrayList<>();
    private final MutableLiveData<List<String>> packingMaterialSppinerItems = new MutableLiveData<>();  // 반환할 결과
    ArrayList<String> materialList = new ArrayList<>();
    private MutableLiveData<List<String>> treePestNameList = new MutableLiveData<>();  // 반환할 결과
    public MutableLiveData<String> failCheck = new MutableLiveData<>();


    /* -----------------------------------------------READ METHODS----------------------------------------------- */

    public LiveData<List<String>> getTreeSpeciesList(){
        return treeSpeciesList;
    }


    // 수목 기본정보 - 수종 리스트 가져오기
    public void getTreeSpeciesList(String Authorization){
        TreeDataListService treeDataListService = RetrofitClient.getInstance(Authorization).create(TreeDataListService.class);

        Call<ResponseVO> call = treeDataListService.getTreeSpeciesList();
        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response);
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());

                    ResponseVO responseVO = response.body();
                    List<TreeSpeciesDTO> dataList = (List<TreeSpeciesDTO>) responseVO.getData();

                    try {
                        JSONArray jsonArray = new JSONArray(dataList);
                        speciesList.add("직접 입력");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String frameMaterial = jsonObject.getString("species");
                            speciesList.add(frameMaterial);
                        }
                        treeSpeciesList.setValue(speciesList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG,"** dhfb? ** " + e.getMessage());

                        failCheck.setValue("fail");
                    }

                }else {

                    try {
                        Log.d(TAG,"** 오류 / 응답 ** " + response.body());
                        Log.d(TAG,"** 오류 / 응답 ** " + response.toString());
                        Log.d(TAG,"** 오류 / 응답 ** "+response.errorBody().string());
                        Log.d(TAG,"** 오류 / 응답 ** "+response);

                        failCheck.setValue("fail");


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



    public LiveData<List<String>> getFrameMaterialSppinerItems() {
        return frameMaterialSppinerItems;
    }

    // 수목 환경정보 - 보호틀 재질 리스트 가져오기
    public void getFrameMaterialList(String Authorization){
        TreeDataListService service = RetrofitClient.getInstance(Authorization).create(TreeDataListService.class);
        Call<ResponseVO> call = service.getFrameMaterialList();

        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response);
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());

                    ResponseVO responseVO = response.body();
                    List<frameMaterialDTO> data = (List<frameMaterialDTO>) responseVO.getData();

                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        frameMaterialList.add("없음");
                        frameMaterialList.add("직접 입력");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String frameMaterial = jsonObject.getString("frameMaterial");
                            Log.d(TAG,"** 데이터 추출33 ** " + frameMaterial);

                            frameMaterialList.add(frameMaterial);
                        }
                        frameMaterialSppinerItems.setValue(frameMaterialList);

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



    public LiveData<List<String>> getPackingMaterialSppinerItems() {
        return packingMaterialSppinerItems;
    }

    // 수목 환경정보 - 포장재 재질 리스트 가져오기
    public void getPackingMaterialList(String Authorization){
        TreeDataListService service = RetrofitClient.getInstance(Authorization).create(TreeDataListService.class);
        Call<ResponseVO> call = service.getPackingMaterialList();

        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response);
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());

                    ResponseVO responseVO = response.body();
                    List<PackingMaterialDTO> dataList= (List<PackingMaterialDTO>) responseVO.getData();
                    try {
                        JSONArray jsonArray = new JSONArray(dataList);
                        materialList.add("없음");
                        materialList.add("직접 입력");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String frameMaterial = jsonObject.getString("packingMaterial");
                            Log.d(TAG,"** 데이터 추출33 ** " + frameMaterial);
                            materialList.add(frameMaterial);
                        }
                        packingMaterialSppinerItems.setValue(materialList);

                    } catch (JSONException e) {
                        e.printStackTrace();
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



    // 수목 상태정보 - 병충해명 리스트 가져오기
    public LiveData<List<String>> getTreePestNameLists() {
        return treePestNameList;
    }
    public void getPestNameList(String Authorization){
        TreeDataListService service = RetrofitClient.getInstance(Authorization).create(TreeDataListService.class);
        Call<ResponseVO> call = service.getPestNameList();

        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response);
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());

                    ResponseVO responseVO = response.body();
                    List<TreePestNameDTO> dataList = (List<TreePestNameDTO>) responseVO.getData();
                    ArrayList<String> pestNameList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(dataList);
                        pestNameList.add("직접 입력");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String pestName = jsonObject.getString("pestName");
                            pestNameList.add(pestName);
                        }
                        treePestNameList.setValue(pestNameList);

                    } catch (JSONException e) {
                        e.printStackTrace();
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