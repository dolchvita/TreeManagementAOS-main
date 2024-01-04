package com.snd.app.repository.tree.treeManagement;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.snd.app.data.singleton.RetrofitClient;
import com.snd.app.domain.tree.vo.ResponseVO;
import com.snd.app.domain.treeManagement.TreeDefoliationDTO;
import com.snd.app.domain.treeManagement.TreeFertilizationDTO;
import com.snd.app.domain.treeManagement.TreeHorticultureDTO;
import com.snd.app.domain.treeManagement.TreeManagementIntegratedVOForProject;
import com.snd.app.domain.treeManagement.TreeMiscellaneousDTO;
import com.snd.app.domain.treeManagement.TreePesticidesDTO;
import com.snd.app.domain.treeManagement.TreePruningDTO;
import com.snd.app.domain.treeManagement.TreeSurgeryDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TreeManagementRepository {
    public String TAG = this.getClass().getName();
    public MutableLiveData<String> insertTreePruning = new MutableLiveData<>();
    public MutableLiveData<String> insertTreeDefoliation = new MutableLiveData<>();
    public MutableLiveData<String> insertTreePesticides = new MutableLiveData<>();
    public MutableLiveData<String> insertTreeSurgery = new MutableLiveData<>();
    public MutableLiveData<String> insertTreeHorticulture = new MutableLiveData<>();
    public MutableLiveData<String> insertTreeFertilization = new MutableLiveData<>();
    public MutableLiveData<String> insertTreeMiscellaneous = new MutableLiveData<>();

    public MutableLiveData<List<Object>> managementList = new MutableLiveData<>();



    /* -----------------------------------------------CREATE METHODS----------------------------------------------- */

    // 수목 가지치기 정보 등록
    public void registerPruning(String Authorization, TreePruningDTO pruning) {
        TreeManagementService service = RetrofitClient.getInstance(Authorization).create(TreeManagementService.class);
        service.registerPruning(pruning)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> responseVOResponse) {
                        if (responseVOResponse.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.toString());

                            ResponseVO response=responseVOResponse.body();
                            insertTreePruning.setValue(response.getResult());

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);
                                insertTreePruning.setValue("fail");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }



    // 수목 맹아제거 정보 등록
    public void registerDefoliation(String Authorization, TreeDefoliationDTO defoliation) {
        TreeManagementService service = RetrofitClient.getInstance(Authorization).create(TreeManagementService.class);
        service.registerDefoliation(defoliation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> responseVOResponse) {
                        if (responseVOResponse.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.toString());

                            ResponseVO response=responseVOResponse.body();
                            insertTreeDefoliation.setValue(response.getResult());

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);
                                insertTreeDefoliation.setValue("fail");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }



    // 수목 병충해방제 정보 등록
    public void registerPesticides(String Authorization, TreePesticidesDTO pesticides) {
        TreeManagementService service = RetrofitClient.getInstance(Authorization).create(TreeManagementService.class);
        service.registerPesticides(pesticides)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> responseVOResponse) {
                        if (responseVOResponse.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.toString());

                            ResponseVO response=responseVOResponse.body();
                            insertTreePesticides.setValue(response.getResult());
                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);
                                insertTreePesticides.setValue("fail");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }



    // 수목 외과수술 정보 등록
    public void registerSurgery(String Authorization, TreeSurgeryDTO surgery) {
        TreeManagementService service = RetrofitClient.getInstance(Authorization).create(TreeManagementService.class);
        service.registerSurgery(surgery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> responseVOResponse) {
                        if (responseVOResponse.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.toString());

                            ResponseVO response=responseVOResponse.body();
                            insertTreeSurgery.setValue(response.getResult());
                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);
                                insertTreeSurgery.setValue("fail");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }



    // 수목 생육환경개선 정보 등록
    public void registerHorticulture(String Authorization, TreeHorticultureDTO horticulture) {
        TreeManagementService service = RetrofitClient.getInstance(Authorization).create(TreeManagementService.class);
        service.registerHorticulture(horticulture)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> responseVOResponse) {
                        if (responseVOResponse.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.toString());

                            ResponseVO response=responseVOResponse.body();
                            insertTreeHorticulture.setValue(response.getResult());
                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);
                                insertTreeHorticulture.setValue("fail");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }



    // 수목 비료주기 정보 등록
    public void registerFertilization(String Authorization, TreeFertilizationDTO fertilizatio) {
        TreeManagementService service = RetrofitClient.getInstance(Authorization).create(TreeManagementService.class);
        service.registerFertilization(fertilizatio)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> responseVOResponse) {
                        if (responseVOResponse.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.toString());

                            ResponseVO response=responseVOResponse.body();
                            insertTreeFertilization.setValue(response.getResult());
                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);
                                insertTreeFertilization.setValue("fail");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }



    // 수목 기타관리정보 정보 등록
    public void registerMiscellaneous(String Authorization, TreeMiscellaneousDTO miscellaneous) {
        TreeManagementService service = RetrofitClient.getInstance(Authorization).create(TreeManagementService.class);
        service.registerMiscellaneous(miscellaneous)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> responseVOResponse) {
                        if (responseVOResponse.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + responseVOResponse.toString());

                            ResponseVO response=responseVOResponse.body();
                            insertTreeMiscellaneous.setValue(response.getResult());
                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);
                                insertTreeMiscellaneous.setValue("fail");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }



    /* -----------------------------------------------READ METHODS----------------------------------------------- */

    public void getManagementHistoryListAll(String authorization, String tagId){
        Log.d(TAG, tagId);
        TreeManagementService service = RetrofitClient.getInstance(authorization).create(TreeManagementService.class);
        Call<ResponseVO> call = service.getManagementHistoryListAll(tagId);
        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());
                    ResponseVO responseVO = response.body();

                    List<TreeManagementIntegratedVOForProject> dataList= (List<TreeManagementIntegratedVOForProject>) responseVO.getData();
                    ArrayList objectList=new ArrayList();
                    for(Object vo: dataList){
                        objectList.add(vo);
                    }
                    managementList.setValue(objectList);

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
