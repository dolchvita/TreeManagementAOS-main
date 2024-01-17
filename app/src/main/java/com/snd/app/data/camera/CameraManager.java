package com.snd.app.data.camera;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
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
import java.nio.channels.Channels;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CameraManager {
    protected String TAG = this.getClass().getName();
    //private MutableLiveData<Uri> _savedUri = new MutableLiveData<>();       // 화면 표시를 위해 사진의 uri 전달
    //public LiveData<Uri> savedUri = _savedUri;



    private ImageCapture imageCapture;  // 카메라 설정을 담고 있는 객체
    private File currentPhotoFile;      // 저장 프로세스 적용시 Bitmap 생성을 위한 파일 전달
    //private Activity activity;
    GetTreeBasicInfoViewModel getTreeBasicInfoVM;
    private Camera camera; // 카메라 객체
    ProcessCameraProvider cameraProvider;
    ScaleGestureDetector scaleGestureDetector;
    String FILENAME_FORMAT = "yyyyMMdd_HHmmss";  // 리파지토리에서 수정예정

    private ExecutorService cameraExecutor;  // 비동기 실행 객체
    CameraRepository cameraRepository;


    public CameraManager(Activity activity) {
        //this.activity = activity;
        getTreeBasicInfoVM = new ViewModelProvider((ViewModelStoreOwner) activity).get(GetTreeBasicInfoViewModel.class);
        cameraRepository = new CameraRepository();
    }


    public File getCurrentPhotoFile() {
        return cameraRepository.currentPhotoFile;
    }

    /* ----------------------------------------- Preview ----------------------------------------- */

    public void startCameraX(PreviewView viewFinder, Activity activity) {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(activity);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider, viewFinder, activity);

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

                    /* LifecycleOwner 연결 */
                    camera = cameraProvider.bindToLifecycle(
                            (LifecycleOwner) activity,
                            cameraSelector,
                            preview,
                            imageCapture);
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.d(TAG, " ExecutionException * InterruptedException " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(activity));
    }


    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider, PreviewView viewFinder, Activity activity) {
        Log.d(TAG, "** bindCameraPreview - activity 확인 ** " + activity);

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
            getScaleGestureDetector(activity);
        }
    }


    public void getScaleGestureDetector(Activity activity){
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

    // 2. 사진 촬영 - 백그라운드 처리할 부분

    public void takePhoto(Activity activity){
        cameraRepository.takePhoto(activity, imageCapture);
    }

    public void saveImageToGallery(Activity activity){
        cameraRepository.saveImageToGallery(activity);
    }

    public File uriToFile(Context context, Uri contentUri) {
        return cameraRepository.uriToFile(context, contentUri);
    }

    public LiveData<Uri> getSavedUri(){
        return cameraRepository.savedUri;
    }



    /*
    public void takePhoto() {
        Runnable savePhotoTask = new Runnable() {
            @Override
            public void run() {
                // 사진 저장 로직
                File photoFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg");
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
                imageCapture.takePicture(outputFileOptions, cameraExecutor,
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                Uri uri = outputFileResults.getSavedUri();
                                // 3-1) 화면에 프리뷰 될 이미지
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
        };
    }


    public void saveImageToGallery(){
        cameraExecutor.execute(() -> {
            String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis());
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
            }
            ContentResolver resolver = activity.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            try (OutputStream stream = resolver.openOutputStream(uri)) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, _savedUri.getValue());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                Toast.makeText(activity.getApplicationContext(), "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e(TAG, "Error saving image to gallery", e);
            }
        });
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

     */


    /* ----------------------------------------- Destroy ----------------------------------------- */

    public void releaseResources(){
        cameraRepository.releaseResources();

        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            cameraProvider = null;
        }
        /*
        if (_savedUri != null) {
            _savedUri.setValue(null);
            savedUri.removeObservers(null);
            _savedUri = null;
        }

         */
        camera = null;
        imageCapture = null;
        currentPhotoFile = null;
        scaleGestureDetector  = null;
        FILENAME_FORMAT = null;

        if(cameraExecutor != null){
            cameraExecutor.shutdown();
            cameraExecutor = null;
        }
    }


}
