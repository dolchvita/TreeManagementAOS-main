package com.snd.app.data.camera;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.snd.app.R;
import com.snd.app.common.TMDialogFragment;
import com.snd.app.databinding.CameraPreviewDialogBinding;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


// 카메라 기능을 대신 구현할 다이얼로그 프레그먼트
public class CameraPreviewDialogFragment extends TMDialogFragment {
    CameraPreviewDialogBinding cameraPreviewDialogBinding;
    CameraPreviewDialogViewModel cameraPreviewDialogVM;
    CameraManager cameraManager;
    PreviewView camera_preview;
    MaterialButton bt_take_photo;

    /* 조건에 따라 보여질 레이아웃 */
    ConstraintLayout camera_preview_layout;
    ConstraintLayout image_preview_layout;

    public MutableLiveData<File> saveFile = new MutableLiveData<>();    // 저장된 파일이 -> RegisterBasicFr 전달 -> VM 전달하여 리스트 구상
    public MutableLiveData<String> saveFileResult = new MutableLiveData<>();   // 별도로 기기 사진첩에 저장 후 알림

    PhotoFileManager photoFileManager;
    private Uri saveUri;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        cameraPreviewDialogBinding = DataBindingUtil.inflate(inflater, R.layout.camera_preview_dialog, null, false);
        cameraPreviewDialogVM = new ViewModelProvider(this).get(CameraPreviewDialogViewModel.class);
        cameraManager = new CameraManager(getActivity());
        cameraPreviewDialogBinding.setVm(cameraPreviewDialogVM);
        camera_preview = cameraPreviewDialogBinding.cameraPreview;
        bt_take_photo = cameraPreviewDialogBinding.btTakePhoto;
        /* Preview */
        camera_preview_layout = cameraPreviewDialogBinding.cameraPreviewLayout;
        image_preview_layout = cameraPreviewDialogBinding.imagePreviewLayout;
        photoFileManager = new PhotoFileManager();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(camera_preview.getSurfaceProvider());
        cameraManager.startCameraX(camera_preview);

        /* Zoom 제어 */
        camera_preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cameraManager.zoomControl(event);
                return true;
            }
        });


        // 1-1) 사진 촬영 - 멀티 스레딩 기법 적용
        cameraPreviewDialogVM.onCameraBt.observe(getActivity(), s -> {
            setVisibleToButtonLayout(View.GONE, View.VISIBLE);

            // 사진을 촬영하는 실질적인 로직을 매니저 객체가 수행하고 그 반응을 줌
            cameraManager.takePhoto()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uri -> {
                        // 사진 촬영 성공, UI 업데이트 등
                        //cameraManager._savedUri.setValue(uri);
                        saveUri = uri;
                        displayCapturedImageForReview(uri); // 이미지 출력 메서드 호출

                        /* 의문이 드는 곳 ! */
                        //cameraManager.currentPhotoFile = photoFileManager.uriToFile(getActivity(), uri);

                    }, error -> {
                        log("사진 처리 실패");
                    });
        });
        return cameraPreviewDialogBinding.getRoot();
    }


    // 1-3) 사진 촬영 성공 후, 이미지 출력
    public void displayCapturedImageForReview(Uri imageUri) {
        // uri 반환받으면, 이를 이용하여 화면에 표시하기 Glide 이용
        if (cameraPreviewDialogBinding != null && cameraPreviewDialogBinding.imagePreview != null) {
            ImageView image_preview = cameraPreviewDialogBinding.imagePreview; // 검토용 ImageView
            Glide.with(getActivity()).load(imageUri).into(image_preview);

            AppCompatButton bt_image_save = cameraPreviewDialogBinding.btImageSave; // 확인 버튼
            AppCompatButton bt_image_cancel = cameraPreviewDialogBinding.btImageCancel; // 다시시도 버튼
            AppCompatImageView bt_file_download = cameraPreviewDialogBinding.imagePreview;  // 사진 파일 저장 버튼


            // 확인 버튼 --> 지금 캡처된 이미지를 데이터로 쓰겠다 !
            bt_image_save.setOnClickListener(v -> {
                File file = photoFileManager.uriToFile(getActivity(), saveUri);
                saveFile.setValue(file);
                dismiss();
            });


            bt_image_cancel.setOnClickListener(v -> {
                setVisibleToButtonLayout(View.VISIBLE, View.GONE);
            });


            // 사진 저장 버튼
            // 2-1) 매니저에게 실질적인 저장로직 수행
            bt_file_download.setOnClickListener(v -> {
                log("/???????");
                cameraManager.saveImageToGallery(saveUri)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            log("갤러리 저장 성공");

                        }, error -> {
                            log("사진 저장 실패 * " + error.getMessage());
                        });
            });
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        /* full-Screen */
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
    }


    private void setVisibleToButtonLayout(int view1, int view2){
        camera_preview_layout.setVisibility(view1);     // 카메라 프리뷰
        image_preview_layout.setVisibility(view2);      // 사진(이미지) 프리뷰
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraPreviewDialogBinding = null;
        camera_preview = null;
        bt_take_photo = null;
        if(cameraManager != null){
            cameraManager.releaseResources();
        }
        this.dismiss();
    }


}
