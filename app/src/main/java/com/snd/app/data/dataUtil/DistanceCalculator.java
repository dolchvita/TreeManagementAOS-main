package com.snd.app.data.dataUtil;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DistanceCalculator {
    public String TAG = this.getClass().getName();
    public MutableLiveData<List<MapPOIItem>> currentMarkers = new MutableLiveData<>();     // 여기서 50개만 지정이 됨.
    public MutableLiveData<List<MapPOIItem>> sortedMarkers = new MutableLiveData<>();


    public double haversineDistance(MapPoint point1, MapPoint point2) {
        double R = 6371; // 지구의 반지름 (킬로미터)
        double distance = 0;

        if(point1 != null && point2 != null){
            double dLat = Math.toRadians(point2.getMapPointGeoCoord().latitude - point1.getMapPointGeoCoord().latitude);
            double dLon = Math.toRadians(point2.getMapPointGeoCoord().longitude - point1.getMapPointGeoCoord().longitude);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(point1.getMapPointGeoCoord().latitude)) * Math.cos(Math.toRadians(point2.getMapPointGeoCoord().latitude)) *
                            Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            distance = R * c;
        }
        return distance; // 킬로미터 단위
    }



    // 2-3) 지정된 범위 내에 있는 마커 찾기
    public LiveData<List<MapPOIItem>> findMarkersInCircle(MapPOIItem[] markers, MapPoint circleCenter, double radius) {
        List<MapPOIItem> markersToContain = new ArrayList<>();

        for (MapPOIItem marker : markers) {
            if (marker!=null && markersToContain.size() >= 50) { // 리스트의 크기가 100 이상이면 루프를 종료
                break;
            }

            MapPoint point = marker.getMapPoint();
            Log.d(TAG, "맵 포인트 : " + point);
            Log.d(TAG, "중심점 : " + circleCenter);       // null
            Log.d(TAG, "반지름 : " + radius);

            if (haversineDistance(point, circleCenter) <= radius) {
                markersToContain.add(marker);
            }
        }
        Log.d(TAG, "결과 : " + markersToContain.size());
        currentMarkers.setValue(markersToContain);
        return currentMarkers;
    }


    // 중심점을 기준으로 가장 가까운 순서대로 마커 정렬
    public void findMarkersSortedByDistance(MapPOIItem[] markers, MapPoint circleCenter) {
        List<MapPOIItemDistancePair> distancePairs = new ArrayList<>();
        Log.d(TAG, "DistanceCalculator 에서 전달 " + markers);

        for (MapPOIItem marker : markers) {
            double distance = haversineDistance(marker.getMapPoint(), circleCenter);
            distancePairs.add(new MapPOIItemDistancePair(marker, distance));
        }

        Collections.sort(distancePairs, (a, b) -> Double.compare(a.distance, b.distance));

        List<MapPOIItem> sortedData = new ArrayList<>();
        for (MapPOIItemDistancePair pair : distancePairs) {
            Log.d(TAG, "추가되는 데이터 " + pair);
            sortedData.add(pair.marker);
        }

        sortedMarkers.setValue(sortedData);
    }


    private static class MapPOIItemDistancePair {
        MapPOIItem marker;
        double distance;

        MapPOIItemDistancePair(MapPOIItem marker, double distance) {
            this.marker = marker;
            this.distance = distance;
        }
    }



}
