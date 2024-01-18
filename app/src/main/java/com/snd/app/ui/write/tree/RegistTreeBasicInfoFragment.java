package com.snd.app.ui.write.tree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.flexbox.FlexboxLayout;
import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.camera.CameraPreviewDialogFragment;
import com.snd.app.databinding.RegistTreeBasicInfoFrBinding;
import com.snd.app.domain.tree.dto.TreeInitializingDTO;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.adapter.PhotoAdapter;
import com.snd.app.data.dataUtil.SpaceItemDecoration;
import com.snd.app.repository.tree.treeDataList.TreeDataListRepository;
import com.snd.app.ui.write.RegistTreeInfoActivity;
import com.snd.app.adapter.TreeHashtagCustomAdapter;
import com.snd.app.ui.write.hashtag.TreeHashtagDialogFragment;


import java.util.ArrayList;
import java.util.List;


public class RegistTreeBasicInfoFragment extends TMFragment implements TreeHashtagDialogFragment.TreeHashtagDialogListener {
    RegistTreeBasicInfoFrBinding treeBasicInfoActBinding;
    RegistTreeBasicInfoViewModel treeBasicInfoVM;
    private TreeInitializingDTO treeInitializingDTO;
    /* 앱 내에서 한번만 생성되어 사용되는 리소스들 */
    private AlertDialog.Builder alertDialogBuilder;
    AlertDialog dialog;
    TreeDataListRepository treeDataListRepository;
    SharedPreferences sharedPreferences;
    SharedPreferenceManager sharedPreferencesManager;
    String idHex;
    String token;
    private Spinner spinner;
    AppCompatEditText editText;

    /* 카메라 관련 */
    private RecyclerView recyclerView;
    public PhotoAdapter photoAdapter;
    CameraPreviewDialogFragment cameraPreviewDialogFragment;
    private static final int REQUEST_GALLERY = 102;

