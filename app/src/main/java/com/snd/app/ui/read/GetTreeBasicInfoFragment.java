package com.snd.app.ui.read;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.snd.app.R;
import com.snd.app.common.TMFragment;

import com.snd.app.adapter.PhotoUrlAdapter;
import com.snd.app.data.camera.CameraPreviewDialogFragment;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.GetTreeBasicInfoFrBinding;
import com.snd.app.data.dataUtil.SpaceItemDecoration;
import com.snd.app.domain.tree.dto.TreeHashtagModifyDTO;
import com.snd.app.adapter.TreeHashtagCustomAdapter;
import com.snd.app.ui.write.hashtag.TreeHashtagDialogFragment;

import java.util.ArrayList;
import java.util.List;


public class GetTreeBasicInfoFragment extends TMFragment implements TreeHashtagDialogFragment.TreeHashtagDialogListener{
    private final String url = "http://125.143.174.65:9955/images/";
    private static final int REQUEST_GALLERY = 102;
    CameraPreviewDialogFragment cameraPreviewDialogFragment;
    GetTreeBasicInfoFrBinding getTreeBasicInfoFrBinding;
    GetTreeBasicInfoViewModel getTreeBasicInfoVM;
    private AlertDialog.Builder alertDialogBuilder;
    SharedPreferenceManager spm;
    private RecyclerView recyclerView;
    PhotoUrlAdapter photoUrlAdapter;
    AppCompatEditText editText;
    private Spinner spinner;
    public String idHex;
    String Authorization;
    /* 이미지 지우기용 */
    private List<String> filenameList = new ArrayList<>();
    /* 해시태그 관련 */
    TreeHashtagCustomAdapter treeHashtagCustomAdapter;
    List<String> hashtagTextList = new ArrayList<>();
    private TreeHashtagModifyDTO hashtagModifyDTO;
    FlexboxLayout edit_get_flexbox_layout;
    FlexboxLayout read_get_flexbox_layout;
    LayoutInflater inflater;
    int index;

