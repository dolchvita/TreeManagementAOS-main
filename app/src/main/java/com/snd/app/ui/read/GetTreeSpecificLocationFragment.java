package com.snd.app.ui.read;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.dataUtil.TextManager;
import com.snd.app.data.singleton.MapViewManager;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.data.singleton.SingletonVO;
import com.snd.app.databinding.GetTreeSpecificLocationFrBinding;
import com.snd.app.domain.tree.dto.TreeLocationInfoDTO;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


public class GetTreeSpecificLocationFragment extends TMFragment {
    GetTreeSpecificLocationFrBinding getTreeSpecificLocationFrBinding;
    GetTreeSpecificLocationViewModel getTreeSpecificLocationVM;
    SharedPreferenceManager sharedPreferenceManager;

    // 지도 관련
    private MapViewManager mapViewManager;
    private MapView mapView;
    RelativeLayout SpecificLocationKakaoMap;

    private AlertDialog.Builder alertDialogBuilder;
    MaterialButton bt_location_modify;
    Spinner spinner;

    String idHex;
    String Authorization;
    SingletonVO singletonVO;
    TreeLocationInfoDTO treeLocationInfoData;
    TextManager textManager;
    boolean flag = false;   // 위치수정 여부를 확인하는 변수

    ConstraintLayout get_tree_specific_location_read_form;
    ConstraintLayout get_tree_specific_location_edit_form;
    AppCompatButton treeSpecificLocation_read_cancel;
    ConstraintLayout layout_border;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getTreeSpecificLocationFrBinding = DataBindingUtil.inflate(inflater, R.layout.get_tree_specific_location_fr, container, false);
        getTreeSpecificLocationFrBinding.setLifecycleOwner(this);
        getTreeSpecificLocationVM = new ViewModelProvider(this).get(GetTreeSpecificLocationViewModel.class);
        getTreeSpecificLocationFrBinding.setSpecificLocationVM(getTreeSpecificLocationVM);
        // 참조할 필수 데이터 (인증 토큰, 태그 아이디)
        sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        Authorization = sharedPreferenceManager.getString("Authorization");
        idHex = sharedPreferenceManager.getString("idHex");
        singletonVO = SingletonVO.getInstance();
        textManager = new TextManager();
        treeLocationInfoData = new TreeLocationInfoDTO();

        return getTreeSpecificLocationFrBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bt_location_modify = getTreeSpecificLocationFrBinding.getTreeSpecificLocationModify;
        get_tree_specific_location_read_form = getTreeSpecificLocationFrBinding.getTreeSpecificLocationReadForm;
        get_tree_specific_location_edit_form = getTreeSpecificLocationFrBinding.getTreeSpecificLocationEditForm;
        treeSpecificLocation_read_cancel = getTreeSpecificLocationFrBinding.treeSpecificLocationReadCancel;
        layout_border = getTreeSpecificLocationFrBinding.getSpecificLocationTextForm;

        initTreeIntegratedVO();
        initMapView();
        mappingText();
        initSidewakSpinner();

        // <위치 수정> Button
        getTreeSpecificLocationVM.modifyLocation.observe(getActivity(), geoCoordinate -> {
            flag = !flag;
            bt_location_modify.setBackgroundColor(Color.parseColor((flag) ? "#FFCC00" : "#4DFFFFFF"));
            setMappingPinned(geoCoordinate);
        });


        // 지도모드 변경
        getTreeSpecificLocationVM.mapType.observe(getActivity(), s -> {
            mapViewManager.setMapView(s.equals("Hybrid") ? "Hybrid" : "Normal");
        });


        // 수정모드 전환하기
        getTreeSpecificLocationVM.readOrEdit.observe(getActivity(), aBoolean -> {
            if(aBoolean){
                editMode(get_tree_specific_location_read_form, get_tree_specific_location_edit_form, treeSpecificLocation_read_cancel, layout_border);

            }else {
                if(flag){
                    MapPoint.GeoCoordinate geoCoordinate = mapViewManager.redMarkersPoint.getValue();
                    saveLocation(geoCoordinate.latitude, geoCoordinate.longitude);
                }

                modifyProcess();
            }
        });


        getTreeSpecificLocationVM.cancel.observe(getActivity(), aBoolean -> {
            if (aBoolean){
                readMode(get_tree_specific_location_read_form, get_tree_specific_location_edit_form, treeSpecificLocation_read_cancel, layout_border);
                getTreeSpecificLocationVM.flag = false;
            }else {
                back();
            }
        });


