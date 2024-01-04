package com.snd.app.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.snd.app.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/* < GetTreeBasicInfoFragment && TreeSpecificInfoFragment >에서 사진 데이터 조회 및 수정할 때 사용될 어댑터 */
public class PhotoUrlAdapter extends RecyclerView.Adapter<PhotoUrlAdapter.PhotoViewHolder> {
    protected String TAG = this.getClass().getName();
    private List<String> imageList = new ArrayList<>(); // 실제 화면에 보여지는 이미지 리스트
    public MutableLiveData<Boolean> editPhoto = new MutableLiveData<>(false);
    public MutableLiveData<String> bt_add = new MutableLiveData<>();

    private MutableLiveData<Integer> _removeImage = new MutableLiveData<>();
    public LiveData<Integer> removeImage = _removeImage;
    public Integer imageViewSize = 0;


    // 이미지 리스트 반영
    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }


    // 기존 이미지에 추가하기 - 새로 카메라로 사진을 찍었을 경우 !
    public void addImageList(File image) {
        imageList.add(image.toString());
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_preview, parent, false);
        int width = 250;  // 원하는 너비
        int height = 250; // 원하는 높이
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        view.setLayoutParams(layoutParams);
        return new PhotoViewHolder(view, this);
    }


    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String imageUrl = null;
        if(imageList.size() == 1 && position == 0){
            imageUrl = imageList.get(0);

        } else if (imageList.size() == 2) {
            imageUrl = imageList.get(position);
        }
        holder.bind(imageUrl);
        imageViewSize = imageList.size();
    }


    public int getImageListItemCount(){
        return (imageList != null) ? imageList.size() : 0;
    }


    @Override
    public int getItemCount() {
        return 2;
    }


    /* DELETE */
    public void removePhoto(int clickedPosition) {
        imageList.remove(clickedPosition);
        _removeImage.setValue(clickedPosition);
        notifyDataSetChanged();
        editPhoto.setValue(true);
    }


    /* ------------------------------------------------------ ViewHolder ------------------------------------------------------ */

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public String TAG = this.getClass().getName();
        AppCompatImageView photoImageView;
        AppCompatImageView bt_photo_remove;
        AppCompatImageView add_image_1;
        private AlertDialog.Builder alertDialogBuilder;
        PhotoUrlAdapter photoUrlAdapter;


        PhotoViewHolder(View itemView, PhotoUrlAdapter photoUrlAdapter) {
            super(itemView);
            this.photoUrlAdapter = photoUrlAdapter;
            photoImageView = itemView.findViewById(R.id.img_1);
            photoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bt_photo_remove = itemView.findViewById(R.id.bt_photo_remove);
            add_image_1 = itemView.findViewById(R.id.add_image_1);  // 갤러리에서 사진 추가하기

            add_image_1.setOnClickListener(v -> {
                photoUrlAdapter.bt_add.setValue("click");
            });

        }/* ./Constructor */


        public void bind(String imageUrl) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(photoImageView.getContext())
                        .load(imageUrl)
                        .override(250,250)      // 최적화
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(photoImageView);
                bt_photo_remove.setVisibility(View.VISIBLE);
                add_image_1.setVisibility(View.GONE);

            } else {
                photoImageView.setImageDrawable(null);
                add_image_1.setVisibility(View.VISIBLE);
                bt_photo_remove.setVisibility(View.GONE);
            }

            // 삭제 버튼
            bt_photo_remove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                showAlertDialog(position);
            });

        }/* ./bind */


        // 사진 삭제
        private void showAlertDialog(int clickedPosition) {
            alertDialogBuilder = new AlertDialog.Builder(itemView.getContext());
            alertDialogBuilder.setTitle("");
            alertDialogBuilder.setMessage("사진을 삭제하시겠습니까?");
            alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    photoUrlAdapter.removePhoto(clickedPosition);
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


    }   /* ./ViewHolder */


}

