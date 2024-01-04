package com.snd.app.data.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
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
    String FILENAME_FORMAT = "yyyyMMdd_HHmmss"; // 리파지토리에서 수정예정
    private MutableLiveData<Uri> _savedUri = new MutableLiveData<>();
    public LiveData<Uri> savedUri = _savedUri;
    private ImageCapture imageCapture;  // 카메라 설정을 담고 있는 객체
    private File currentPhotoFile;
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

    public void startCameraX(PreviewView viewFinder) {
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
                imageCapture = new ImageCapture.Builder().build();

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


    void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider, PreviewView viewFinder) {
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

    // 사진 찍는 메서드
    public void takePhoto() {
        final ImageCapture imageCapture = this.imageCapture;
        if (imageCapture == null) {
            return;
        }
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(
                activity.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
                .build();
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(activity),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e(TAG, "Photo capture failed : " + exc.getMessage(), exc);
                    }
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Toast.makeText(activity.getApplicationContext(), "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        _savedUri.setValue(output.getSavedUri());

                        // 파일 객체로 변환
                        Uri savedUri = output.getSavedUri();
                        currentPhotoFile = uriToFile(activity, savedUri); // Uri를 File로 변환
                    }
                }
        );/* ./takePicture */
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
