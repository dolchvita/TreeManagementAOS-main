package com.snd.app.data.singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.domain.tree.vo.TreeIntegratedVO;

public class SingletonVO {
    private static SingletonVO instance = null;
    public MutableLiveData<TreeIntegratedVO> treeIntegratedVO = new MutableLiveData<>();


    private SingletonVO() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SingletonVO getInstance() {
        if (instance == null) { //if there is no instance available... create new one
            synchronized (SingletonVO.class) {
                if (instance == null) instance = new SingletonVO();
            }
        }
        return instance;
    }


    public LiveData getData() {
        return treeIntegratedVO;
    }

    public void setData(TreeIntegratedVO treeIntegratedData) {
        treeIntegratedVO.setValue(treeIntegratedData);
    }



}
