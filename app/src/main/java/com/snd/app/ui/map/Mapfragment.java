package com.snd.app.ui.map;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.singleton.MapViewManager;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.MainMapFrBinding;
import com.snd.app.domain.tree.dto.TreeCoordinateDTO;
import com.snd.app.domain.tree.dto.TreeListRangeDTO;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;


public class Mapfragment extends TMFragment{
    MainMapFrBinding mapFrBinding;
    MapViewModel mapVM;
    private MapViewManager mapViewManager;
    private MapView mapView;
    RelativeLayout mainKakaoMapLayout;
    private SharedPreferenceManager sharedPreferencesManager;
    TreeSpecificInfoViewModel treeSpecificInfoVM;
    ProgressBar progressBar;    // 로딩시 표시


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mapFrBinding = DataBindingUtil.inflate(inflater, R.layout.main_map_fr, container, false);
        mapVM = new ViewModelProvider(getActivity()).get(MapViewModel.class);
        mapFrBinding.setLifecycleOwner(this);
        mapFrBinding.setMapVM(mapVM);
        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        mapVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        // 상세페이지 프레그먼트 창을 위한 뷰모델
        treeSpecificInfoVM = new ViewModelProvider(this).get(TreeSpecificInfoViewModel.class);
        progressBar = mapFrBinding.progress;
        progressBar.setVisibility(View.VISIBLE);
        return mapFrBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        initMapView();

        // 현재 위치를 트래킹하여 좌표를 잡아오면
        if( mapViewManager.currentMapopint != null){
            mapViewManager.currentMapopint.observe(getActivity(), geoCoordinate -> {
                getTreeInfoListByRange(geoCoordinate);
            });
        }


        // 범위내의 수목 통합 정보 리스트 가져오기
        mapVM.getTreeInfoListByRange().observe(getActivity(), objects -> {
            setAddAllLocationMarker(convertToTreeCoordinateDTO(objects));
        });


        mapVM.failCheck().observe(getActivity(), s -> {
           if(s.equals("The List is Empty")){
               progressBar.setVisibility(View.GONE);
           }
        });


        mapVM.mapType.observe(getActivity(), s -> {
            mapViewManager.setMapView((s.equals("Hybrid") ? "Hybrid" : "Normal"));
        });


        // 드래그하여 지도 중심점이 바뀔 때
        mapViewManager.mapViewCenterPoint.observe(getActivity(), geoCoordinate -> {
            getTreeInfoListByRange(geoCoordinate);
        });


        // 수목 상세 페이지
        if (mapViewManager.changeSpecificPage != null){
            mapViewManager.changeSpecificPage.observe(getActivity(), s -> {
                if(getActivity() != null) {
                    TreeSpecificInfoFragment inputPestInfoDialogFragment = new TreeSpecificInfoFragment();
                    inputPestInfoDialogFragment.show(getChildFragmentManager(), s);
                }
            });
        }

    }   /* ./onResume */


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


    // 최종적으로 마커를 표시하는 메서드
    public void setAddAllLocationMarker(List<TreeCoordinateDTO> dtoList){
        mapView.removeAllPOIItems();
        for(TreeCoordinateDTO dto : dtoList){
            mapViewManager.addMarkers(dto.getLatitude(), dto.getLongitude(), dto.getNfc());
        }
        progressBar.setVisibility(View.GONE);
    }


    private void getTreeInfoListByRange(MapPoint.GeoCoordinate mapPoint){
        if(mapPoint != null){
            TreeListRangeDTO treeListRangeDTO = new TreeListRangeDTO();
            treeListRangeDTO.setLatitude(mapPoint.latitude);
            treeListRangeDTO.setLongitude(mapPoint.longitude);
            treeListRangeDTO.setRange(500.0);
            treeListRangeDTO.setTreeCount(300);
            mapVM.loadTreeInfoListByRange(treeListRangeDTO);
        }
    }


    private void initMapView(){
        mapViewManager = new MapViewManager(getContext());
        mapViewManager.initMapView();
        mapView = mapViewManager.getMapView();
        mainKakaoMapLayout = mapFrBinding.mainKakaoMap;
        mainKakaoMapLayout.addView(mapView);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapFrBinding = null;
        sharedPreferencesManager = null;
        mainKakaoMapLayout = null;
        if(mapViewManager != null){
            mapViewManager.cleanUp();
        }
        mapViewManager = null;
        mapView = null;
        progressBar = null;
    }


}