package com.snd.app.data.dataUtil;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;


/* AppCompatEditText의 android:text 속성과 Double 값 사이의 변환을 관리 */

public class BindingAdapters {
    @BindingAdapter("android:text")
    public static void setDouble(AppCompatEditText view, Double value) {
        if (value != null) {
            view.setText(value.toString());
        } else {
            view.setText("");
        }
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static Double getDouble(AppCompatEditText view) {
        try {
            return Double.parseDouble(view.getText().toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
