package com.snd.app.common;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.data.singleton.SharedPreferenceManager;


// 모든 액티비티가 상속받을 최상위 객체
public class TMActivity extends AppCompatActivity {
   protected String TAG = this.getClass().getName();
   protected SharedPreferences sharedPreferences;
   protected SharedPreferences.Editor editor;
   SharedPreferenceManager sharedPreferencesManager;
   AlertDialog.Builder builder;

   // 이미지 권한
   private static final int REQUEST_IMAGE_CAPTURE = 1;
   protected int REQUEST_IMAGE_CODE = 0;
   // 위치 권한
   protected static final int REQUEST_LOCATION_PERMISSION = 1;
   // 사진첩 접근 권한
   public static final int REQUEST_GALLERY = 102;
   // 다른 클래스에서 사용 가능
   public static LocationManager locationManager;
   public static LocationListener locationListener;
   public double latitude;
   public double longitude;
   TMViewModel tmVM;
   /* STATIC */
   public final int BASIC = 1;
   public final int SPACIFICLOCATION = 2;
   public final int STATUS = 3;
   public final int ENVIRONMENT = 4;
   public final int PEST = 5;


   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      editor = sharedPreferences.edit();
      sharedPreferencesManager = SharedPreferenceManager.getInstance(this.getApplicationContext());
      tmVM = new ViewModelProvider(this).get(TMViewModel.class);
      builder = new AlertDialog.Builder(this);

      locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      if(locationManager != null){
         locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
               Log.d(TAG, "** onLocationChanged 호출 **");
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
               Log.d(TAG, "onStatusChanged: " + provider);
            }
            @Override
            public void onProviderEnabled(String provider) {
               Log.d(TAG, "onProviderEnabled: " + provider);
            }
            @Override
            public void onProviderDisabled(String provider) {
               Log.d(TAG, "onProviderDisabled: " + provider);
            }
         };
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         Window window = getWindow();
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         window.setStatusBarColor(Color.TRANSPARENT);

         View decorView = window.getDecorView();
         int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
         decorView.setSystemUiVisibility(option);
      }
   }


   @Override
   protected void onResume() {
      super.onResume();
      NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
      if (nfcAdapter != null) {
         nfcAdapter.disableForegroundDispatch(this);
      }
      if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
              != PackageManager.PERMISSION_GRANTED) {
         requestPermissions();
      }
   }


   // 1 제일 먼저 동작하는 메서드 !
   protected void requestPermissions() {
      Log.d(TAG, "TMAct - requestPermissions 호출");
      // 1 카메라 권한
      // 2 사진 저장할 수 있는 권한
      // 3 위치 권한
      if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
              != PackageManager.PERMISSION_GRANTED ||
              ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                      != PackageManager.PERMISSION_GRANTED ||
              ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                      != PackageManager.PERMISSION_GRANTED ||
              ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                      != PackageManager.PERMISSION_GRANTED
      ) {
         ActivityCompat.requestPermissions(this,
                 new String[]{
                         android.Manifest.permission.CAMERA,
                         Manifest.permission.WRITE_EXTERNAL_STORAGE,
                         Manifest.permission.ACCESS_FINE_LOCATION,
                         Manifest.permission.READ_EXTERNAL_STORAGE
                 },
                 REQUEST_IMAGE_CAPTURE);
      } else {
         // 이미 권한이 허용된 경우에 대한 처리
         REQUEST_IMAGE_CODE = 1;
         locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
      }
   }


   // 2 권한 요청에 대한 결과
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      // 카메라 허용
      if (requestCode == REQUEST_IMAGE_CAPTURE) {
         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            editor.putBoolean("camera_permission_granted", true);
            editor.apply();
         } else {
            editor.putBoolean("camera_permission_granted", false);
            editor.apply();
         }
      }
      // 위치 허용
      if (requestCode == REQUEST_LOCATION_PERMISSION) {
         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "** onRequestPermissionsResult - 위치 허용 **");

         } else {
            Toast.makeText(this, "위치 권한이 거부되어 앱을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
         }
      }
   }


   public void log(String msg){
      Log.d(TAG, msg);
   }


   public void back(){
      builder.setTitle("입력 중인 내용을 취소하시겠습니까?");
      builder.setMessage("현재 페이지에서 입력 중인 내용은 저장되지 않습니다.");
      builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            finish();
         }
      });
      builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
         }
      });
      AlertDialog dialog = builder.create();
      dialog.show();
   }


   @Override
   protected void onDestroy() {
      super.onDestroy();
      sharedPreferences = null;
      sharedPreferencesManager = null;
      editor = null;
      builder = null;
      if (locationManager != null && locationListener != null) {
         locationManager.removeUpdates(locationListener);
      }
      locationManager = null;
      locationListener = null;
   }


}