package com.snd.app.ui.write;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.singleton.LocationRepository;
import com.snd.app.data.singleton.MapViewManager;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.RegistMapLoadingFrBinding;
import com.snd.app.domain.tree.dto.TreeCoordinateDTO;
import com.snd.app.domain.tree.dto.TreeListRangeDTO;
import com.snd.app.ui.write.tree.RegistTreeBasicInfoFragment;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;


public class MapLoadingFragment extends TMFragment {
    RegistMapLoadingFrBinding mapLoadingFrBinding;
    MapLoadingViewModel mapLoadingVM;
    LocationCallback locationCallback;
    LocationRepository locationRepository;
    private MapViewManager mapViewManager;
    private MapView mapView;
    RelativeLayout loadingLayoutBox;
    LinearLayout mapLoading_button_layout;
    RelativeLayout treeBasicKakaoMapLayout;
    SharedPreferenceManager sharedPreferencesManager;
    private AlertDialog.Builder alertDialogBuilder;
    MapPoint.GeoCoordinate redMarkersPinned;
    Boolean callMethod1 = false;
    MaterialButton bt_pinned;
    boolean flag = false;

    @Nullable
    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mapLoadingFrBinding = DataBindingUtil.inflate(inflater, R.layout.regist_map_loading_fr, container, false);
        mapLoadingFrBinding.setLifecycleOwner(this);
        mapLoadingVM = new ViewModelProvider(getActivity()).get(MapLoadingViewModel.class);
        mapLoadingFrBinding.setMapLoadingVM(mapLoadingVM);
        bt_pinned = mapLoadingFrBinding.btPinned;
        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        mapLoadingVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        locationRepository = new LocationRepository(getContext());
        locationRepository.setPermissionGranted(true);
        locationRepository.startTracking();

        return  mapLoadingFrBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        initMapView();
        getLocationRepository();

        // 현재 위치를 트래킹하여 좌표를 잡아오면..!
        if( mapViewManager.currentMapopint != null){
            mapViewManager.currentMapopint.observe(getActivity(), geoCoordinate -> {
                if(geoCoordinate != null){
                    getTreeInfoListByRange(geoCoordinate);
                }
            });
        }


        // 드래그하여 지도 중심점이 바뀔 때
        mapViewManager.mapViewCenterPoint.observe(getActivity(), geoCoordinate -> {
            getTreeInfoListByRange(geoCoordinate);
        });


        // 빨간핀의 중심좌표가 바뀔 때
        mapViewManager.redMarkersPoint.observe(getActivity(), geoCoordinate -> {
            saveLocation(geoCoordinate.latitude, geoCoordinate.longitude);
            redMarkersPinned = geoCoordinate;
        });


        // 싱글턴에서 뽑혀오는 위치 (초기 위치 설정)
        locationRepository.locationVO.observe(getActivity(), location -> {
            initLocationData(location);
        });


        locationRepository.accuracy.observe(getActivity(), aFloat -> {
            mapLoadingVM.accuracy.set(aFloat.toString());
        });


        mapLoadingVM.getTreeInfoListByRange().observe(getActivity(), objects -> {
            setAddAllLocationMarker(convertToTreeCoordinateDTO(objects));
        });


        mapLoadingVM.addMarker.observe(getActivity(), treeCoordinateDTO -> {
            setAddAllLocationMarker(treeCoordinateDTO);
        });


        mapLoadingVM._resultLiveData.observe(getActivity(), s -> {
            checkLocation();
          //  ((RegistTreeInfoActivity) getActivity()).checkLocation();
        });


        mapLoadingVM._back.observe(getActivity(), s -> {
            finishActivity();
        });


        mapLoadingVM.mapType.observe(getActivity(), s -> {
            mapViewManager.setMapView((s.equals("Hybrid")) ? "Hybrid" : "Normal");
        });


