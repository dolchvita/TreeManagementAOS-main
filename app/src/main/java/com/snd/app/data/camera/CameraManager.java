package com.snd.app.data.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.snd.app.ui.read.GetTreeBasicInfoViewModel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import io.reactivex.Completable;
import io.reactivex.Single;

public class CameraManager {
    protected String TAG = this.getClass().getName();

    private ImageCapture imageCapture;  // 카메라 설정을 담고 있는 객체
    public File currentPhotoFile;      // 저장 프로세스 적용시 Bitmap 생성을 위한 파일 전달
    private Activity activity;
    GetTreeBasicInfoViewModel getTreeBasicInfoVM;
    private Camera camera; // 카메라 객체
    ProcessCameraProvider cameraProvider;
    ScaleGestureDetector scaleGestureDetector;
    String FILENAME_FORMAT = "yyyyMMdd_HHmmss";  // 리파지토리에서 수정예정


    public CameraManager(Activity activity) {
        this.activity = activity;
        getTreeBasicInfoVM = new ViewModelProvider((ViewModelStoreOwner) activity).get(GetTreeBasicInfoViewModel.class);
    }


    /* ----------------------------------------- Preview ----------------------------------------- */

    public void  startCameraX(PreviewView viewFinder) {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(activity);

        cameraProviderFuture.addListener(() -> {
            try {
                if(cameraProviderFuture != null && viewFinder != null){
                    cameraProvider = cameraProviderFuture.get();
                    if(cameraProvider != null){
                        bindCameraPreview(cameraProvider, viewFinder);

                        CameraSelector cameraSelector = new CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                .build();
                        Preview preview = new Preview.Builder().build();
                        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                        if(viewFinder.getDisplay() != null){
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
                        }
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.d(TAG, " startCameraX - 오류 발생 * " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(activity));
        
        // 이미지 캐시 줄이기
        imageCapture = null;
    }


    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider, PreviewView viewFinder) {
        if(activity != null){
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

    // 1-2) 사진 촬영을 실제 구현하는 곳
    public Single<Uri> takePhoto() {
        return Single.create(emitter -> {
            File photoFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg");
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            if(imageCapture != null){
                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(activity),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                Uri uri = outputFileResults.getSavedUri();

                                // 저장된 사진 uri
                                emitter.onSuccess(uri);
                            }
                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {

                                /*
                                if (exception.getImageCaptureError() == ImageCapture.ERROR_IMAGE_CAPTURE_FAILED) {
                                    text_no_image.setText("Failed to capture image. Please try again.");
                                } else {
                                    text_no_image.setText("An error occurred while capturing the image.");
                                }

                                 */
                                //log("Image capture failed: " + exception.getMessage());
                                emitter.onError(exception);

                            }
                        });
            }
        });
    }


    public Completable saveImageToGallery(Uri saveUri) {
        return Completable.create(emitter -> {
            Log.d(TAG, "** saveImageToGallery 호출 1 **");
            try {
                String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis());
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
                    Log.d(TAG, "** saveImageToGallery 호출 2 **");
                }

                if(activity != null) {
                    ContentResolver resolver = activity.getContentResolver();
                    Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    if (uri == null) {
                        throw new IOException("Uri insert failed.");
                    }
                    try (OutputStream stream = resolver.openOutputStream(uri)) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, saveUri);  //_savedUri 가 null
                        stream.flush();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        emitter.onComplete(); // 작업 완료 알림
                    }
                } else {
                    throw new NullPointerException("Activity is null");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in saveImageToGallery", e);
                emitter.onError(e); // 오류 발생 알림
            }
        });
    }


    /* ----------------------------------------- Destroy ----------------------------------------- */

    public void releaseResources(){
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            cameraProvider = null;
        }
        camera = null;
        imageCapture = null;
        currentPhotoFile = null;
        scaleGestureDetector  = null;
        FILENAME_FORMAT = null;
        activity = null;
    }


}