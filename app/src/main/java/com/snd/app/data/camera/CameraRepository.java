package com.snd.app.data.camera;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CameraRepository {
    private ExecutorService cameraExecutor;  // 비동기 실행 객체
    protected String TAG = this.getClass().getName();
    String FILENAME_FORMAT = "yyyyMMdd_HHmmss";  // 리파지토리에서 수정예정
    private MutableLiveData<Uri> _savedUri = new MutableLiveData<>();       // 화면 표시를 위해 사진의 uri 전달
    public LiveData<Uri> savedUri = _savedUri;
    public File currentPhotoFile;      // 저장 프로세스 적용시 Bitmap 생성을 위한 파일 전달

    private MutableLiveData<String> _result1 = new MutableLiveData<>();
    public LiveData<String> result1 = _result1;



    public CameraRepository() {
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    public void takePhoto(Activity activity, ImageCapture imageCapture){
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
                                _savedUri.postValue(uri);

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

        cameraExecutor.execute(savePhotoTask);
    }


    public void saveImageToGallery(Activity activity){
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



    /* ----------------------------------------- Photo ----------------------------------------- */

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


    public void releaseResources(){
        cameraExecutor.shutdown();
    }


}
