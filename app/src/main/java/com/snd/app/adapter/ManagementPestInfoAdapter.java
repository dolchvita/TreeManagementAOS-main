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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.snd.app.R;
import com.snd.app.data.dataUtil.DatePickerHelper;
import com.snd.app.data.dataUtil.FormatDataTime;
import com.snd.app.domain.tree.dto.TreePestInfoDTO;
import com.snd.app.domain.tree.vo.TreePestInfoVO;

import java.util.ArrayList;
import java.util.List;

// 병충해 조회시 나타나는 폼 (수목이력관리)
public class ManagementPestInfoAdapter extends RecyclerView.Adapter<ManagementPestInfoAdapter.ViewHolder>{
    public static String TAG = "ManagementPestInfoAdapter";
    AppCompatTextView tree_pest_info_species;
    FormatDataTime formatDataTime;

    public MutableLiveData<List<Object>> notifyDataChange = new MutableLiveData<>();
    public List<TreePestInfoVO> treePestInfoList;   // 정보가 들어있는 객체 리스트
    List<String> treePestNameList = new ArrayList<>();      // 서버에서 데이터 받을 것

    public MutableLiveData<TreePestInfoDTO> modifyProcess = new MutableLiveData<>();


    // Constructor
    public ManagementPestInfoAdapter(List<Object> dtoList) {
        treePestInfoList = convertToDTO(dtoList);
        Log.d(TAG, "리스트에 뭐들었나 확인 " + treePestInfoList);
    }


    private List<TreePestInfoVO> convertToDTO(List<Object> dtoList){
        treePestInfoList = new ArrayList<>();
        Gson gson = new Gson();
        for(Object obj : dtoList){
            String jsonData = gson.toJson(obj);
            TreePestInfoVO vo = new TreePestInfoVO();
            vo = gson.fromJson(jsonData, TreePestInfoVO.class);
            treePestInfoList.add(vo);
        }

        /* DTO가 아닌 VO로 한 이유?
         * 발생일시 등을 List<Double> 형으로 받기 위함! */
        return treePestInfoList;
    }


    // 병해명 리스트 채우기
    public void initTreePestNameList(List<String> treePestNameList){
        this.treePestNameList = treePestNameList;
    }


