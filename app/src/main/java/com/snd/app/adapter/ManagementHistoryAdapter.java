package com.snd.app.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.snd.app.R;
import com.snd.app.data.dataUtil.FormatDataTime;
import com.snd.app.domain.treeManagement.TreeManagementIntegratedVOForProject;

import java.util.ArrayList;
import java.util.List;

public class ManagementHistoryAdapter extends RecyclerView.Adapter<ManagementHistoryAdapter.ViewHolder> {
    public String TAG = this.getClass().getName();
    private List<TreeManagementIntegratedVOForProject> TreeManagementHistoryList = new ArrayList<>();
    FormatDataTime formatDataTime;

    public ManagementHistoryAdapter(List<Object> items) {
        Gson gson = new Gson();
        for(Object obj : items){
            String jsonData = gson.toJson(obj);        // Object -> Json -> DTO
            TreeManagementIntegratedVOForProject vo = new TreeManagementIntegratedVOForProject();
            vo = gson.fromJson(jsonData, TreeManagementIntegratedVOForProject.class);
            TreeManagementHistoryList.add(vo);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_management_history, parent, false);
        formatDataTime = new FormatDataTime();
        return new ManagementHistoryAdapter.ViewHolder(view, this);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TreeManagementIntegratedVOForProject dto = TreeManagementHistoryList.get(position);
        holder.tree_management_history_nfc.setText(dto.getNfc());
        holder.tree_management_history_business.setText(dto.getBusiness());
        holder.tree_management_history_operation_date.setText(formatDataTime.getBasicInserted(dto.getOperationDate()));
    }


    @Override
    public int getItemCount() {
        return TreeManagementHistoryList.size();
    }


    /*
    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }
     */


    /* ----------------------------------ViewHolder---------------------------------- */

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ManagementHistoryAdapter historyAdapter;
        AppCompatTextView tree_management_history_nfc;
        AppCompatTextView tree_management_history_business;
        AppCompatTextView tree_management_history_operation_date;

        public ViewHolder(@NonNull View itemView, ManagementHistoryAdapter historyAdapter) {
            super(itemView);
            this.historyAdapter = historyAdapter;

            tree_management_history_nfc = itemView.findViewById(R.id.tree_management_history_nfc);
            tree_management_history_business = itemView.findViewById(R.id.tree_management_history_business);
            tree_management_history_operation_date = itemView.findViewById(R.id.tree_management_history_operation_date);
            // Example: textView = itemView.findViewById(R.id.your_text_view_id);


            itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                }
            });
        }
    }



}
