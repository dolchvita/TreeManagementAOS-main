package com.snd.app.repository.tree.treeHashtag;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.data.singleton.RetrofitClient;
import com.snd.app.domain.tree.dto.TreeHashtagModifyDTO;
import com.snd.app.domain.tree.vo.ResponseVO;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class TreeHashtagRepository {
    public String TAG = this.getClass().getName();
    private MutableLiveData<String> _response = new MutableLiveData<>();
    public LiveData<String> response = _response;


    /* ------------------------------------------------- CREATE ------------------------------------------------- */

    // 수목 해시태그 Append
    public void appendTreeHashtag(String Authorization, TreeHashtagModifyDTO treeHashtagModify) {
        TreeHashtagService service = RetrofitClient.getInstance(Authorization).create(TreeHashtagService.class);
        service.appendTreeHashtag(treeHashtagModify)
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
                            _response.setValue("append" + responseVO.getResult());

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



    /* ----------------------------------------------- UPDATE ----------------------------------------------- */

    // 수목 해시태그 Update
    public void updateTreeHashtag(String Authorization, TreeHashtagModifyDTO treeHashtagModify) {
        Log.d(TAG, "** dycjdqhsorl** " + treeHashtagModify);

        TreeHashtagService service = RetrofitClient.getInstance(Authorization).create(TreeHashtagService.class);
        service.updateTreeHashtag(treeHashtagModify)
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
                            _response.setValue("update" + responseVO.getResult());

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



    /* ----------------------------------------------- DELETE ----------------------------------------------- */

    // 수목 해시태그 Delete
    public void deleteTreeHashtag(String Authorization, TreeHashtagModifyDTO treeHashtagModify){
        TreeHashtagService service = RetrofitClient.getInstance(Authorization).create(TreeHashtagService.class);
        service.deleteTreeHashtag(treeHashtagModify)
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
                            _response.setValue("delete" + responseVO.getResult());

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
