package com.snd.app.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.flexbox.FlexboxLayout;
import com.snd.app.R;

import java.util.ArrayList;
import java.util.List;


public class TreeHashtagCustomAdapter {
    public String TAG = this.getClass().getName();
    /* DATA */
    public MutableLiveData<Integer> removeItem = new MutableLiveData<>();
    public MutableLiveData<Integer> modifyPeocess = new MutableLiveData<>();
    public MutableLiveData<String> error = new MutableLiveData<>();
    List<String> hashtagList = new ArrayList<>();
    int position;   // 해당 itemView 인덱스
    /* VIEW */
    public AppCompatTextView tr_hashtag;
    AppCompatImageView bt_pencil;
    AppCompatImageView bt_trash;
    FlexboxLayout flexboxLayout;
    View constraintLayout;


    // View 요소 하나에 부여되는 것
    public View onCreateViewHolder(LayoutInflater inflater, FlexboxLayout flexboxLayout, int position){
        constraintLayout = inflater.inflate(R.layout.item_hashtag, flexboxLayout, false);
        this.flexboxLayout = flexboxLayout;
        this.position = position;

        // 레이아웃 내의 뷰 요소에 접근
        tr_hashtag = constraintLayout.findViewById(R.id.tr_hashtag);
        bt_pencil = constraintLayout.findViewById(R.id.bt_pencil);
        bt_trash = constraintLayout.findViewById(R.id.bt_trash);


        // 수정 버튼
        bt_pencil.setOnClickListener(v -> {
            modifyPeocess.setValue(position);
        });


        // 지우기 버튼
        bt_trash.setOnClickListener(v -> {
            showAlertDialog(position);
        });


        return constraintLayout;
    }   /* ./onCreateViewHolder */


    public void addText(String hashtag){
        hashtagList.add(hashtag);
    }



    public void bind(String text){
        if(text != null) {
            tr_hashtag.setText(text);
            hashtagList.add(text);
        }
        flexboxLayout.addView(constraintLayout);
    }


    public void setHashtagList(List<String> hashtagList){
        this.hashtagList = hashtagList;
    }


    public void setItemBind(String text){
        tr_hashtag.setText(text);
        flexboxLayout.addView(constraintLayout);
    }


    public int getItemCount(){
        return hashtagList.size();
    }


    private void showAlertDialog(int clickedPosition) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(constraintLayout.getContext());
        alertDialogBuilder.setTitle("");
        alertDialogBuilder.setMessage("태그를 지우시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeHashtagItem(clickedPosition);
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }


    /* 이 메서드 점검하기 */
    public void removeHashtagItem(int position){
        if(hashtagList != null && !hashtagList.isEmpty() && position < hashtagList.size() && position >= 0){
            if(hashtagList.get(position) != null){
                String item = hashtagList.get(position);
                removeItem.setValue(position);
                hashtagList.remove(item);  // 실제 지우기
            }

        }else {
            Log.d(TAG, "else 문 진입 ** " + position);
            error.setValue("error");
        }
    }


    public void removeResources(){
        tr_hashtag = null;
        bt_pencil = null;
        bt_trash = null;
        flexboxLayout = null;
        constraintLayout = null;
        removeItem.removeObserver(null);
        modifyPeocess.removeObserver(null);
        error.removeObserver(null);
    }


}
