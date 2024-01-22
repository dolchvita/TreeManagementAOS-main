package com.snd.app.data.singleton;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationRepository {
    protected String TAG = this.getClass().getName();
    private Context context;
    private MutableLiveData<Boolean> _isGpsEnabled = new MutableLiveData<>();
    private MutableLiveData<Location> _location = new MutableLiveData<>();
    //반환할 좌표
    private MutableLiveData<Location> _locationVO = new MutableLiveData<>();
    public LiveData<Location> locationVO = _locationVO;

    private MutableLiveData<Integer> _satellites = new MutableLiveData<>();
    private LiveData<Integer> satellites = getSatellites();
    private MutableLiveData<String> _provider = new MutableLiveData<>();
    public LiveData<String> provider = _provider;

    private MutableLiveData<Float> _accuracy = new MutableLiveData<>();
    public LiveData<Float> accuracy = _accuracy;
    private LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private LocationCallback locationCallback;
    private boolean isLocationAvailable = false;
    private boolean isLocationStarted = false;
    private boolean isGranted = false;
    private int totalSatelliteCount = 0;
    private long locationReqInterval = 1000L;
    private LocationRequest locationRequest = LocationRequest.create().setInterval(locationReqInterval)
            .setFastestInterval(locationReqInterval).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    FusedLocationProviderClient client;


    public LocationRepository(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        checkLocationSettings();
        initLocationCallback();
    }


    private void checkLocationSettings() {
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                isLocationAvailable = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isLocationAvailable = false;
            }
        });
    }


    private void initLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    _location.setValue(location);
                    _provider.setValue(location.getProvider());
                    _accuracy.setValue(location.getAccuracy());
                }
            }
        };
    }


    public void setPermissionGranted(boolean isGranted) {
        this.isGranted = isGranted;
        if (isGranted) {
            setGpsListener();
        }
    }


    @SuppressLint("MissingPermission")
    private void setGpsListener() {
        if (isGranted) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
                        @Override
                        public void onStarted() {
                            super.onStarted();
                            _isGpsEnabled.setValue(true);
                        }
                        @Override
                        public void onStopped() {
                            super.onStopped();
                            _isGpsEnabled.setValue(false);
                        }
                        @Override
                        public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                            super.onSatelliteStatusChanged(status);
                            int satelliteCount = status.getSatelliteCount();
                            int usedInFixCount = 0;     // 실제 사용 가능한 위성 개수
                            for (int i = 0; i < satelliteCount; i++) {
                                if (status.usedInFix(i)) {
                                    usedInFixCount++;
                                }
                            }
                            totalSatelliteCount = satelliteCount;
                            _satellites.setValue(totalSatelliteCount);
                        }
                    }, null);
                } else {
                    locationManager.addGpsStatusListener(new GpsStatus.Listener() {
                        @Override
                        public void onGpsStatusChanged(int event) {
                            countSatellites();
                        }
                    });
                }
            } catch (SecurityException e) {
                //exitProcess(0);
            }
        }
    }


    // 오차 범위 설정하는 메서드 -> 마지막 위치 대신 가져오기
    public void checkLocationAccuracy() {
        if (context != null) {
            client = LocationServices.getFusedLocationProviderClient(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        client.requestLocationUpdates(locationRequest, locationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    _accuracy.setValue(location.getAccuracy());

                    if (location.getAccuracy() <= 10) {  // 오차 범위가 10미터 이내인 경우
                        Log.d(TAG, "Location with good accuracy: " + location.getLatitude() + ", " + location.getLongitude());

                        Double latitude = location.getLatitude();
                        Double longitude = location.getLongitude();

                        _locationVO.setValue(location);
                    }
                }
            }
        }, null);
        locationCallback = null;
    }


    public LiveData getSatellites(){
        return _satellites;
    }


    @SuppressLint("MissingPermission")
    private void countSatellites() {
        if (isGranted) {
            int seenSatellites = 0;
            int satellitesInFix = 0;
            for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
                if (sat.usedInFix()) {
                    satellitesInFix++;
                }
                seenSatellites++;
            }
            _satellites.setValue(seenSatellites);
        }
    }


    @SuppressLint("MissingPermission")
    public void startTracking() {
        if (isGranted) {
            if (!isLocationStarted) {
                isLocationStarted = true;
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        }
    }


    public void stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        isLocationStarted = false;
    }


}