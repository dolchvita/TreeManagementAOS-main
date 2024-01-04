package com.snd.app.data.camera;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.snd.app.R;
import com.snd.app.common.TMDialogFragment;
import com.snd.app.databinding.CameraPreviewDialogBinding;
import com.snd.app.databinding.CameraPreviewDialogBindingImpl;

import java.io.File;


// 카메라 기능을 대신 구현할 다이얼로그 프레그먼트
public class CameraPreviewDialogFragment extends TMDialogFragment {
    CameraPreviewDialogBinding cameraPreviewDialogBinding;
    CameraPreviewDialogViewModel cameraPreviewDialogVM;
    CameraManager cameraManager;
    PreviewView camera_preview;
    MaterialButton bt_take_photo;
    public MutableLiveData<File> saveFile = new MutableLiveData<>();
    /* 조건에 따라 보여질 레이아웃 */
    ConstraintLayout camera_preview_layout;
    ConstraintLayout image_preview_layout;


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
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(camera_preview.getSurfaceProvider());
        cameraManager.startCameraX(camera_preview);

        /* Zoom */
        camera_preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cameraManager.zoomControl(event);
                return true;
            }
        });


        // 사진 촬영
        cameraPreviewDialogVM.onCameraBt.observe(getActivity(), s -> {
            cameraManager.takePhoto();
            setVisibleToButtonLayout(View.GONE, View.VISIBLE);
        });


        cameraManager.savedUri.observe(getActivity(), uri -> {
            displayCapturedImageForReview(uri);
        });


        return cameraPreviewDialogBinding.getRoot();
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


    public void displayCapturedImageForReview(Uri imageUri) {
        if (cameraPreviewDialogBinding != null && cameraPreviewDialogBinding.imagePreview != null) {
            ImageView image_preview = cameraPreviewDialogBinding.imagePreview; // 검토용 ImageView
            Glide.with(getActivity()).load(imageUri).into(image_preview);

            AppCompatButton bt_image_save = cameraPreviewDialogBinding.btImageSave; // 확인 버튼
            AppCompatButton bt_image_cancel = cameraPreviewDialogBinding.btImageCancel; // 다시시도 버튼


            bt_image_save.setOnClickListener(v -> {
                File file = cameraManager.getCurrentPhotoFile();
                saveFile.setValue(file);
                dismiss();
            });


            bt_image_cancel.setOnClickListener(v -> {
                setVisibleToButtonLayout(View.VISIBLE, View.GONE);
            });
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
        if (cameraManager != null) {
            cameraManager.releaseResources();
        }
        cameraManager = null;
        camera_preview = null;
        bt_take_photo = null;
    }


}