    @NonNull
    @Override
    public ManagementPestInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.snd.app.R.layout.item_pest_info, parent, false);
        formatDataTime = new FormatDataTime();
        return new ManagementPestInfoAdapter.ViewHolder(view, this);
    }


    @Override
    public void onBindViewHolder(@NonNull ManagementPestInfoAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onCreateViewHolder 호출 ** ");

        TreePestInfoVO treePestInfoVO = treePestInfoList.get(position);
        // 여기 코드를 홀더로 옮겨보자 !
        tree_pest_info_species.setText(treePestInfoVO.getSpecies());

        // 화면 요소 매핑
        holder.tree_pest_info_name.setText(treePestInfoVO.getPest());
        if(treePestInfoVO.getOccurred() != null){
            holder.tree_pest_info_register_occurred.setText(formatDataTime.localDateFormat(treePestInfoVO.getOccurred()));
        }
        holder.tree_pest_info_severity.setText(treePestInfoVO.getSeverity());
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount 호출 ** ");
        return treePestInfoList.size();
    }


    public void addPestInfoItem(List<Object> item) {
        convertToDTO(item);
        notifyItemInserted(treePestInfoList.size() - 1);
    }



    /* ------------------------------------------------------ ViewHolder ------------------------------------------------------ */

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ManagementPestInfoAdapter adapter;
        Spinner tree_pest_info_name_spinner;
        public AppCompatEditText tree_pest_info_name;
        AppCompatEditText tree_pest_info_register_occurred; // 발생일
        Spinner tree_pest_info_severity_spinner;    // 심각도
        AppCompatEditText tree_pest_info_severity;
        AppCompatTextView tree_pest_info_recovered_tv;  // 완치일
        AppCompatTextView tree_pest_info_recovered;
        AppCompatButton pest_write_save;
        AppCompatButton bt_occurred;
        String[] severityArray;
        private AlertDialog.Builder alertDialogBuilder;
        TreePestInfoDTO treePestInfoDTO;
        String result = null;
        FormatDataTime formatDataTime;


        /* Concstructor */
        public ViewHolder(@NonNull View itemView, ManagementPestInfoAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            tree_pest_info_name_spinner = itemView.findViewById(R.id.tree_pest_info_name_spinner);  // 병해명
            tree_pest_info_name = itemView.findViewById(R.id.tree_pest_info_name);
            tree_pest_info_register_occurred = itemView.findViewById(R.id.tree_pest_info_register_occurred);    // 발생일
            tree_pest_info_severity_spinner = itemView.findViewById(R.id.tree_pest_info_severity_spinner);  // 심각도
            tree_pest_info_severity = itemView.findViewById(R.id.tree_pest_info_severity);
            tree_pest_info_recovered_tv = itemView.findViewById(R.id.tree_pest_info_recovered_tv);  // 완치일
            tree_pest_info_recovered = itemView.findViewById(R.id.tree_pest_info_recovered);

            pest_write_save = itemView.findViewById(R.id.pest_write_save);  // 수정하기 버튼
            bt_occurred = itemView.findViewById(R.id.bt_occurred);  // 날짜 선택 버튼

            adapter.tree_pest_info_species = itemView.findViewById(R.id.tree_pest_info_species);

            treePestInfoDTO = new TreePestInfoDTO();
            formatDataTime = new FormatDataTime();

            if(adapter.treePestNameList != null ){
                initPestSpinner();
                initSeveritySpinner();
            }


            // 발생일 날짜 선택 버튼
            bt_occurred.setOnClickListener(v -> {
                DatePickerHelper.showDatePickerDialog(itemView.getContext(), selectedDate -> {
                    tree_pest_info_register_occurred.setText(selectedDate);
                    treePestInfoDTO.setOccurred(selectedDate);
                    Log.d(TAG, "발생일 매핑 " + treePestInfoDTO.getOccurred());
                });
            });


            // 수정 버튼 클릭 이벤트
            pest_write_save.setOnClickListener(v -> {
                int position = getAdapterPosition();
                TreePestInfoVO vo = adapter.treePestInfoList.get(position);
                mappingDTO(vo);

                if(checkPestInfoData()){
                    modifyProcess();
                }else {
                    Toast.makeText(itemView.getContext(), "입력정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            });


            if(tree_pest_info_severity.getText().equals("완치")){
                Log.d(TAG, "완치 처리 ??");
            }


        }/* ./Concstructor */



        private void mappingDTO(TreePestInfoVO vo){
            treePestInfoDTO.setIndex_no(vo.getIndex_no());
            treePestInfoDTO.setNfc(vo.getNfc());
            treePestInfoDTO.setSubmitter(vo.getSubmitter());
            treePestInfoDTO.setVendor(vo.getVendor());
            if(treePestInfoDTO.getPest() == null){
                treePestInfoDTO.setPest(vo.getPest());
            }
            if(treePestInfoDTO.getOccurred() == null){
                // 직접 매핑
                String str = formatDataTime.localDateFormat(vo.getOccurred());
                treePestInfoDTO.setOccurred(str);
            }
            if(treePestInfoDTO.getSeverity() == null){
                treePestInfoDTO.setSeverity(vo.getSeverity());
            }
        }



        // 공란 확인
        private boolean checkPestInfoData(){
            Log.d(TAG, "체크 " + treePestInfoDTO);
            return (treePestInfoDTO.getPest() == null || treePestInfoDTO.getOccurred() == null) ? false : true;
        }



        /* ----------------------------------------- Pest Spinner ----------------------------------------- */

        private void initPestSpinner(){
            // 병충해명 Spinner 설정
            ArrayAdapter<String> pestAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, adapter.treePestNameList);
            pestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tree_pest_info_name_spinner.setAdapter(pestAdapter);
            tree_pest_info_name_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mappingPestInfoSelectedItem(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }


        // 병해명 매핑
        public void mappingPestInfoSelectedItem(int position){
            String selected = adapter.treePestNameList.get(position);

            if(selected.equals("직접 입력")){
                inputPestName();

            }else {
                tree_pest_info_name.setText(selected);
                treePestInfoDTO.setPest(selected);
            }
        }


        // 병해명 수동 입력
        public String inputPestName(){
            tree_pest_info_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    treePestInfoDTO.setPest(s.toString());
                    result = s.toString();
                }
            });
            return result;
        }


        /* ----------------------------------------- Severity Spinner ----------------------------------------- */

        private void initSeveritySpinner(){
            // 심각도 Spinner 설정
            severityArray = itemView.getContext().getResources().getStringArray(R.array.treePest_severity);
            ArrayAdapter<String> severityAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, severityArray);
            severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tree_pest_info_severity_spinner.setAdapter(severityAdapter);
            tree_pest_info_severity_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedSeverity = severityArray[position];
                    treePestInfoDTO.setSeverity(selectedSeverity);
                    if(selectedSeverity.equals("완치 (일 선택)")){
                        DatePickerHelper.showDatePickerDialog(itemView.getContext(), selectedDate -> {
                            treePestInfoDTO.setSeverity("완치");
                            treePestInfoDTO.setRecovered(selectedDate);
                            setRecorvered(selectedDate);
                        });
                    }else {
                        tree_pest_info_recovered_tv.setVisibility(View.GONE);
                        tree_pest_info_recovered.setVisibility(View.GONE);
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
            tree_pest_info_recovered.setVisibility(View.VISIBLE);
            tree_pest_info_recovered.setText(str);
        }


        private void modifyProcess(){
            alertDialogBuilder = new AlertDialog.Builder(itemView.getContext());
            alertDialogBuilder.setTitle("병해 정보를 수정하시겠습니까?");
            alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.modifyProcess.setValue(treePestInfoDTO);
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



    }/* ./ViewHolder */


}