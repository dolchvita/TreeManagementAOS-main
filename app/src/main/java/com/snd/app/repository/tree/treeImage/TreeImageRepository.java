package com.snd.app.repository.tree.treeImage;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.data.singleton.RetrofitClient;
import com.snd.app.domain.tree.vo.ResponseVO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 서버와 통신할 객체
public class TreeImageRepository {
    // 서버 처리에 대한 결과를 뷰모델에게 전달
    // 1. 뷰모델이 가질 이미지 리스트를 전달하기
    // 2. 뷰모델은 적용하기
    // 3. UI 업데이트 하기
    public String TAG = this.getClass().getName();
    private MutableLiveData<List<String>> _filenameList = new MutableLiveData<>();
    public LiveData<List<String>> filenameList = _filenameList;


    /* 확인할 부분
    * 1) 이미지 수정
    * 2) 이미지 삭제 */
    private MutableLiveData<String> _modifyResult = new MutableLiveData<>();   // 서버 반응
    public LiveData<String> modifyResult = _modifyResult;

    private MutableLiveData<String> _deleteResult = new MutableLiveData<>();   // 서버 반응
    public LiveData<String> deleteResult = _deleteResult;


    /* ------------------------------------------------- CREATE METHODS ------------------------------------------------- */

    // 수목 기본정보 이미지등록
    public void registerTreeImage(String Authorization, String nfc, List<File> currentFileList){
        TreeImageService service = RetrofitClient.getInstance(Authorization).create(TreeImageService.class);
        RequestBody tagId = RequestBody.create(MediaType.parse("text/plain"), nfc);
        List<MultipartBody.Part> parts = new ArrayList<>();

        for (int i = 0; i < currentFileList.size(); i++) {
            File file = currentFileList.get(i);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            parts.add(MultipartBody.Part.createFormData("image" + (i + 1), file.getName(), requestBody));
        }
        service.registerTreeImage(tagId, parts)
                .subscribeOn(Schedulers.io()) // 작업을 IO 스레드에서 실행
                .observeOn(AndroidSchedulers.mainThread()) // 결과를 메인(UI) 스레드에서 받음
                .subscribe(new io.reactivex.Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> response) {
                        if (response.isSuccessful()) {
                            ResponseVO responseVO = response.body();
                            String result=responseVO.getResult();
                            _modifyResult.setValue(result);

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + response.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + response);


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


    // 수목 기본정보 이미지 추가 등록 (이미 하나가 존재할 경우, 하나만 추가로 넣기)
    public void registerTreeImage2(String Authorization, String nfc, List<File> currentFileList, int num){
        TreeImageService service = RetrofitClient.getInstance(Authorization).create(TreeImageService.class);
        RequestBody tagId = RequestBody.create(MediaType.parse("text/plain"), nfc);
        List<MultipartBody.Part> parts = new ArrayList<>();

        File file = currentFileList.get(0);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        parts.add(MultipartBody.Part.createFormData("image" + (num), file.getName(), requestBody));
        Log.d(TAG, "** 등록될 사진 파일 **" + file);

        service.registerTreeImage(tagId, parts)
                .subscribeOn(Schedulers.io()) // 작업을 IO 스레드에서 실행
                .observeOn(AndroidSchedulers.mainThread()) // 결과를 메인(UI) 스레드에서 받음
                .subscribe(new io.reactivex.Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> response) {
                        if (response.isSuccessful()) {
                            ResponseVO responseVO = response.body();
                            String result = responseVO.getResult();
                            _modifyResult.setValue(result);
                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + response.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + response.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + response);
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


    /* ------------------------------------------------- READ METHODS ------------------------------------------------- */

    // 수목 저장된 파일명 리스트 반환
    public void getTreeImageFilenameList(String Authorization, String tagId){
        TreeImageService service = RetrofitClient.getInstance(Authorization).create(TreeImageService.class);
        Call<ResponseVO> call = service.getTreeImageFilenameList(tagId);
        call.enqueue(new Callback<ResponseVO>() {
            @Override
            public void onResponse(Call<ResponseVO> call, Response<ResponseVO> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                    Log.d(TAG,"** 성공 / 응답 ** " + response.toString());

                    ResponseVO responseVO = response.body();
                    List<String> list = (List<String>)responseVO.getData();
                    _filenameList.setValue(list);

                }else {
                    Log.d(TAG,"** 오류 / 응답 ** " + response.body());
                    Log.d(TAG,"** 오류 / 응답 ** " + response.toString());
                }
            }
            @Override
            public void onFailure(Call<ResponseVO> call, Throwable t) {
                Log.d(TAG,"** 통신 실패 ** "+t.getMessage());
            }
        });
    }


    /* ------------------------------------------------- UPDATE METHODS ------------------------------------------------- */

    // 수목 기본정보 이미지수정 모두
   public void  modifyTreeImageAll(String Authorization, List<File> currentFileList, String tagIdstr){
       TreeImageService service = RetrofitClient.getInstance(Authorization).create(TreeImageService.class);
       RequestBody tagId = RequestBody.create(MediaType.parse("text/plain"), tagIdstr);
       List<MultipartBody.Part> parts = new ArrayList<>();

       if(currentFileList != null){
           for (int i = 0; i < currentFileList.size(); i++) {
               File file = currentFileList.get(i);
               RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
               parts.add(MultipartBody.Part.createFormData("image" + (i + 1), file.getName(), requestBody));
           }
       }
       service.modifyTreeImageAll(tagId, parts)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new io.reactivex.Observer<Response<ResponseVO>>() {
                   @Override
                   public void onSubscribe(Disposable d) {
                   }
                   @Override
                   public void onNext(Response<ResponseVO> response) {
                       if (response.isSuccessful()) {
                           Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                           Log.d(TAG,"** 성공 / 응답 ** " + response.toString());

                           ResponseVO responseVO = response.body();
                           String result = responseVO.getResult();
                           _modifyResult.setValue(result);

                       } else {
                           Log.d(TAG,"** 오류 / 응답 ** " + response.body());
                           Log.d(TAG,"** 오류 / 응답 ** " + response.toString());
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


    // 수목 기본정보 이미지수정 (특정 하나만)
    public void  modifyTreeParticularImage(String Authorization, String tagIdStr, String keywordStr, File file){
       Log.d(TAG, "수정할 사진 확인하기 ** " + file);
        TreeImageService service = RetrofitClient.getInstance(Authorization).create(TreeImageService.class);

        RequestBody tagId = RequestBody.create(MediaType.parse("text/plain"), tagIdStr);
        RequestBody keyword = RequestBody.create(MediaType.parse("text/plain"), keywordStr);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        service.modifyTreeParticularImage(tagId, keyword, part)
                .subscribeOn(Schedulers.io()) // 작업을 IO 스레드에서 실행
                .observeOn(AndroidSchedulers.mainThread()) // 결과를 메인(UI) 스레드에서 받음
                .subscribe(new io.reactivex.Observer<Response<ResponseVO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(Response<ResponseVO> response) {
                        if (response.isSuccessful()) {
                            // 서버 응답이 성공인 경우
                            Log.d(TAG,"** 성공 / 응답 ** " + response.body());
                            Log.d(TAG,"** 성공 / 응답 ** " + response.toString());
                            ResponseVO responseVO = response.body();

                            String result = responseVO.getResult();
                            _modifyResult.setValue(result);

/*                            if (data != null){
                                Log.d(TAG,"** data ** " + data.toString());
                                int result = (int) Double.parseDouble(data.toString());

                                Log.d(TAG,"** data** " + result);
                            }

 */
                        } else {
                            Log.d(TAG,"** 오류 / 응답 ** " + response.body());
                            Log.d(TAG,"** 오류 / 응답 ** " + response.toString());
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


    /* -----------------------------------------------DELETE METHODS----------------------------------------------- */

    // 수목 기본 정보 이미지삭제
    public void deleteTreeImage(String Authorization, String tagId, String keyword) {
        TreeImageService service = RetrofitClient.getInstance(Authorization).create(TreeImageService.class);
        service.deleteTreeImage(tagId, keyword)
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
                            ResponseVO vo = responseVOResponse.body();
                            Log.d(TAG, "** 데이터 확인 ** " +vo.getData());

                            _deleteResult.setValue(vo.getResult());
                            Log.d(TAG, "** 결과 확인 ** " +vo.getResult());

                        } else {
                            try {
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.body());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.toString());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse.errorBody().string());
                                Log.d(TAG, "** 오류 / 응답 ** " + responseVOResponse);

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