    ConstraintLayout get_tree_basic_read_form;
    ConstraintLayout get_tree_basic_edit_form;
    AppCompatButton treeBasic_read_cancel;
    ConstraintLayout layout_border;
    Boolean flag = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getTreeBasicInfoFrBinding = DataBindingUtil.inflate(inflater, R.layout.get_tree_basic_info_fr, container, false);
        getTreeBasicInfoFrBinding.setLifecycleOwner(this);
        getTreeBasicInfoVM = new ViewModelProvider(getActivity()).get(GetTreeBasicInfoViewModel.class);
        getTreeBasicInfoFrBinding.setVm(getTreeBasicInfoVM);
        // 참조할 필수 데이터 (인증 토큰, 태그 아이디)
        spm = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        Authorization = spm.getString("token");
        idHex = spm.getString("idHex");
        getTreeBasicInfoVM.loadTreeIntegratedVO(Authorization, idHex);
        getTreeBasicInfoVM.setSpiciesSpinner();
        /* 사진 설정 */
        getTreeBasicInfoVM.loadTreeImageFilenameList(Authorization, idHex);
        recyclerView = getTreeBasicInfoFrBinding.getTreeInfoRvImage;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new SpaceItemDecoration(20));
        /* 해시태그 관련 */
        treeHashtagCustomAdapter = new TreeHashtagCustomAdapter();

        this.inflater = inflater;
        hashtagModifyDTO = new TreeHashtagModifyDTO();
        hashtagModifyDTO.setNfc(idHex);

        return getTreeBasicInfoFrBinding.getRoot();
    }   /* ./onCreate */


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        get_tree_basic_read_form = getTreeBasicInfoFrBinding.getTreeBasicReadForm;
        get_tree_basic_edit_form = getTreeBasicInfoFrBinding.getTreeBasicEditForm;
        /* 해시태그 폼 */
        edit_get_flexbox_layout = getTreeBasicInfoFrBinding.flexboxLayout;
        read_get_flexbox_layout = getTreeBasicInfoFrBinding.readFlexboxLayout;
        treeBasic_read_cancel = getTreeBasicInfoFrBinding.treeBasicReadCancel;
        layout_border = getTreeBasicInfoFrBinding.layoutBorder;

        editText = view.findViewById(R.id.scarlet_diam);
        initSpeciesSpinner();

        getTreeBasicInfoVM.failCheck().observe(getActivity(), s -> {
            Toast.makeText(getContext(), "등록되지 않았거나, \n 허용된 칩이 아닙니다. ", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });


        getTreeBasicInfoVM.getTreeIntegratedVO().observe(getActivity(), treeIntegratedVO -> {
            treeIntegratedVO.setNfc(idHex);
            getTreeBasicInfoVM.setTextViewModel(treeIntegratedVO);

            /* HashTag */
            if(treeIntegratedVO.getHashtag() != null){
                hashtagTextList = splitStringOfHashtag(treeIntegratedVO.getHashtag());

                initFlexlayoutViews(read_get_flexbox_layout);
            }
        });


        PhotoUrlAdapter();


        // Camera Button
        getTreeBasicInfoVM.camera.observe(getActivity(), s -> {
            if(s.equals("camera")){
                if (photoUrlAdapter != null && photoUrlAdapter.getImageListItemCount() > 1){
                    Toast.makeText(getContext(), "이미지는 최대 2장까지 가능합니다.", Toast.LENGTH_SHORT).show();
                }else {
                    cameraPreviewDialogFragment = new CameraPreviewDialogFragment();
                    cameraPreviewDialogFragment.show(getActivity().getSupportFragmentManager(), "inputDialog");
                    if(cameraPreviewDialogFragment != null){
                        cameraPreviewDialogFragment.saveFile.observe(getActivity(), file -> {
                            getTreeBasicInfoVM.addImageList(file);
                        });
                    }
                }
            }else {
                showToast("사진을 추가하시려면 수정하기 버튼을 먼저 눌러주세요.");
            }
        }); /* ./camera */


        // Modify Mode
        getTreeBasicInfoVM.readOrEdit.observe(getActivity(), aBoolean -> {
            flag = aBoolean;
            if(aBoolean){
                editMode(get_tree_basic_read_form, get_tree_basic_edit_form, treeBasic_read_cancel, layout_border);
                getTreeBasicInfoVM.flag = true;

                initFlexlayoutViews(edit_get_flexbox_layout);

            }else {
                checkModify();
            }
        });


        // 취소
        getTreeBasicInfoVM.cancel.observe(getActivity(), aBoolean -> {
            if(!aBoolean){
                back();
            }else {
                readMode(get_tree_basic_read_form, get_tree_basic_edit_form, treeBasic_read_cancel, layout_border);
                getTreeBasicInfoVM.flag = false;
            }
        });


        modifyResult();


        getTreeBasicInfoVM.getTreeImageFilenameList().observe(getActivity(), strings -> {
            filenameList = strings;
            getTreeBasicInfoVM.initFileNameListSize = strings.size();
            getTreeBasicInfoVM.imageProcess.setValue(strings.size());
            if(strings.size() > 0){
                getTreeImageFileName(strings);
                getTreeBasicInfoVM.fileNameList = strings;
            }
        });


        /* ----------------------------------------- HASHTAG --------------------------------------------- */

        getTreeBasicInfoVM.onHashtagEvent.observe(getActivity(), s -> {
            open();
        });


        // 수정 창 열기
        treeHashtagCustomAdapter.modifyPeocess.observe(getActivity(), integer -> {
            index = integer;
            String item = hashtagTextList.get(integer);
            hashtagModifyDTO.setHashtagOriginalValue(item);
            open(item);
        });


        // 해시태그 지우기
        treeHashtagCustomAdapter.removeItem.observe(getActivity(), integer -> {
            removeHashtag(integer, (flag) ? edit_get_flexbox_layout : read_get_flexbox_layout);
        });


        // 해시태그 수정 결과
        getTreeBasicInfoVM.checkTreeHashtag().observe(getActivity(), s -> {
            alertHashtagResultMessage(s);
        });


        treeHashtagCustomAdapter.error.observe(getActivity(), s -> {
            alertHashtagResultMessage(s);
        });

    }   /* ./onViewCreated */


    public void initFlexlayoutViews(FlexboxLayout flexboxLayout){
        // read 모드와 edit 모드 분리하기
        flexboxLayout.removeAllViews();

        for(int i = 0; i < hashtagTextList.size(); i++){
            treeHashtagCustomAdapter.onCreateViewHolder(inflater, flexboxLayout, i);
            treeHashtagCustomAdapter.setItemBind(hashtagTextList.get(i));
        }
        treeHashtagCustomAdapter.setHashtagList(hashtagTextList);
    }


    @Override
    public void onDialogPositiveClick(String hashtag, String method) {
        if(method.equals("append")){
            treeHashtagCustomAdapter.addText(hashtag);
            hashtagModifyDTO.setHashtagAppendValue(hashtag);
            getTreeBasicInfoVM.appendTreeHashtag(hashtagModifyDTO);

        } else {
            hashtagTextList.set(index, hashtag);
            treeHashtagCustomAdapter.setHashtagList(hashtagTextList);
            hashtagModifyDTO.setHashtagUpdateValue(hashtag);
            getTreeBasicInfoVM.updateTreeHashtag(hashtagModifyDTO);
        }

        //화면 새로고침
        initFlexlayoutViews((flag) ? edit_get_flexbox_layout : read_get_flexbox_layout);
    }


    public void removeHashtag(int integer, FlexboxLayout flexboxLayout){
        String item = hashtagTextList.get(integer);
        // 뷰 지우기
        flexboxLayout.removeView(flexboxLayout.getChildAt(integer));
        hashtagTextList.remove(item);

        // 데이터 지우기 (서버 통신)
        hashtagModifyDTO.setHashtagOriginalValue(item);
        getTreeBasicInfoVM.deleteTreeHashtag(hashtagModifyDTO);

        // 재배열
        initFlexlayoutViews(flexboxLayout);
    }


    public List<String> splitStringOfHashtag(String str){
        List<String> list = new ArrayList<>();
        String[] splitText = str.split("#");
        for(String text: splitText){
            if (!text.isEmpty()) {
                list.add(text);
            }
        }
        return list;
    }


    /* ----------------------------------------- IMAGE --------------------------------------------- */

    private void PhotoUrlAdapter(){
        photoUrlAdapter = new PhotoUrlAdapter();
        recyclerView.setAdapter(photoUrlAdapter);

        photoUrlAdapter.editPhoto.observe(getActivity(), aBoolean -> {
            getTreeBasicInfoVM.editPhoto = aBoolean;
        });


        photoUrlAdapter.removeImage.observe(getActivity(), integer -> {
            String obj = getTreeBasicInfoVM.fileNameList.get(integer);
            getTreeBasicInfoVM.fileNameList.remove(obj);
        });


        photoUrlAdapter.bt_add.observe(getActivity(), s -> {
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            getActivity().startActivityForResult(intent, REQUEST_GALLERY );
        });


        // 사진을 추가했을 경우
        getTreeBasicInfoVM.addFile.observe(getActivity(), file -> {
            photoUrlAdapter.addImageList(file);
            filenameList.add(file.getName());
        });

    }   /* ./PhotoUrlAdapter */


    /* ----------------------------------------- SPINNER ----------------------------------------- */

    private void initSpeciesSpinner(){
        spinner = getView().findViewById(R.id.readBasic_tr_state);
        getTreeBasicInfoVM.getSpiciesSpinner().observe(getViewLifecycleOwner(), this::updateSpinner);
        getTreeBasicInfoVM.showEditText.observe(getActivity(), show -> {
            editText.setVisibility(show ? View.VISIBLE : View.GONE);
            inputSpecies();
        });
    }


    private void updateSpinner(List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    public void inputSpecies(){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString() == null || s.toString().equals("")){
                    Toast.makeText(getContext(), "수목명을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                getTreeBasicInfoVM.inputSpecies(s.toString());
            }
        });
    }


    public void getTreeImageFileName(List<String> filenameList){
        List<String> photoUrls = new ArrayList<>();
        for(String filename : filenameList){
            getTreeBasicInfoVM.num = extractImageSequence(filename);
            String file = url + filename;
            photoUrls.add(file);
        }
        if(photoUrlAdapter != null){
            photoUrlAdapter.setImageList(photoUrls);
        }
    }


    // 사진의 이름을 조사하여 자리 선정하기 (수정시 적용) #1
    private Integer extractImageSequence(String imageUrl) {
        if (imageUrl.contains("_1")) {
            return 2;
        } else if (imageUrl.contains("_2")) {
            return 1;
        }
        return 0;
    }


    private void checkModify(){
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("수정하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getTreeBasicInfoVM.checkDeleteProcess(filenameList);
                getTreeBasicInfoVM.modifyTreeBasicInfo();
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


    /* --------------------------------------------- RESULT --------------------------------------------- */

    // 수정시 적용될 수 있는 등록, 업데이트, 삭제 등을 트랜잭션으로 결과를 반환
    private void modifyResult() {
        MutableLiveData<Boolean> isBasicInfoModified = new MutableLiveData<>(true);
        MutableLiveData<Boolean> isImageModified = new MutableLiveData<>(true);
        MutableLiveData<Boolean> isImageDeleted = new MutableLiveData<>(true);

        getTreeBasicInfoVM.checkModifyBasicInfo().observe(getActivity(), s -> {
            isBasicInfoModified.setValue(s.equals("success"));
            checkTransactionCompletion(isBasicInfoModified, isImageModified, isImageDeleted);
        });

        getTreeBasicInfoVM.modifyResult().observe(getActivity(), s -> {
            isImageModified.setValue(s.equals("success"));
            checkTransactionCompletion(isBasicInfoModified, isImageModified, isImageDeleted);
        });

        getTreeBasicInfoVM.deleteResult().observe(getActivity(), s -> {
            isImageDeleted.setValue(s.equals("success"));
            checkTransactionCompletion(isBasicInfoModified, isImageModified, isImageDeleted);
        });
    }


    private void checkTransactionCompletion(LiveData<Boolean> isBasicInfoModified, LiveData<Boolean> isImageModified, LiveData<Boolean> isImageDeleted) {
        if (isBasicInfoModified.getValue() && isImageModified.getValue() && isImageDeleted.getValue()) {
            showToast("수정되었습니다.");
        } else if (isBasicInfoModified.getValue() != null && isImageModified.getValue() != null && isImageDeleted.getValue() != null) {
            showToast("네트워크가 원활하지 않습니다. 다시 시도해주세요.");
        }
        getActivity().finish();
    }


    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void open(){
        TreeHashtagDialogFragment treeHashtagDialogFragment = new TreeHashtagDialogFragment();
        treeHashtagDialogFragment.process = "append";
        treeHashtagDialogFragment.setTreeHashtagDialogListener(this);   // 리스너 구현
        treeHashtagDialogFragment.show(getActivity().getSupportFragmentManager(), "TreeHashtagDialogFragment");
    }


    private void open(String text){
        TreeHashtagDialogFragment treeHashtagDialogFragment = new TreeHashtagDialogFragment();
        treeHashtagDialogFragment.process = "update";
        treeHashtagDialogFragment.text = text;
        treeHashtagDialogFragment.setTreeHashtagDialogListener(this);   // 리스너 구현
        treeHashtagDialogFragment.show(getActivity().getSupportFragmentManager(), "TreeHashtagDialogFragment");
    }


    public void alertHashtagResultMessage(String s){
        String msg = null;
        if(s.equals("appendsuccess")){
            msg = "성공적으로 추가되었습니다.";

        } else if (s.equals("deletesuccess")) {
            msg = "성공적으로 삭제되었습니다.";

        }else if (s.equals("updatesuccess")) {
            msg = "성공적으로 수정되었습니다.";

        } else {
            msg = "네트워크가 원활하지 않습니다. 다시 시도해주세요.";
        }
        showToast(msg);

        // 새로고침
        initFlexlayoutViews((flag) ? edit_get_flexbox_layout : read_get_flexbox_layout);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getTreeBasicInfoFrBinding = null;
        alertDialogBuilder = null;
        photoUrlAdapter = null;
        recyclerView = null;
        editText = null;
        spinner = null;
        spm = null;
        if(treeHashtagCustomAdapter != null){
            treeHashtagCustomAdapter.removeResources();
        }
        treeHashtagCustomAdapter = null;
        edit_get_flexbox_layout = null;
        read_get_flexbox_layout = null;
        inflater = null;
        get_tree_basic_read_form = null;
        get_tree_basic_edit_form = null;
        treeBasic_read_cancel = null;
        layout_border = null;
    }


}