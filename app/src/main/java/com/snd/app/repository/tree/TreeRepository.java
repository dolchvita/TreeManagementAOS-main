package com.snd.app.repository.tree;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.snd.app.domain.tree.dto.TreeBasicInfoDTO;
import com.snd.app.domain.tree.dto.TreeEnvironmentInfoDTO;
import com.snd.app.domain.tree.dto.TreeInitializingDTO;
import com.snd.app.domain.tree.dto.TreeListRangeDTO;
import com.snd.app.domain.tree.dto.TreeLocationInfoDTO;
import com.snd.app.domain.tree.dto.TreePestInfoDTO;
import com.snd.app.domain.tree.dto.TreeSpecificLocationInfoDTO;
import com.snd.app.domain.tree.dto.TreeStatusInfoDTO;
import com.snd.app.data.singleton.RetrofitClient;
import com.snd.app.domain.tree.vo.ResponseVO;
import com.snd.app.domain.tree.vo.TreeIntegratedVO;

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


public class TreeRepository {
    public String TAG = this.getClass().getName();
    // 등록 콜백
    public MutableLiveData<String> checkTreeBasicInfo = new MutableLiveData<>();
    public MutableLiveData<String> checkSpecificLocationInfo = new MutableLiveData<>();
    public MutableLiveData<String> checkStatusInfo = new MutableLiveData<>();
    public MutableLiveData<String> checkTreeEnvironmentInfo = new MutableLiveData<>();
    public MutableLiveData<String> checkTreePestInfo = new MutableLiveData<>();
    public MutableLiveData<String> checkTreePestInfo2 = new MutableLiveData<>();
    // 조회 콜백
    public MutableLiveData<TreeIntegratedVO> treeIntegratedVO = new MutableLiveData<>();
    public MutableLiveData<List<Object>> treeIntegratedList = new MutableLiveData<>();
    public MutableLiveData<List<Object>> pestInfoList = new MutableLiveData<>();
    public MutableLiveData<String> failCheck = new MutableLiveData<>();
    // 수정 콜백
    public MutableLiveData<String> checkModifyBasicInfo = new MutableLiveData<>();
    public MutableLiveData<String> checkModifyLocationInfo = new MutableLiveData<>();
    public MutableLiveData<String> checkModifyEnvironmentInfo = new MutableLiveData<>();
    public MutableLiveData<String> checkModifyTreePestInfo = new MutableLiveData<>();




    /* ----------------------------------------------- CREATE METHODS ----------------------------------------------- */

    // 수목 기본 정보 등록
    public void initializeTreeBasicInfoForRegister(String Authorization, TreeInitializingDTO initializingTreeInfo) {
        TreeService service = RetrofitClient.getInstance(Authorization).create(TreeService.class);
        service.initializeTreeBasicInfoForRegister(initializingTreeInfo)
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
                            checkTreeBasicInfo.setValue("success");

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);

                                checkTreeBasicInfo.setValue("fail");

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



    public LiveData<String> checkSpecificLocationInfo() {
        return checkSpecificLocationInfo;
    }
    // 수목 위치(기본)정보 등록
    public void registerSpecificLocationInfo(String Authorization, TreeSpecificLocationInfoDTO treeSpecificLocationInfo){
        Log.d(TAG, "** registerSpecificLocationInfo ** " + treeSpecificLocationInfo);

        TreeService service = RetrofitClient.getInstance(Authorization).create(TreeService.class);
        service.registerSpecificLocationInfo(treeSpecificLocationInfo)
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
                            checkSpecificLocationInfo.setValue("success");

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);

                                checkSpecificLocationInfo.setValue("fail");

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



    // 수목 상태 정보 등록
    public void registerTreeStatusInfo(String Authorization, TreeStatusInfoDTO treeStatusInfoDTO){
        TreeService service = RetrofitClient.getInstance(Authorization).create(TreeService.class);
        service.registerTreeStatusInfo(treeStatusInfoDTO)
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

                            ResponseVO responseVO = responseVOResponse.body();
                            responseVO.getResult();
                            checkStatusInfo.setValue(responseVO.getResult());

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);

                                checkStatusInfo.setValue("fail");

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



    public LiveData<String> checkTreeEnvironmentInfo() {
        return checkTreeEnvironmentInfo;
    }

