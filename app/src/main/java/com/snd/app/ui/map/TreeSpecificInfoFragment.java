package com.snd.app.ui.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.snd.app.R;
import com.snd.app.adapter.PhotoUrlAdapter;
import com.snd.app.adapter.TreeHashtagCustomAdapter;
import com.snd.app.common.TMDialogFragment;
import com.snd.app.data.dataUtil.SpaceItemDecoration;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.TreeSpecificInfoFrBinding;

import java.util.ArrayList;
import java.util.List;


public class TreeSpecificInfoFragment extends TMDialogFragment {
    private final String url = "https://forestpark.yeoju.go.kr/images/";
    TreeSpecificInfoFrBinding treeSpecificInfoFrBinding;
    TreeSpecificInfoViewModel treeSpecificInfoVM;
    private RecyclerView recyclerView;
    TextView textView;
    private PhotoUrlAdapter photoAdapter;
    private SharedPreferenceManager sharedPreferencesManager;
    private String idHex;
    private String Authorization;
    /* 해시태그 관련 */
    TreeHashtagCustomAdapter treeHashtagCustomAdapter;
    List<String> hashtagTextList = new ArrayList<>();
    FlexboxLayout flexboxLayout;
    LayoutInflater inflater;
    AppCompatTextView tr_hashtag_tv;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        treeSpecificInfoFrBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.tree_specific_info_fr, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(treeSpecificInfoFrBinding.getRoot());
        treeSpecificInfoVM = new ViewModelProvider(this).get(TreeSpecificInfoViewModel.class);
        treeSpecificInfoFrBinding.setTreeSpecificInfoVM(treeSpecificInfoVM);
        treeSpecificInfoFrBinding.setLifecycleOwner(this);
        /* 필수 리소스 */
        sharedPreferencesManager = SharedPreferenceManager.getInstance(getContext().getApplicationContext());
        Authorization = sharedPreferencesManager.getString("token");
        treeSpecificInfoVM.Authorization.setValue(Authorization);
        treeSpecificInfoVM.idHex.setValue(idHex);
        idHex = getTag();
        /* 사진 설정 */
        treeSpecificInfoVM.loadTreeImageFilenameList(Authorization, idHex);
        recyclerView = treeSpecificInfoFrBinding.treeSpecificInfoRvImage;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new SpaceItemDecoration(20));
        photoAdapter = new PhotoUrlAdapter();
        recyclerView.setAdapter(photoAdapter);
        textView = treeSpecificInfoFrBinding.noImageText;
        treeSpecificInfoVM.loadTreeInfoByNFCtadId(Authorization, idHex);


        /* 사진 조회 */
        treeSpecificInfoVM.getTreeIntegratedVO().observe(this, treeIntegratedVO -> {
            treeIntegratedVO.setNfc(idHex);
            treeSpecificInfoVM.setTextViewModel(treeIntegratedVO);

            /* 해시태그 조회 */
            if(treeIntegratedVO.getHashtag() != null){
                hashtagTextList = splitStringOfHashtag(treeIntegratedVO.getHashtag());
                initFlexlayoutViews();
            }
        });


        treeSpecificInfoVM.getTreeImageFilenameList().observe(getActivity(), strings -> {
            if(strings.size() > 0){
                getTreeImageFileName(strings);

            }else {
                textView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });


        treeSpecificInfoVM.back.observe(getActivity(), s -> {
            dismiss();
        });


        return builder.create();
    }   /* ./onCreateDialog */


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* 해시태그 관련 */
        treeHashtagCustomAdapter = new TreeHashtagCustomAdapter();
        flexboxLayout = treeSpecificInfoFrBinding.flexboxLayout;
        this.inflater = inflater;
        tr_hashtag_tv = treeSpecificInfoFrBinding.trHashtagTv;

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void initFlexlayoutViews(){
        tr_hashtag_tv.setVisibility(View.VISIBLE);  // 비고칸 보이게
        flexboxLayout.removeAllViews();
        for(int i = 0; i < hashtagTextList.size(); i++){
            treeHashtagCustomAdapter.onCreateViewHolder(inflater, flexboxLayout, i);
            treeHashtagCustomAdapter.setItemBind(hashtagTextList.get(i));
        }
        treeHashtagCustomAdapter.setHashtagList(hashtagTextList);
    }


    // 조회 기능
    public void getTreeImageFileName(List<String> filenameList){
        List<String> photoUrls = new ArrayList<>();
        for(String filename : filenameList){
            String photo = url + filename;
            if(photo != null){
                photoUrls.add(photo);
            }
        }
        photoAdapter.setImageList(photoUrls);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        treeSpecificInfoFrBinding = null;
        sharedPreferencesManager = null;
        treeHashtagCustomAdapter = null;
        recyclerView = null;
        photoAdapter = null;
        flexboxLayout = null;
        tr_hashtag_tv = null;
        textView = null;
        inflater = null;
    }


}