    /* 해시태그 관련 */
    List<String> textList = new ArrayList<>();
    TreeHashtagCustomAdapter treeHashtagCustomAdapter;
    FlexboxLayout flexboxLayout;
    LayoutInflater inflater;
    int position = 0;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        treeBasicInfoActBinding = DataBindingUtil.inflate(inflater, R.layout.regist_tree_basic_info_fr, container, false);
        treeBasicInfoActBinding.setLifecycleOwner(this);
        treeBasicInfoVM = new ViewModelProvider(getActivity()).get(RegistTreeBasicInfoViewModel.class);
        treeBasicInfoActBinding.setTreeBasicInfoVM(treeBasicInfoVM);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        idHex = sharedPreferencesManager.getString("idHex");
        token = sharedPreferencesManager.getString("token");
        treeBasicInfoVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        /* 사진 관련 RecyclerView */
        recyclerView = treeBasicInfoActBinding.registTreeInfoRvImage;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new SpaceItemDecoration(20));
        /* 해시태그 관련 */
        flexboxLayout = treeBasicInfoActBinding.tlqk;
        treeHashtagCustomAdapter = new TreeHashtagCustomAdapter();



        /* -------------------------------------- CAMERA ------------------------------------------ */

        // 사진 수 나타내는 메서드
        TextView textView = treeBasicInfoActBinding.imageCnt;
        treeBasicInfoVM.countText.observe(getActivity(), integer -> {
            String count = getString(R.string.image_count, integer.toString());
            textView.setText(count);
        });

        treeBasicInfoVM.isImage.observe(getActivity(), s -> {
            checkPhoto();
        });

        photoAdapter();

        return treeBasicInfoActBinding.getRoot();
    }// ./onCreate


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editText = view.findViewById(R.id.tr_name);
        editText.setVisibility(View.GONE);


        try {
            setTreeBasicInfoDTO();
        } catch (JsonProcessingException e) {

            throw new RuntimeException(e);
        }

        treeBasicInfoVM.Authorization.observe(getActivity(), s -> {
            treeBasicInfoVM.setSpiciesSpinner();
        });

        initBasicSpinner();

        treeBasicInfoVM.throwFailCheck().observe(getActivity(), s -> {
            failProcess();
        });

        treeBasicInfoVM.back.observe(getActivity(), o -> {
            finishProccess();
        });

        treeBasicInfoVM.insertProcess.observe(getActivity(), s -> {
            if(s.equals("none")){
                Toast.makeText(getContext(), "수목명을 입력해주세요", Toast.LENGTH_SHORT).show();
            }else {
                insertProcess();
            }
        });

        treeBasicInfoVM.checkTreeBasicInfo().observe(getActivity(), s -> {
            if(s.equals("success")){
                Toast.makeText(getContext(), "수목 기본 정보가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                ((RegistTreeInfoActivity)getActivity()).num = 1;
                ((RegistTreeInfoActivity)getActivity()).switchFragment();
            }else {
                Toast.makeText(getContext(), "중복된 칩 이거나,\n 허용되지 않은 칩입니다.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });


        /* -------------------------------------- HASHTAG ------------------------------------------ */

        treeBasicInfoVM.onHashtagEvent.observe(getActivity(), s -> {
            open();
        });

        treeHashtagCustomAdapter.removeItem.observe(getActivity(), integer -> {
            flexboxLayout.removeView(flexboxLayout.getChildAt(integer));
            textList.remove(textList.get(integer));
        });

    }   /* ./onViewCreated */


    private String setTextToHashtag(List<String> list){
        StringBuilder sb = new StringBuilder();
        for(String text : list){
            sb.append("#").append(text);
        }
        return sb.toString();
    }


    @Override
    public void onDialogPositiveClick(String hashtag, String method) {
        textList.add(hashtag);
        treeHashtagCustomAdapter.onCreateViewHolder(inflater, flexboxLayout, position);   // 초기화를 여기서 하면 오류 안나나?
        treeHashtagCustomAdapter.bind(hashtag);
        int size = treeHashtagCustomAdapter.getItemCount();
        position = size - 1;
    }


    private void open(){
        TreeHashtagDialogFragment treeHashtagDialogFragment = new TreeHashtagDialogFragment();
        treeHashtagDialogFragment.process = "append";
        treeHashtagDialogFragment.setTreeHashtagDialogListener(this);   // 리스너 구현
        treeHashtagDialogFragment.show(getActivity().getSupportFragmentManager(), "TreeHashtagDialogFragment");
    }


    /* -------------------------------------- PHOTO ------------------------------------------ */

    private void photoAdapter(){
        photoAdapter = new PhotoAdapter();
        recyclerView.setAdapter(photoAdapter);

        // Camera (실행) Button
        treeBasicInfoVM.camera.observe(getActivity(), o -> {
            if (photoAdapter != null && photoAdapter.getImageListItemCount() > 1){
                Toast.makeText(getContext(), "이미지는 최대 2장까지 가능합니다.", Toast.LENGTH_SHORT).show();
            }else {
                cameraPreviewDialogFragment = new CameraPreviewDialogFragment();
                cameraPreviewDialogFragment.show(getActivity().getSupportFragmentManager(), "inputDialog");

                if(cameraPreviewDialogFragment != null){
                    cameraPreviewDialogFragment.saveFile.observe(getActivity(), file -> {
                        // File 객체가 넘어올 예정
                        treeBasicInfoVM.addImageList2(file);

                        /*
                        treeBasicInfoVM.currentFileList.add(file);

                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        treeBasicInfoVM.addImageList(bitmap);
                         */
                    });
                }
            }
        })/* ./Camera */;


        /* photoAdapter에서 Bitmap 객체들을 받아서 화면에 렌더링
        * --> 어댑터에서 코드를 변경하면, 비트맵 처리를 안 해도 가능하지 않을까? */
        treeBasicInfoVM.currentFileList.observe(getActivity(), files -> {
            photoAdapter.setImageList2(files);
        });



        // 갤러리 추가 버튼
        photoAdapter.bt_add.observe(getActivity(), s -> {
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            getActivity().startActivityForResult(intent, REQUEST_GALLERY);
        });


        // 사진 지우는 메서드
        photoAdapter.removeImage.observe(getActivity(), integer -> {
            // 지워진 결과가 반환되는데? - 왜 하나일까
            // 요게 문제구먼, 하나밖에 안 들어있음, 렌더링 속도가 느린 건가?
            log("기본 프레그먼트에서 확인 1 ** " + treeBasicInfoVM._currentFileList);
            //log("기본 프레그먼트에서 확인 2 ** " + treeBasicInfoVM.currentFileList.getValue());
            //log("기본 프레그먼트에서 확인 3 ** " + treeBasicInfoVM._currentFileList.get(integer));

            //treeBasicInfoVM._currentFileList.remove(treeBasicInfoVM._currentFileList.get(integer));   // 해당 지우기
            //treeBasicInfoVM.countText.setValue(treeBasicInfoVM._currentFileList.size());
        });

    }   /* ./photoAdapter */


    /* --------------------------------------------- SPINNER --------------------------------------------- */

    // 스피너 설정
    private void initBasicSpinner(){
        spinner = getView().findViewById(R.id.basic_tr_state);
        treeBasicInfoVM.getSpiciesSpinner().observe(getViewLifecycleOwner(), strings -> {
            updateSpinner(strings);
        });
        treeBasicInfoVM.showEditText.observe(getActivity(), show -> {
            if(editText != null){
                editText.setVisibility(show ? View.VISIBLE : View.GONE);
            }
            inputSpecies();
        });
    }


    // 리스트 데이터를 받아서 스피너에 연결하는 메서드
    private void updateSpinner(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    public void inputSpecies(){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {//
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString() == null || s.toString().equals("")){
                    Toast.makeText(getContext(), "수목명을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                treeInitializingDTO.setSpecies(s.toString());
            }
        });
    }


    public void setTreeBasicInfoDTO() throws JsonProcessingException {
        treeInitializingDTO = new TreeInitializingDTO();
        treeInitializingDTO.setNfc(idHex);
        treeInitializingDTO.setSubmitter(sharedPreferencesManager.getString("id"));
        treeInitializingDTO.setVendor(sharedPreferencesManager.getString("company"));
        // 위치 정보
        String latitude = sharedPreferencesManager.getString("latitude");
        String longitude = sharedPreferencesManager.getString("longitude");

        if(latitude != null && longitude != null){
            treeInitializingDTO.setLatitude(Double.parseDouble(latitude));
            treeInitializingDTO.setLongitude(Double.parseDouble(longitude));
        }
        treeBasicInfoVM.setTextViewModel(treeInitializingDTO);
    }

    /* --------------------------------------------- INSERT --------------------------------------------- */

    void insertProcess(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("수목 기본 정보를 등록하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                treeInitializingDTO.setHashtag(setTextToHashtag(textList));
                treeBasicInfoVM.registerTreeImage();
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = alertDialogBuilder.create();
        dialog.show();
    }


    private void checkPhoto(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("사진이 추가되지 않았습니다. 계속 진행하시겠습니까?");
        alertDialogBuilder.setMessage("사진 등록시 현재 페이지에서만 추가할 수 있습니다.");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getView().findViewById(R.id.treeBasic_write_cancel).setEnabled(false);
                treeInitializingDTO.setHashtag(setTextToHashtag(textList));
                treeBasicInfoVM.initializeTreeBasicInfoForRegister();
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = alertDialogBuilder.create();
        dialog.show();
    }


    @Override
    public void onDestroyView() {
        log("** 기본 정보 - onDestroyView **");
        super.onDestroyView();
        treeBasicInfoActBinding = null;
        alertDialogBuilder = null;
        if(dialog != null){
            dialog.dismiss();
        }
        treeDataListRepository = null;
        treeInitializingDTO = null;
        sharedPreferences = null;
        sharedPreferencesManager = null;
        spinner = null;
        editText = null;
        recyclerView = null;
        photoAdapter = null;
        if(cameraPreviewDialogFragment != null){
            cameraPreviewDialogFragment.dismiss();
        }

        cameraPreviewDialogFragment = null;
        treeHashtagCustomAdapter = null;
        flexboxLayout = null;
        inflater = null;
    }


}