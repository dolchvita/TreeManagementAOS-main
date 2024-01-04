package com.snd.app.data.singleton;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


public class MapViewManager implements net.daum.mf.map.api.MapView.POIItemEventListener, MapView.MapViewEventListener, MapView.CurrentLocationEventListener {
    private static MapViewManager instance;
    private MapView mapView;
    MapPOIItem currentMarker;
    protected String TAG = this.getClass().getName();
    public MutableLiveData<String> changeSpecificPage = new MutableLiveData<>();
    public MutableLiveData<MapPoint.GeoCoordinate> currentMapopint = new MutableLiveData<>(null);    // 처음 현재 위치의 좌표 전달 (주변 마커 업데이트)
    public MutableLiveData<MapPoint.GeoCoordinate> mapViewCenterPoint = new MutableLiveData<>();     // 드래그된 지도의 중심점 좌표
    public MutableLiveData<MapPoint.GeoCoordinate> redMarkersPoint = new MutableLiveData<>();     // 빨간핀의 좌표

    private boolean isMethodCalled = false;
    public MutableLiveData<Boolean> pinnedBt = new MutableLiveData<>(true);
    public boolean flag = false;


    // Constructor
    public MapViewManager(Context context) {
        mapView = new MapView(context);
        mapView.setPOIItemEventListener(this);
        mapView.setMapViewEventListener(this);    // 이벤트 리스너 추가
        mapView.setZoomLevel(1, true);
    }


    // Singleton
    public static synchronized MapViewManager getInstance(Context context) {
        if (instance == null) {
            instance = new MapViewManager(context);
        }
        return instance;
    }


    public MapView getMapView() {
        return mapView;
    }


    public void initMapView() {
        mapView.setCurrentLocationEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);   // 현재 위치 트래킹
    }


    public void getTreeSpecificLocationView(double latitude, double longitude){
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        mapView.setMapCenterPoint(mapPoint, true);
    }


    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        if(isMethodCalled) {
            return;
        }
        MapPoint centerPoint = mapView.getMapCenterPoint();
        MapPoint.GeoCoordinate centerCoordinate = centerPoint.getMapPointGeoCoord();

        if(centerCoordinate.latitude != 37.56640625 && centerCoordinate.longitude != 126.97787475585938){   // 카카오맵 초기 좌표 피하기
            mapView.setMapCenterPoint(mapPoint, true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);    // 위치 한 번 잡으면 고정
            currentMapopint.setValue(centerCoordinate);
            isMethodCalled = true;
        }
    }


    /** 기존 마커랑 현재 마커 분리하기 **/
    public void addMarkers2(Double latitude, Double longitude) {
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        currentMarker = new MapPOIItem();
        currentMarker.setItemName("Default Marker");
        currentMarker.setTag(0);
        currentMarker.setMapPoint(mapPoint);
        currentMarker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        currentMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        // 수동 움직이기
        currentMarker.setMapPoint(mapView.getMapCenterPoint());
        if(mapView != null){
            mapView.addPOIItem(currentMarker);
            mapView.invalidate();
        }
    }


    // 마커 움직이는대로 위치 Update - > 움직일 때마다 마커가 같이 따라감
    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        if (currentMarker != null && pinnedBt.getValue()){
            currentMarker.setMapPoint(mapPoint);
            redMarkersPoint.setValue(currentMarker.getMapPoint().getMapPointGeoCoord());    // 빨간핀 좌표 업뎃
            mapView.invalidate();
        }
    }


    public void addMarkers(Double latitude, Double longitude, String idHex) {
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(idHex);     // 이름을 제대로 지정하지 않으면 마커가 화면에 안 보이는 경우가 있음
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        if(mapView != null){
            mapView.addPOIItem(marker);
            mapView.invalidate();
        }
    }


    public void removeMarkers(){
        MapPOIItem[] markers = mapView.getPOIItems();
        for (MapPOIItem marker : markers) {
            if (!"Default Marker".equals(marker.getItemName())) {
                mapView.removePOIItem(marker);
            }
        }
    }


    // 빨간 핀만 삭제 (위치 수정시 사용)
    public void removeCurrentMarkers(){
        MapPOIItem[] markers = mapView.getPOIItems();
        for (MapPOIItem marker : markers) {
            if ("Default Marker".equals(marker.getItemName())) {
                mapView.removePOIItem(marker);
            }
        }
    }


    // 화면을 드래그하면 중앙의 좌표값을 업데이트
    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        MapPoint centerPoint = mapView.getMapCenterPoint();
        MapPoint.GeoCoordinate centerCoordinate = centerPoint.getMapPointGeoCoord();
        mapViewCenterPoint.setValue(centerCoordinate);
    }


    // 하이브리드 모드 (위성 + 로드)
    public void setMapView(String mode){
        mapView.setMapType((mode.equals("Hybrid") ? MapView.MapType.Hybrid : MapView.MapType.Standard));
    }


    @Override
    public void onPOIItemSelected(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem) {
    }
    @Override
    public void onCalloutBalloonOfPOIItemTouched(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem) {
        changeSpecificPage.setValue(mapPOIItem.getItemName());
    }
    @Override
    public void onCalloutBalloonOfPOIItemTouched(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
    }
    @Override
    public void onDraggablePOIItemMoved(net.daum.mf.map.api.MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
    }
    @Override
    public void onMapViewInitialized(MapView mapView) {
    }
    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
    }
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
    }
    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }
    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }
    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }
    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }


    public void cleanUp() {
        if (mapView != null) {
            mapView.removeAllPOIItems();
            mapView.setPOIItemEventListener(null);
            mapView.setCurrentLocationEventListener(null);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mapView.setCurrentLocationRadius(0);
        }
    }


    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
    }
    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
    }
    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
    }

}