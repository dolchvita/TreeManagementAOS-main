package com.snd.app.data.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.snd.app.ui.read.GetTreeBasicInfoViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class CameraManager {
    protected String TAG = this.getClass().getName();
    private MutableLiveData<Uri> _savedUri = new MutableLiveData<>();       // 화면 표시를 위해 사진의 uri 전달
    public LiveData<Uri> savedUri = _savedUri;
    private ImageCapture imageCapture;  // 카메라 설정을 담고 있는 객체
    private File currentPhotoFile;      // 저장 프로세스 적용시 Bitmap 생성을 위한 파일 전달
    Activity activity;
    GetTreeBasicInfoViewModel getTreeBasicInfoVM;
    private Camera camera; // 카메라 객체
    ProcessCameraProvider cameraProvider;
    ScaleGestureDetector scaleGestureDetector;


    public CameraManager(Activity activity) {
        this.activity = activity;
        getTreeBasicInfoVM = new ViewModelProvider((ViewModelStoreOwner) activity).get(GetTreeBasicInfoViewModel.class);
    }


    public File getCurrentPhotoFile() {
        return currentPhotoFile;
    }



    /* ----------------------------------------- Preview ----------------------------------------- */

    public void  startCameraX(PreviewView viewFinder) {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(activity);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider, viewFinder);

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(viewFinder.getDisplay().getRotation())
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        (LifecycleOwner) activity, cameraSelector, preview, imageCapture);

                /* camera 객체 생성 */
                camera = cameraProvider.bindToLifecycle(
                        (LifecycleOwner) activity,
                        cameraSelector,
                        preview,
                        imageCapture);
            } catch (ExecutionException | InterruptedException e) {
            }
        }, ContextCompat.getMainExecutor(activity));
    }


    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider, PreviewView viewFinder) {
        Preview preview = new Preview.Builder()
                .build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder().build();
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner) activity, cameraSelector, preview);

        // 카메라 바인딩 추가
        camera = cameraProvider.bindToLifecycle((LifecycleOwner) activity, cameraSelector, preview);
        getScaleGestureDetector();
    }


    public void getScaleGestureDetector(){
        if (camera != null) {
            scaleGestureDetector = new ScaleGestureDetector(activity, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    float currentZoomRatio = camera.getCameraInfo().getZoomState().getValue().getZoomRatio();
                    float delta = detector.getScaleFactor();
                    camera.getCameraControl().setZoomRatio(currentZoomRatio * delta);
                    return true;
                }
            });
        }
    }


    public void zoomControl(MotionEvent event){
        scaleGestureDetector.onTouchEvent(event);
    }


    /* ----------------------------------------- Photo ----------------------------------------- */

    // 2. 사진 촬영
    public void takePhoto() {
        File photoFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(activity),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        /* 3. 캡처된 이미지 처리 */

                        // 3-1) 화면에 프리뷰 될 이미지
                       Uri uri = outputFileResults.getSavedUri();
                        _savedUri.setValue(uri);

                        // 3-2) 서버에 업로드할 파일
                        currentPhotoFile = uriToFile(activity, uri);

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "CameraManager - takePhoto onError " + exception.getMessage());
                    }
                });
    }


    public File uriToFile(Context context, Uri contentUri) {
        File tempFile = null;
        try {
            String fileName = getFileName(context, contentUri);
            InputStream inputStream = context.getContentResolver().openInputStream(contentUri);
            if (inputStream != null) {
                tempFile = new File(context.getCacheDir(), fileName);
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }


    @SuppressLint("Range")
    private String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }



    /* ----------------------------------------- Destroy ----------------------------------------- */

    public void releaseResources(){
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        if (_savedUri != null) {
            _savedUri.setValue(null);
        }
        camera = null;
        scaleGestureDetector  = null;
    }


}
