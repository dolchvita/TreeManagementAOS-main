package com.snd.app.repository.tree.treeHashtag;

import com.snd.app.domain.tree.dto.TreeHashtagModifyDTO;
import com.snd.app.domain.tree.vo.ResponseVO;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TreeHashtagService {

    /* ----------------------------------------------- CREATE ----------------------------------------------- */

    // 수목 해시태그 Append
    @POST("app/tree/hashtag/append")
    Observable<Response<ResponseVO>> appendTreeHashtag(@Body TreeHashtagModifyDTO treeHashtagModify);


    /* ----------------------------------------------- UPDATE ----------------------------------------------- */

    // 수목 해시태그 Update
    @POST("app/tree/hashtag/update")
    Observable<Response<ResponseVO>> updateTreeHashtag(@Body TreeHashtagModifyDTO treeHashtagModify);


    /* ----------------------------------------------- DELETE ----------------------------------------------- */

    // 수목 해시태그 Delete
    @POST("app/tree/hashtag/delete")
    Observable<Response<ResponseVO>> deleteTreeHashtag(@Body TreeHashtagModifyDTO treeHashtagModify);


}
