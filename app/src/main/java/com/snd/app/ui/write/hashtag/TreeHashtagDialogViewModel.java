package com.snd.app.ui.write.hashtag;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.tree.dto.TreeHashtagDTO;
import com.snd.app.repository.tree.treeHashtag.TreeHashtagUseCase;

public class TreeHashtagDialogViewModel extends TMViewModel {
    TreeHashtagUseCase treeHashtagUseCase;
    TreeHashtagDTO treeHashtagDTO;
    private MutableLiveData<Boolean> _isValidationCheck = new MutableLiveData<>(true);
    public LiveData<Boolean> isValidationCheck = _isValidationCheck;


    public TreeHashtagDialogViewModel() {
        treeHashtagUseCase = getAppComponent().treeHashtagUseCase();
        treeHashtagDTO = new TreeHashtagDTO();
    }


    public void hashtagTextFormCheck(){
        String hashReg = "^[가-힣a-zA-Z0-9-]+$";
        _isValidationCheck.setValue((treeHashtagDTO.getHashtag() != null && !treeHashtagDTO.getHashtag().matches(hashReg)) ? false : true);
    }


}