        mapLoadingVM.pinnedBt.observe(getActivity(), s -> {
            flag = !flag;
            mapViewManager.pinnedBt.setValue(flag);
            bt_pinned.setBackgroundColor(Color.parseColor((flag) ? "#4DFFFFFF" : "#FFCC00"));
        });

    }   /* ./onResume */



    public void checkLocation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("현재 위치가 맞습니까?");
        builder.setMessage("");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.write_content, new RegistTreeBasicInfoFragment()).commit();
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


    private void initLocationData(Location location){
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        if(!callMethod1){
            setMapLoadingData(latitude, longitude);

            if(saveLocation(latitude, longitude).equals("위치 저장 실패")){
                failProcess();
            }
            callMethod1 = true;
        }
    }


    private void getTreeInfoListByRange(MapPoint.GeoCoordinate mapPoint){
        if(mapPoint != null){
            TreeListRangeDTO treeListRangeDTO = new TreeListRangeDTO();
            treeListRangeDTO.setLatitude(mapPoint.latitude);
            treeListRangeDTO.setLongitude(mapPoint.longitude);
            treeListRangeDTO.setRange(500.0);
            treeListRangeDTO.setTreeCount(300);
            mapLoadingVM.loadTreeInfoListByRange(treeListRangeDTO);
        }
    }


    public void setAddAllLocationMarker(List<TreeCoordinateDTO> dtoList){
        if(flag){
            mapView.removeAllPOIItems();
            mapViewManager.addMarkers2(redMarkersPinned.latitude, redMarkersPinned.longitude);
        }else {
            mapViewManager.removeMarkers();
        }
        for(TreeCoordinateDTO dto : dtoList){
            mapViewManager.addMarkers(dto.getLatitude(), dto.getLongitude(), dto.getNfc());
        }
    }


    private List<TreeCoordinateDTO> convertToTreeCoordinateDTO(List<Object> objects){
        List<TreeCoordinateDTO> dtoList = new ArrayList<>();
        for(Object obj : objects){
            Gson gson = new Gson();
            String jsonData = gson.toJson(obj);        // Object -> Json -> DTO
            TreeCoordinateDTO dto = new TreeCoordinateDTO();
            dto = gson.fromJson(jsonData, TreeCoordinateDTO.class);
            dtoList.add(dto);
        }
        return dtoList;
    }


    private void initMapView(){
        mapViewManager = new MapViewManager(getContext());
        mapView = mapViewManager.getMapView();
        mapLoading_button_layout = mapLoadingFrBinding.mapLoadingButtonLayout;
        treeBasicKakaoMapLayout = mapLoadingFrBinding.loadingKakaoMap;
        loadingLayoutBox = mapLoadingFrBinding.loadingLayoutBox;

        mapViewManager.initMapView();
        treeBasicKakaoMapLayout.removeAllViews();
        treeBasicKakaoMapLayout.addView(mapView);
    }


    public void getLocationRepository(){
        locationRepository.getSatellites().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer satellites) {
                if (locationRepository != null && satellites > 1 && satellites < 500){
                    mapLoadingVM.satellite.set(satellites.toString());
                    locationRepository.checkLocationAccuracy();
                }
            }
        });
    }


    // 현재 위치 마커 표시 (빨강이)
    public void setMapLoadingData(Double latitude, Double longitude){
        loadingLayoutBox.setVisibility(View.GONE);
        mapLoading_button_layout.setVisibility(View.VISIBLE);
        mapViewManager.addMarkers2(latitude, longitude);
    }


    public void setAddAllLocationMarker(TreeCoordinateDTO dto){
        for(int i = 0; i < mapLoadingVM.objectSize; i++){
            mapViewManager.addMarkers(dto.getLatitude(), dto.getLongitude(), dto.getNfc());
        }
    }


    // 위치 좌표를 저장하는 메서드 --> 기본정보 입력시 사용
    public String saveLocation(Double latitude, Double longitude){
        if(sharedPreferencesManager.saveString("latitude", String.valueOf(latitude)) && sharedPreferencesManager.saveString("longitude", String.valueOf(longitude))){
            return "위치 저장 성공";
        }
        return "위치 저장 실패";
    }


    public void failProcess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("데이터 로드 오류가 발생했습니다.");
        alertDialogBuilder.setMessage("처음부터 다시 시도해주세요.");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }


    public void finishActivity(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("뒤로 나가시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapLoadingFrBinding = null;
        if (locationCallback != null) {
            LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(locationCallback);
        }
        locationCallback = null;
        locationRepository.getSatellites().removeObservers(this);
        locationRepository = null;
        if (treeBasicKakaoMapLayout != null && mapView != null) {
            treeBasicKakaoMapLayout.removeView(mapView);
        }
        treeBasicKakaoMapLayout = null;
        if(mapViewManager != null){
            mapViewManager.cleanUp();
        }
        mapViewManager = null;
        loadingLayoutBox = null;
        mapLoading_button_layout = null;
        sharedPreferencesManager = null;
        alertDialogBuilder = null;
        redMarkersPinned = null;
        bt_pinned = null;
    }


}