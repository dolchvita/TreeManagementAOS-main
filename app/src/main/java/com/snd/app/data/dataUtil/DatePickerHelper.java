package com.snd.app.data.dataUtil;

import android.app.DatePickerDialog;
import android.content.Context;

import java.util.Calendar;

public class DatePickerHelper {

    public interface OnDateSelectedListener {
        void onDateSelected(String selectedDate);
    }

    public static void showDatePickerDialog(Context context, final OnDateSelectedListener listener) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // 월과 일을 2자리 수로 포맷팅
                    String formattedMonth = String.format("%02d", monthOfYear + 1);
                    String formattedDay = String.format("%02d", dayOfMonth);

                    String selectedDate = year1 + "-" + formattedMonth + "-" + formattedDay;
                    if (listener != null) {
                        listener.onDateSelected(selectedDate);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }


}
