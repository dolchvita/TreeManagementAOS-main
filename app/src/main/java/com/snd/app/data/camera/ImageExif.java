package com.snd.app.data.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;

import java.io.File;
import java.io.IOException;


public class ImageExif {

    // 이미지 파일의 경로를 받아서 올바르게 회전된 Bitmap을 반환하는 메소드
    public static Bitmap rotateImageIfRequired(File imageFile) {
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        ExifInterface exif = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                exif = new ExifInterface(imageFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