    // 수목 환경 정보 등록
    public void registerTreeEnvironmentInfo(String Authorization, TreeEnvironmentInfoDTO treeEnvironmentInfo){
        TreeService service = RetrofitClient.getInstance(Authorization).create(TreeService.class);
        service.registerTreeEnvironmentInfo(treeEnvironmentInfo)
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
                            checkTreeEnvironmentInfo.setValue("success");
                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);

                                checkTreeEnvironmentInfo.setValue("fail");

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


    public LiveData<String> checkTreePestInfo() {
        return checkTreePestInfo;
    }

    public LiveData<String> checkTreePestManagement(){
        return checkTreePestInfo2;
    }


    // 수목 병해 정보 등록
    public void registerTreePestInfo(String Authorization, List<TreePestInfoDTO> pestinfoList){
        for(TreePestInfoDTO dto:pestinfoList){
            Log.d(TAG, ""+dto);
        }

        TreeService service = RetrofitClient.getInstance(Authorization).create(TreeService.class);
        service.registerTreePestInfo(pestinfoList)
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

                            ResponseVO response = responseVOResponse.body();
                            response.getResult();
                            checkTreePestInfo.setValue(response.getResult());

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);

                                if(checkTreePestInfo != null){
                                    checkTreePestInfo.setValue("fail");
                                }

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

    // 수목 병해 정보 등록
    public void registerTreePestInfo2(String Authorization, List<TreePestInfoDTO> pestinfoList){
        for(TreePestInfoDTO dto:pestinfoList){
            Log.d(TAG, ""+dto);
        }
        TreeService service = RetrofitClient.getInstance(Authorization).create(TreeService.class);
        service.registerTreePestInfo(pestinfoList)
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
                            response.getResult();
                            checkTreePestInfo2.setValue(response.getResult());
                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);
                                if(checkTreePestInfo2 != null){
                                    checkTreePestInfo2.setValue("fail");
                                }
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



    /* ----------------------------------------------- READ METHODS ----------------------------------------------- */

    public LiveData<TreeIntegratedVO> getTreeInfo() {
        return treeIntegratedVO;
    }

    // 수목 통합 정보 가져오기 by NFC tagId
    public void getTreeInfoByNFCtagId(String authorization, String tagId){
        TreeService service = RetrofitClient.getInstance(authorization).create(TreeService.class);
        Call<ResponseVO> call = service.getTreeInfoByNFCtagId(tagId);

        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());
                    ResponseVO responseVO = response.body();
                    Object data = responseVO.getData();

                    Gson gson = new Gson();
                    String jsonData = gson.toJson(data);        // Object -> Json -> DTO
                    TreeIntegratedVO treeIntegratedVO1 = new TreeIntegratedVO();
                    treeIntegratedVO1 = gson.fromJson(jsonData, TreeIntegratedVO.class);

                    treeIntegratedVO.setValue(treeIntegratedVO1);
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



    // 수목 병해 정보 가져오기 by NFC tagId
    public void getTreePetsInfoByNFCtagId(String authorization, String tagId){
        TreeService service = RetrofitClient.getInstance(authorization).create(TreeService.class);
        Call<ResponseVO> call = service.getTreePetsInfoByNFCtagId(tagId);
        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());
                    ResponseVO responseVO = response.body();

                    ArrayList voList = new ArrayList();
                    List<Object> dataList = (List<Object>) responseVO.getData();

                    for(Object vo : dataList){
                        voList.add(vo);
                    }

