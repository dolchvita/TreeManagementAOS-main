package com.snd.app.repository.tree.treeHashtag;


import androidx.lifecycle.LiveData;

import com.snd.app.domain.tree.dto.TreeHashtagModifyDTO;

public class TreeHashtagUseCase {
    public String TAG = this.getClass().getName();
    TreeHashtagRepository treeHashtagRepository;


    public TreeHashtagUseCase(TreeHashtagRepository treeHashtagRepository) {
        this.treeHashtagRepository = treeHashtagRepository;
    }


    /* Result */
    public LiveData<String> checkTreeHashtag(){
        return  treeHashtagRepository.response;
    }


    /* ------------------------------------------------- CREATE ------------------------------------------------- */

    public void appendTreeHashtag(String Authorization, TreeHashtagModifyDTO treeHashtagModify){
        treeHashtagRepository.appendTreeHashtag(Authorization, treeHashtagModify);
    }


    /* ----------------------------------------------- UPDATE ----------------------------------------------- */

    public void updateTreeHashtag(String Authorization, TreeHashtagModifyDTO treeHashtagModify){
        treeHashtagRepository.updateTreeHashtag(Authorization, treeHashtagModify);
    }


    /* ------------------------------------------------- DELETE ------------------------------------------------- */

    public void deleteTreeHashtag(String Authorization, TreeHashtagModifyDTO treeHashtagModify){
        treeHashtagRepository.deleteTreeHashtag(Authorization, treeHashtagModify);
    }


}
