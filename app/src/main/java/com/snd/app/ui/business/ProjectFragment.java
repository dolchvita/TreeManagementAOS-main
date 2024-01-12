package com.snd.app.ui.business;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.snd.app.MainActivity;
import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.dataUtil.FormatDataTime;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.BusinessProjectFrBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ProjectFragment extends TMFragment {
    private SharedPreferenceManager sharedPreferencesManager;
    BusinessProjectFrBinding businessProjectFrBinding;
    ProjectViewModel projectVM;
    TableLayout project_table;
    TableRow tableRow;
    String projectName;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        businessProjectFrBinding = DataBindingUtil.inflate(inflater, R.layout.business_project_fr, container, false);
        businessProjectFrBinding.setLifecycleOwner(this);
        projectVM = new ViewModelProvider(getActivity()).get(ProjectViewModel.class);
        businessProjectFrBinding.setVm(projectVM);

        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        projectVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        projectName = sharedPreferencesManager.getString("projectName");

        return businessProjectFrBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        project_table = view.findViewById(R.id.business_table);


        ((MainActivity)getActivity()).backButton.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).backButton.setOnClickListener(v -> {
            ((MainActivity)getActivity()).switchFragment(3);
            ((MainActivity)getActivity()).backButton.setVisibility(View.GONE);
        });


        TextView textView = businessProjectFrBinding.businessProjectName;
        String text = getString(R.string.project_format, projectName);
        textView.setText(text);

        projectVM.Authorization.observe(getActivity(), s -> {
            Log.d(TAG, projectName);
            projectVM.getCompanyBusinessList(s, projectName);
        });

        projectVM.getManagementList().observe(getActivity(), objects -> {
            Log.d(TAG, "" + objects);
            try {
                initProjectTable(objects);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

    }


    void initProjectTable(List projectDTOS) throws JSONException {
        Context context = getContext();
        if (context == null) {
            return;
        }
        project_table.removeAllViews();

        FormatDataTime formatDataTime = new FormatDataTime();
        int desiredHeight = 150;  // 원하는 높이를 픽셀로 설정 (예: 100픽셀)
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, desiredHeight);
        layoutParams.setMargins(5, 0, 0, 0);

        JSONArray jsonArray = new JSONArray(projectDTOS);
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            tableRow = new TableRow(getContext());

            // 분류
            TextView businessView = new TextView(getContext());
            businessView.setLayoutParams(layoutParams); // 높이 설정 추가
            businessView.setText((jsonObject.getString("business")));
            businessView.setBackgroundResource(R.drawable.border_table_row);
            tableRow.addView(businessView);

            // 대상수목
            TextView NfcView = new TextView(getContext());
            NfcView.setLayoutParams(layoutParams); // 높이 설정 추가
            NfcView.setText(jsonObject.getString("nfc"));
            NfcView.setBackgroundResource(R.drawable.border_table_row);
            tableRow.addView(NfcView);

            // 작업자
            TextView operatorView = new TextView(getContext());
            operatorView.setLayoutParams(layoutParams); // 높이 설정 추가
            operatorView.setText(jsonObject.getString("operator"));
            operatorView.setBackgroundResource(R.drawable.border_table_row);
            tableRow.addView(operatorView);

            // 시행일시
            TextView operationDateView = new TextView(getContext());
            operationDateView.setLayoutParams(layoutParams); // 높이 설정 추가
            //= jsonObject.getString("operationDate");


            List<Double> startDate = formatDataTime.setDoubleList(jsonObject.getString("operationDate"));
            String start = formatDataTime.localDateFormat(startDate);
            operationDateView.setText(start);
            operationDateView.setBackgroundResource(R.drawable.border_table_row);

            tableRow.addView(operationDateView);
            project_table.addView(tableRow);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        businessProjectFrBinding = null;
        sharedPreferencesManager = null;
        project_table = null;
        tableRow = null;
    }


}
