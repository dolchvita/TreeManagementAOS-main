package com.snd.app.repository.business;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
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
import com.snd.app.databinding.MainBusinessFrBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class BusinessFragment extends TMFragment {
    MainBusinessFrBinding mainBusinessFrBinding;
    BusinessViewModel businessVM;

    private SharedPreferenceManager sharedPreferencesManager;
    TableLayout business_table;
    TextView projectNameView;
    TableRow tableRow;
    String company;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainBusinessFrBinding = DataBindingUtil.inflate(inflater, R.layout.main_business_fr, container, false);
        mainBusinessFrBinding.setLifecycleOwner(this);
        businessVM = new ViewModelProvider(getActivity()).get(BusinessViewModel.class);
        mainBusinessFrBinding.setVm(businessVM);

        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        businessVM.Authorization.setValue(sharedPreferencesManager.getString("token"));
        company = sharedPreferencesManager.getString("company");

        return mainBusinessFrBinding.getRoot();
    }



    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        business_table = view.findViewById(R.id.business_table);

        TextView textView = mainBusinessFrBinding.businessName;
        String text = getString(R.string.company_format, company);

        // 색상 변경
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        spannable.setSpan(new ForegroundColorSpan(R.color.cocoa_brown), 0, company.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Color.RED 대신 원하는 색상값을 설정
        textView.setText(spannable);

        businessVM.Authorization.observe(getActivity(), s -> {
            businessVM.getCompanyBusinessList(s, company);
        });


        businessVM.getProjectList().observe(getActivity(), projectDTOS -> {
            try {
                initProjectTable(projectDTOS);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

    }




    @SuppressLint("ResourceAsColor")
    void initProjectTable(List projectDTOS) throws JSONException {
        Context context = getContext();
        if (context == null) {
            return;
        }

        business_table.removeAllViews();
        FormatDataTime formatDataTime = new FormatDataTime();
        int desiredHeight = 150;  // 원하는 높이를 픽셀로 설정 (예: 100픽셀)
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, desiredHeight);

        JSONArray jsonArray = new JSONArray(projectDTOS);

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            tableRow = new TableRow(getContext());

            // 연번
            TextView serialNumberView = new TextView(getContext());
            serialNumberView.setGravity(Gravity.CENTER);
            serialNumberView.setLayoutParams(layoutParams); // 높이 설정 추가
            serialNumberView.setText((jsonObject.getString("index_id")));
            tableRow.addView(serialNumberView);

            // 사업명
            projectNameView = new TextView(getContext());
            projectNameView.setGravity(Gravity.CENTER);
            projectNameView.setTextColor(R.color.cocoa_brown);
            projectNameView.setLayoutParams(layoutParams); // 높이 설정 추가
            projectNameView.setText(jsonObject.getString("business"));
            tableRow.addView(projectNameView);

            // 시작일자
            TextView startDateView = new TextView(getContext());
            startDateView.setGravity(Gravity.CENTER);
            startDateView.setLayoutParams(layoutParams); // 높이 설정 추가

            List<Double> startDate = formatDataTime.setDoubleList(jsonObject.getString("start"));
            Log.d(TAG, "점검중 startDate " + startDate);

            String start = formatDataTime.localDateFormat(startDate);
            Log.d(TAG, "점검중 start " + start);

            startDateView.setText(start);
            tableRow.addView(startDateView);

            // 종료일자
            TextView endDateView = new TextView(getContext());
            endDateView.setGravity(Gravity.CENTER);
            endDateView.setLayoutParams(layoutParams); // 높이 설정 추가
            String terminationValue = jsonObject.getString("termination");
            if(terminationValue == null || "null".equals(terminationValue)){
                endDateView.setText("");
            }else {
                List<Double> endDate = formatDataTime.setDoubleList(jsonObject.getString("termination"));
                String end = formatDataTime.localDateFormat(endDate);
                endDateView.setText(end);
            }
            tableRow.addView(endDateView);
            business_table.addView(tableRow);

            setOnClickListner();
        }
    }



    private void setOnClickListner(){
        tableRow.setOnClickListener(v -> {
            TableRow clickedRow = (TableRow) v;
            projectNameView = (TextView) clickedRow.getChildAt(1);
            String projectName = projectNameView.getText().toString();
            Log.d(TAG, "결과!!!!" + projectName);
            sharedPreferencesManager.saveString("projectName", projectName);
            ((MainActivity)getActivity()).switchFragment(4);
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainBusinessFrBinding = null;
        sharedPreferencesManager = null;
        business_table = null;
        projectNameView = null;
        tableRow = null;
    }



}
