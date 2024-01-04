package com.snd.app.data.dataUtil;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.function.Consumer;

public class TextManager {
    public static String TAG = "TextManager";

    public static TextWatcher createTextWatcher(final Consumer<String> onUpdate) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence!=null&&charSequence!=""){
                    String text = charSequence.toString();
                    onUpdate.accept(text);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }


    public void addTextWatcherWithValidation(EditText editText, Consumer<Double> action) {
        editText.addTextChangedListener(createTextWatcher(text -> {
            if (!text.isEmpty()) {
                try {
                    double value = Double.parseDouble(text);
                    action.accept(value);
                } catch (NumberFormatException e) {
                    // handle the exception or ignore
                }
            }
        }));
    }



}
