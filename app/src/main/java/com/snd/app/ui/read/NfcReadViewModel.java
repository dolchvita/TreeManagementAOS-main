package com.snd.app.ui.read;

import androidx.lifecycle.LiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.repository.tree.TreeUseCase;

import javax.inject.Inject;

public class NfcReadViewModel extends TMViewModel {
    @Inject
    TreeUseCase treeUseCase;

    public NfcReadViewModel() {
        treeUseCase = getAppComponent().treeUseCase();
    }


    // 등록된 칩인지 확인
    public LiveData<String> failCheck(){
        return treeUseCase.failCheck();
    }


}