                    pestInfoList.setValue(dataList);

                }else {
                    try {
                        Log.d(TAG,"** 오류 / 응답 ** " + response.body());
                        Log.d(TAG,"** 오류 / 응답 ** " + response.toString());
                        Log.d(TAG,"** 오류 / 응답 ** " + response.errorBody().string());
                        Log.d(TAG,"** 오류 / 응답 ** " + response);

                        failCheck.setValue("아예 생성 안됨..");

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


    // 범위내의 수목 통합 정보 리스트 가져오기
    public void getTreeInfoListByRange(String authorization, TreeListRangeDTO rangeInfo){
        TreeService service = RetrofitClient.getInstance(authorization).create(TreeService.class);
        Call<ResponseVO> call = service.getTreeInfoListByRange(rangeInfo.getLatitude(), rangeInfo.getLongitude(),
                rangeInfo.getRange(), rangeInfo.getTreeCount());

        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());
                    ResponseVO responseVO = response.body();

                    List<TreeIntegratedVO> dataList = (List<TreeIntegratedVO>) responseVO.getData();
                    ArrayList objectList = new ArrayList();

                    for(Object vo: dataList){
                        objectList.add(vo);
                    }
                    treeIntegratedList.setValue(objectList);

                }else {
                    try {
                        Log.d(TAG,"** 오류 / 응답 ** " + response.body());
                        Log.d(TAG,"** 오류 / 응답 ** " + response.toString());
                        Log.d(TAG,"** 오류 / 응답 ** "+response.errorBody().string());
                        Log.d(TAG,"** 오류 / 응답 ** "+response);

                        // 데이터가 없는 경우 이곳 진입
                        failCheck.setValue("The List is Empty");

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseVO> call, Throwable t) {
                Log.d(TAG,"** 통신 실패 ** " + t.getMessage());
            }
        });
    }


    /* ----------------------------------------------- UPDATE METHODS ----------------------------------------------- */

    public LiveData<String> checkModifyProcess() {
        return checkModifyBasicInfo;
    }

    // 수목 기본정보 수정
    public void  modifyBasicInfo(String authorization, TreeBasicInfoDTO treeBasicInfo){
        TreeService service = RetrofitClient.getInstance(authorization).create(TreeService.class);
        // Disposable 객체를 할당하고 구독을 시작합니다.
        service.modifyTreeBasicInfo(treeBasicInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + response.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + response.toString());

                            ResponseVO responseVO = response.body();
                            String result = responseVO.getResult();

                            // 여기서 사진 수정 메서드 호출할 것
                            checkModifyBasicInfo.setValue(result);

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + response.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + response);

                                checkModifyBasicInfo.setValue("fail");

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


    public LiveData<String> checkModifyLocationInfo() {
        return checkModifyLocationInfo;
    }

    // 수목 위치정보 수정
    public void  modifyLocationInfo(String authorization, TreeLocationInfoDTO treeLocationInfo){
        TreeService service = RetrofitClient.getInstance(authorization).create(TreeService.class);
        service.modifyTreeLocationInfo(treeLocationInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + response.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + response.toString());

                            ResponseVO responseVO = response.body();

                            // 여기서 사진 수정 메서드 호출할 것
                            checkModifyLocationInfo.setValue("success");

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + response.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + response);

                                checkModifyLocationInfo.setValue("fail");

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


    // 수목 상태정보 수정
    public void  modifyTreeStatusInfo(String authorization, TreeStatusInfoDTO treeStatusInfo){
        Log.d(TAG, "modifyTreeEnvironmentInfo 호출 : "+treeStatusInfo);
        TreeService service = RetrofitClient.getInstance(authorization).create(TreeService.class);
        service.modifyTreeStatusInfo(treeStatusInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + response.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + response.toString());

                            ResponseVO responseVO=response.body();
                            responseVO.getResult();
                            checkStatusInfo.setValue(responseVO.getResult());

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + response.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + response);

                                checkModifyEnvironmentInfo.setValue("fail");

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


    public LiveData<String> checkModifyEnvironmentInfo() {
        return checkModifyEnvironmentInfo;
    }

    // 수목 환경정보 수정
    public void  modifyTreeEnvironmentInfo(String authorization, TreeEnvironmentInfoDTO treeEnvironmentInfo){
        Log.d(TAG, "modifyTreeEnvironmentInfo 호출 : "+treeEnvironmentInfo);
        TreeService service = RetrofitClient.getInstance(authorization).create(TreeService.class);
        service.modifyTreeEnvironmentInfo(treeEnvironmentInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + response.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + response.toString());

                            // 여기서 사진 수정 메서드 호출할 것
                            checkModifyEnvironmentInfo.setValue("success");

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + response.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + response);

                                checkModifyEnvironmentInfo.setValue("fail");

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


    // 수목 병해정보 수정
    public void  modifyTreePestInfo(String authorization, TreePestInfoDTO treePestInfo){
        Log.d(TAG, "modifyTreePestInfo 호출 : "+treePestInfo);
        TreeService service = RetrofitClient.getInstance(authorization).create(TreeService.class);
        service.modifyTreePestInfo(treePestInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "** 성공 / 응답 ** " + response.body());
                            Log.d(TAG, "** 성공 / 응답 ** " + response.toString());

                            // 여기서 사진 수정 메서드 호출할 것
                            ResponseVO responseVO=response.body();
                            responseVO.getResult();
                            checkModifyTreePestInfo.setValue(responseVO.getResult());

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + response.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + response);

                                checkModifyTreePestInfo.setValue("fail");

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


}
