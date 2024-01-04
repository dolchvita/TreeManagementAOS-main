package com.snd.app.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.snd.app.R;


public class TMFragment extends Fragment {
    public String TAG = this.getClass().getName();
    private AlertDialog.Builder alertDialogBuilder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void finishProccess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("입력 중인 내용을 취소하시겠습니까?");
        alertDialogBuilder.setMessage("현재 페이지에서 입력 중인 내용은 저장되지 않습니다.");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
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


    /* 조회 / 수정 모드 분리 */
    public void editMode(ConstraintLayout readForm, ConstraintLayout editForm, AppCompatButton button, ConstraintLayout border){
        readForm.setVisibility(View.GONE);
        editForm.setVisibility(View.VISIBLE);
        border.setBackgroundResource(R.drawable.border_my2);
        int color = ContextCompat.getColor(getContext(), R.color.kelly_green); // 색상 리소스 가져오기
        button.setBackgroundColor(color);
        button.setText("저 장");
    }

    public void readMode(ConstraintLayout readForm, ConstraintLayout editForm, AppCompatButton button, ConstraintLayout border){
        readForm.setVisibility(View.VISIBLE);
        editForm.setVisibility(View.GONE);
        border.setBackgroundResource(R.drawable.border_my);
        button.setBackgroundResource(R.drawable.button_regist);
        button.setText("수정하기");
    }


    public void log(String msg){
        Log.d(TAG, msg);
    }


    public void failProcess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("데이터 로드 오류가 발생했습니다.");
        alertDialogBuilder.setMessage("로그아웃 후 처음부터 다시 시도해주세요.");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
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


    public void back(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("나가시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
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



}
