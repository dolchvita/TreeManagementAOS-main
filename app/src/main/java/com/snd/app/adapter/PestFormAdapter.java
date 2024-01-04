package com.snd.app.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.snd.app.R;
import com.snd.app.data.dataUtil.DatePickerHelper;
import com.snd.app.domain.tree.dto.TreePestInfoDTO;

import java.util.ArrayList;
import java.util.List;


public class PestFormAdapter extends RecyclerView.Adapter<PestFormAdapter.ViewHolder> {
    // 데이터 관련 작업
    public String TAG = this.getClass().getName();
    private final List<String> inputBox;
    List<String> treePestNameList;

    public MutableLiveData<String> pestInfoSelectedItem;
    public MutableLiveData<String> severitySelectedItem;

    public ArrayList<TreePestInfoDTO> treePestInfoList;

    // Constructor
    public PestFormAdapter(List<String> treePestNameList) {
        this.inputBox = new ArrayList<>();
        this.treePestNameList = treePestNameList;
        pestInfoSelectedItem = new MutableLiveData<>();
        severitySelectedItem = new MutableLiveData<>();
        treePestInfoList = new ArrayList<>();   // 디티오가 담길 리스트
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.snd.app.R.layout.item_pest_info_register_form, parent, false);
        return new ViewHolder(view, this);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    }


    public ArrayList<TreePestInfoDTO> getCurrentData() {
        return treePestInfoList;
    }


    @Override
    public int getItemCount() {
        return inputBox.size();
    }


    // 프레그먼트에서 호출됨
    public void addPestItemBox(String pestInfo, TreePestInfoDTO treePestInfoDTO) {
        inputBox.add(pestInfo);
        treePestInfoList.add(treePestInfoDTO);
        notifyItemInserted(inputBox.size() - 1);
    }


    public void removeItemBox(int position, Boolean flag) {
        if (flag && position >= 0 && position < treePestInfoList.size()) {
            inputBox.remove(position);
            treePestInfoList.remove(position);
            notifyItemRemoved(position);
        }
    }





    /* ---------------------------------------------------- ViewHolder ---------------------------------------------------- */
    // UI 관련 작업
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String TAG = this.getClass().getName();
        PestFormAdapter pestFormAdapter;
        Spinner pestInfoSpinner;
        Spinner severitySpinner;
        AppCompatButton bt_occurred;
        private AlertDialog.Builder alertDialogBuilder;
        public Boolean flag = true;
        public AppCompatEditText t_pest_info_occurred;
        public AppCompatEditText t_pest_info_name;
        public AppCompatEditText t_pest_info_recovered;
        public AppCompatTextView tree_pest_info_recovered_tv;
        AppCompatImageView tree_pest_info_register_remove;
        String result;
        // 어댑터에서 매핑을 다 하고 보내자 !
        TreePestInfoDTO treePestInfoDTO;


        public ViewHolder(@NonNull View itemView, PestFormAdapter pestFormAdapter) {
            super(itemView);
            this.pestFormAdapter = pestFormAdapter;
            pestInfoSpinner = itemView.findViewById(R.id.tree_pest_info_register_spinner);  // 병충해명 스피너 리스트
            severitySpinner = itemView.findViewById(R.id.tree_pest_info_register_severity_spinner);

            // 날짜선택 버튼
            bt_occurred = itemView.findViewById(R.id.bt_occurred);
            t_pest_info_occurred = itemView.findViewById(R.id.tree_pest_info_register_occurred);

            t_pest_info_name = itemView.findViewById(R.id.tree_pest_info_register_name);
            t_pest_info_recovered = itemView.findViewById(R.id.tree_pest_info_register_recovered);
            tree_pest_info_recovered_tv = itemView.findViewById(R.id.tree_pest_info_register_recovered_tv);
            tree_pest_info_register_remove = itemView.findViewById(R.id.tree_pest_info_register_remove);    // 삭제 버튼


            treePestInfoDTO = new TreePestInfoDTO();

            if(pestFormAdapter.treePestNameList != null){
                initPestSpinner();
                initSeveritySpinner();
            }


            // 클릭시 삭제 로직 구현
            tree_pest_info_register_remove.setOnClickListener(v -> {
                int clickedPosition = getAdapterPosition();
                Log.d(TAG, "** onClick ** "+clickedPosition);
                showAlertDialog(clickedPosition);
            });



            // 발생일 버튼
            bt_occurred.setOnClickListener(v -> {
                TreePestInfoDTO currentDTO = pestFormAdapter.treePestInfoList.get(getAdapterPosition());

                DatePickerHelper.showDatePickerDialog(itemView.getContext(), selectedDate -> {
                    t_pest_info_occurred.setText(selectedDate);
                    currentDTO.setOccurred(selectedDate);
                    //Log.d(TAG, "발생일 매핑 " + currentDTO.getOccurred());
                });
            });

        }/* ./Constructor */




        /* ---------------------------------- Pest Spinner ---------------------------------- */

        private void initPestSpinner(){
            // 병충해명 Spinner 설정
            ArrayAdapter<String> pestAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, pestFormAdapter.treePestNameList);
            pestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pestInfoSpinner.setAdapter(pestAdapter);
            pestInfoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mappingPestInfoSelectedItem(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }



        public void  mappingPestInfoSelectedItem(int position){
            String selected = pestFormAdapter.treePestNameList.get(position);;
            TreePestInfoDTO currentDTO = pestFormAdapter.treePestInfoList.get(getAdapterPosition());

            if(selected.equals("직접 입력")){
                t_pest_info_name.setVisibility(View.VISIBLE);
                inputSpecies(currentDTO);
            }else {
                t_pest_info_name.setVisibility(View.GONE);
                // 리스트 안에 해당 디티오 변경하기
                currentDTO.setPest(selected);
            }
        }


        public String inputSpecies(TreePestInfoDTO currentDTO){
            t_pest_info_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    result = s.toString();
                    currentDTO.setPest(result);
                }
            });
            return result;
        }


        /* ---------------------------------- Severity Spinner ---------------------------------- */

        private void initSeveritySpinner(){
            // 심각도 Spinner 설정
            String[] severityArray = itemView.getContext().getResources().getStringArray(R.array.treePest_severity);
            ArrayAdapter<String> severityAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, severityArray);
            severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            severitySpinner.setAdapter(severityAdapter);
            severitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedSeverity = severityArray[position];
                    TreePestInfoDTO currentDTO = pestFormAdapter.treePestInfoList.get(getAdapterPosition());

                    currentDTO.setSeverity(selectedSeverity);
                    if(selectedSeverity.equals("완치 (일 선택)")){
                        DatePickerHelper.showDatePickerDialog(itemView.getContext(), selectedDate -> {
                            currentDTO.setSeverity("완치");
                            currentDTO.setRecovered(selectedDate);
                            setRecorvered(selectedDate);
                        });

                    }else {
                        tree_pest_info_recovered_tv.setVisibility(View.GONE);
                        t_pest_info_recovered.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }


        // 완치일 매핑
        public void setRecorvered(String str) {
            tree_pest_info_recovered_tv.setVisibility(View.VISIBLE);
            t_pest_info_recovered.setVisibility(View.VISIBLE);
            t_pest_info_recovered.setText(str);
        }


        // 아이템 삭제
        private void showAlertDialog(int integer) {
            alertDialogBuilder = new AlertDialog.Builder(itemView.getContext());
            alertDialogBuilder.setTitle("아이템 입력을 취소하시겠습니까?");
            alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    flag = true;
                    Log.d(TAG, "showAlertDialog 값은? " + integer);

                    pestFormAdapter.removeItemBox(integer, flag);
                }
            });
            alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    flag = false;
                }
            });
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }



    } /* ./ViewHolder */



}

