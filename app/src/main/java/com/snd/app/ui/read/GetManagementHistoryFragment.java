package com.snd.app.ui.read;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.snd.app.R;
import com.snd.app.adapter.ManagementHistoryAdapter;
import com.snd.app.common.TMFragment;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.GetManagementHistoryFrBinding;


public class GetManagementHistoryFragment extends TMFragment {
    GetManagementHistoryFrBinding getManagementHistoryFrBinding;
    GetManagementHistoryViewModel getManagementHistoryVM;
    SharedPreferenceManager sharedPreferencesManager;
    ManagementHistoryAdapter historyAdapter;
    RecyclerView tree_management_history;
    String idHex;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getManagementHistoryFrBinding = DataBindingUtil.inflate(inflater, R.layout.get_management_history_fr, container, false);
        getManagementHistoryFrBinding.setLifecycleOwner(this);
        getManagementHistoryVM = new ViewModelProvider(this).get(GetManagementHistoryViewModel.class);
        getManagementHistoryFrBinding.setVm(getManagementHistoryVM);
        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        getManagementHistoryVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        idHex = sharedPreferencesManager.getString("idHex");

        return getManagementHistoryFrBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getManagementHistoryVM.Authorization.observe(getActivity(), s -> {
            getManagementHistoryVM.getManagementHistoryListAll(s, idHex);
        });

        tree_management_history = getManagementHistoryFrBinding.treeManagementHistory;

        getManagementHistoryVM.getManagementList().observe(getActivity(), objects -> {
            if(objects.size() > 0){
                historyAdapter = new ManagementHistoryAdapter(objects);
                tree_management_history.setAdapter(historyAdapter);

            }else {
                TextView no_history_text = getManagementHistoryFrBinding.noHistoryText;
                no_history_text.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getManagementHistoryFrBinding = null;
        sharedPreferencesManager = null;
        historyAdapter = null;
        tree_management_history = null;
    }


}
