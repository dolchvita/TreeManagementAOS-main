package com.snd.app.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.snd.app.R;
import com.snd.app.domain.treeManagement.ManagementMenuItem;

import java.util.List;

public class TreeManagementMenuAdapter extends RecyclerView.Adapter<TreeManagementMenuAdapter.ViewHolder> {
    public static String TAG = "MenuAdapter";

    private List<ManagementMenuItem> menuItems;
    public MutableLiveData<Integer> menuClick = new MutableLiveData<>();


    public TreeManagementMenuAdapter(List<ManagementMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_management_menu, parent, false);
        return new ViewHolder(view, this);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManagementMenuItem menuItem = menuItems.get(position);
        holder.menuTextView.setText(menuItem.getMenuName());

        switch (position){
            case 0:
                holder.tree_management_img.setImageResource(R.drawable.ic_leaf);
                break;
            case 1:
                holder.tree_management_img.setImageResource(R.drawable.ic_seedling);
                break;
            case 2:
                holder.tree_management_img.setImageResource(R.drawable.ic_hand_medical);
                break;
            case 3:
                holder.tree_management_img.setImageResource(R.drawable.ic_stethoscope);
                break;
            case 4:
                holder.tree_management_img.setImageResource(R.drawable.ic_droplet);
                break;
            case 5:
                holder.tree_management_img.setImageResource(R.drawable.ic_thermometer);
                break;
            case 6:
                holder.tree_management_img.setImageResource(R.drawable.ic_ellipsis);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return menuItems.size();
    }


    /* ----------------------------------ViewHolder---------------------------------- */

    class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView menuTextView;
        AppCompatImageView tree_management_img;
        TreeManagementMenuAdapter treeManagementMenuAdapter;

        public ViewHolder(@NonNull View itemView, TreeManagementMenuAdapter treeManagementMenuAdapter) {
            super(itemView);
            menuTextView = itemView.findViewById(R.id.tree_management_menu);
            tree_management_img = itemView.findViewById(R.id.tree_management_img);
            this.treeManagementMenuAdapter = treeManagementMenuAdapter;

            itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    Log.d(TAG, "** onClick ** " + clickedPosition);
                    menuTextView.setBackgroundColor(R.color.teal_200);
                    treeManagementMenuAdapter.menuClick.setValue(clickedPosition);
                }
            });
        }
    }


}