        // 뒤로 가기
        getTreeSpecificLocationVM.back.observe(getViewLifecycleOwner(), back -> {
            back();
        });


        // 수정이 완료되었다면 -> 이미 하나의 반응 객체가 결과 전달해줌
        getTreeSpecificLocationVM.checkModifyLocationInfo().observe(getViewLifecycleOwner(), checkModifyLocationInfo -> {
            String text = (checkModifyLocationInfo.equals("success")) ? "수정되었습니다." : "네트워크가 원활하지 않습니다. 다시 시도해주세요.";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

            getActivity().finish();
        });

    }// ./onViewCreated


   private void initTreeIntegratedVO(){
       singletonVO.treeIntegratedVO.observe(getViewLifecycleOwner(), treeIntegratedVO -> {
           getTreeSpecificLocationVM.Authorization = Authorization;

           log("프레그먼트에서 확인 " + treeIntegratedVO);

           if(treeIntegratedVO != null){
               getTreeSpecificLocationVM.setTextView(treeIntegratedVO);
               mapViewManager.addMarkers(treeIntegratedVO.getLatitude(), treeIntegratedVO.getLongitude(), idHex);
               mapViewManager.getTreeSpecificLocationView(treeIntegratedVO.getLatitude(), treeIntegratedVO.getLongitude());
           }
       });
   }


    void initMapView(){
        mapViewManager = new MapViewManager(getContext());
        mapView = mapViewManager.getMapView();
        SpecificLocationKakaoMap = getTreeSpecificLocationFrBinding.getTreeSpecificLocationKakaoMap;
        SpecificLocationKakaoMap.addView(mapView);
    }


    void initSidewakSpinner(){
        spinner = (Spinner) getView().findViewById(R.id.get_tree_specific_location_tr_state);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.treeStatus_pest,  android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
    }


    public void saveLocation(Double latitude, Double longitude){
        getTreeSpecificLocationVM.treeLocationInfoDTO.setLatitude(latitude);
        getTreeSpecificLocationVM.treeLocationInfoDTO.setLongitude(longitude);
    }


    private void setMappingPinned(MapPoint.GeoCoordinate geoCoordinate){
        if(flag){
            mapViewManager.addMarkers2(geoCoordinate.latitude, geoCoordinate.longitude);    // RedPin
        }else {
            mapViewManager.removeCurrentMarkers();
        }
    }


    /* ------------------------------------ 입력값 받기 ------------------------------------ */

    private void mappingText(){
        if(getView()!=null){
            EditText carriagewayEditText = getView().findViewById(R.id.get_tree_specific_location_carriageway);
            EditText distanceEditText = getView().findViewById(R.id.get_tree_specific_location_distance);
            carriagewayEditText.addTextChangedListener(TextManager.createTextWatcher((text) -> {
                if (!text.isEmpty() && isInteger(text)) {
                    getTreeSpecificLocationVM.treeLocationInfoDTO.setCarriageway(Integer.parseInt(text));
                }
            }));

            distanceEditText.addTextChangedListener(TextManager.createTextWatcher((text) -> {
                if (!text.isEmpty() && isInteger(text)) {
                    if (text.length() > 3){
                        Toast.makeText(getContext(), "m 단위를 확인해주세요", Toast.LENGTH_SHORT).show();
                    }else {
                        getTreeSpecificLocationVM.treeLocationInfoDTO.setDistance(Integer.parseInt(text));
                    }
                }
            }));
        }
    }


    // 정수인지 판별
    public boolean isInteger(String input) {
        return input.matches("-?\\d+"); // 정수 판별: 부호 선택적, 숫자 1개 이상
    }


    private void modifyProcess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("수정하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                getTreeSpecificLocationVM.modifyLocationInfo();
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
        getTreeSpecificLocationFrBinding = null;
        sharedPreferenceManager = null;
        if(mapViewManager != null){
            mapViewManager.cleanUp();
        }
        mapViewManager = null;
        mapView = null;
        SpecificLocationKakaoMap = null;
        alertDialogBuilder = null;
        bt_location_modify = null;
        spinner = null;
        treeLocationInfoData = null;
        textManager = null;
        get_tree_specific_location_read_form = null;
        get_tree_specific_location_edit_form = null;
        treeSpecificLocation_read_cancel = null;
        layout_border = null;
    }


}
