package com.snd.app.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.snd.app.R;
import com.snd.app.data.camera.ImageExif;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* <수목 기본 정보 입력>에서 사진 데이터 "등록"할 때 사용될 어댑터 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    public String TAG = this.getClass().getName();

    /* 지금 작업중 */
    //private List<Bitmap> imageList = new ArrayList<>();
    private List<File> imageList2 = new ArrayList<>();

    private MutableLiveData<Integer> _removeImage = new MutableLiveData<>();
    public LiveData<Integer> removeImage = _removeImage;
    public MutableLiveData<String> bt_add = new MutableLiveData<>();


    public void setImageList(List<Bitmap> imageList) {
        Log.d(TAG, "포토어댑터가 받은 이미지 목록 " + imageList);
        //this.imageList = imageList;
        notifyDataSetChanged();     // 리스트 갱신
    }


    public void setImageList2(List<File> imageList2) {
        this.imageList2 = imageList2;
        notifyDataSetChanged();     // 리스트 갱신
    }



    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_preview, parent, false);
        int width = 240;  // 원하는 너비
        int height = 240; // 원하는 높이
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        view.setLayoutParams(layoutParams);
        return new PhotoViewHolder(view, this);
    }


    /* 화면 렌더링 */
    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        File photo = null;
        Log.d(TAG, "onBindViewHolder 에서 리스트 확인 ** " +imageList2);
        if (position < imageList2.size()) {
            photo = imageList2.get(position);


            // 비트맵에서는 이미지 경로가 없음, 이미지 파일의 경로나 파일 객체가 있어야 함 !
            //imageExif.rotateImageIfRequired()
        }
        holder.bind2(photo);
    }


    // 실제 사진이 담겨있는 리스트
    public int getImageListItemCount(){
        return (imageList2 != null) ? imageList2.size() : 0;
    }


    @Override
    public int getItemCount() {
        return 2;
    }


    /* 지금 작업중 */
    public void removePhoto(int clickedPosition) {
        Log.d(TAG, "RemoveImage 호출 ** " + clickedPosition + " / " + imageList2);
        if(imageList2 != null){
            File file = imageList2.get(clickedPosition);
            imageList2.remove(file);
        }
        _removeImage.setValue(clickedPosition);
        Log.d(TAG, "포토어댑터 남은 리스트 " + imageList2);      // [/data/user/0/com.snd.app/cache/20231204_131940.jpg]
        notifyDataSetChanged();
    }


    /* ------------------------------------------------------ ViewHolder ------------------------------------------------------ */

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public String TAG = this.getClass().getName();
        AppCompatImageView photoImageView;
        AppCompatImageView bt_photo_remove;
        AppCompatImageView add_image_1;
        private AlertDialog.Builder alertDialogBuilder;
        PhotoAdapter photoAdapter;
        ImageExif imageExif;


        /* Concstructor */
        PhotoViewHolder(View itemView, PhotoAdapter photoAdapter) {
            super(itemView);
            this.photoAdapter = photoAdapter;
            photoImageView = itemView.findViewById(R.id.img_1);
            photoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bt_photo_remove = itemView.findViewById(R.id.bt_photo_remove);
            add_image_1 = itemView.findViewById(R.id.add_image_1);

            imageExif = new ImageExif();

            // 갤러리 추가 버튼
            add_image_1.setOnClickListener(v -> {
                photoAdapter.bt_add.setValue("click");
            });

        }   /* ./Concstructor */


        /* 이미지뷰에 비트맵 설정을 하는구나 */
        public void bind2(File file) {
            if (file != null) {
                Bitmap image = imageExif.rotateImageIfRequired(file);
                Bitmap resizedImage  = Bitmap.createScaledBitmap(image, 200, 200, true);

                // 비트맵 표시하기 전 처리할 것 (이미지뷰에는 비트맵 표시가 맞다)
                photoImageView.setImageBitmap(resizedImage );
                bt_photo_remove.setVisibility(View.VISIBLE);
                add_image_1.setVisibility(View.GONE);

            } else {
                // 이미지 파일이 없을 경우 처리. 예를 들어, 기본 이미지 설정
                photoImageView.setImageDrawable(null);
                add_image_1.setVisibility(View.VISIBLE);
                bt_photo_remove.setVisibility(View.GONE);
            }

            // 삭제 버튼
            bt_photo_remove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                showAlertDialog(position);
            });
        }   /* ./bind */


        // 사진 삭제
        private void showAlertDialog(int clickedPosition) {
            alertDialogBuilder = new AlertDialog.Builder(itemView.getContext());
            alertDialogBuilder.setTitle("");
            alertDialogBuilder.setMessage("사진을 삭제하시겠습니까?");
            alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    photoAdapter.removePhoto(clickedPosition);
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