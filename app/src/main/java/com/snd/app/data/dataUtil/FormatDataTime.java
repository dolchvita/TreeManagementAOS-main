package com.snd.app.data.dataUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FormatDataTime {
    public String TAG = this.getClass().getName();


    // 문자열을 받으면 그것을 더블형 리스트로 반환
    public List<Double> setDoubleList(String input){
        input = input.replaceAll("\\[|\\]|start=", "").trim();
        String[] parts = input.split(",");
        List<Double> doubleList = new ArrayList<>();
        for (String part : parts) {
            doubleList.add(Double.parseDouble(part.trim()));
        }
        return doubleList;
    }


    // 날짜 리스트를 받으면, LocalDate의 문자열 반환   < 년 - 월 - 일 >
    public String localDateFormat(List<Double> dateList) {
        if (dateList == null) {
            // 적절한 처리를 합니다. 예를 들면, 빈 문자열을 반환하거나 예외를 던질 수 있습니다.
            return ""; // 또는 throw new IllegalArgumentException("list cannot be null");
        }

        String formatDate = null;
        if (dateList != null && dateList.size() == 3) {  // size 체크를 3으로 변경
            LocalDate date = LocalDate.of(
                    dateList.get(0).intValue(),
                    dateList.get(1).intValue(),
                    dateList.get(2).intValue());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formatDate = date.format(formatter);
        } else if (dateList.size() > 3) {
            LocalDate date = LocalDate.of(
                    dateList.get(0).intValue(),
                    dateList.get(1).intValue(),
                    dateList.get(2).intValue());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formatDate = date.format(formatter);
        } else {
            formatDate = "";
        }
        return formatDate;
    }


    public String getBasicInserted(List<Double> dateList) {
        String formatDateTime = null;
        if (dateList != null && dateList.size() == 6) {
            LocalDateTime dateTime = LocalDateTime.of(
                    dateList.get(0).intValue(),
                    dateList.get(1).intValue(),
                    dateList.get(2).intValue(),
                    dateList.get(3).intValue(),
                    dateList.get(4).intValue(),
                    dateList.get(5).intValue());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            formatDateTime = dateTime.format(formatter);
        } else {
            formatDateTime = "";
        }
        return formatDateTime;
    }



}
