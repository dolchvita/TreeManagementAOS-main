package com.snd.app.ui.write.hashtag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputLayout;
import com.snd.app.R;
import com.snd.app.common.TMDialogFragment;
import com.snd.app.databinding.RegistTreeHashtagDialogBinding;

import java.util.function.Consumer;

public class TreeHashtagDialogFragment extends TMDialogFragment {
    TreeHashtagDialogViewModel treeHashtagDialogVM;
    RegistTreeHashtagDialogBinding registTreeHashtagDialogBinding;
    private TreeHashtagDialogListener listener;
    ConstraintLayout hashtag_layout;
    AppCompatEditText hashtag;
    TextInputLayout hashtag_field;
    public String text;
    public String process;


    public interface TreeHashtagDialogListener {
        void onDialogPositiveClick(String hashtag, String method);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        treeHashtagDialogVM = new ViewModelProvider(this).get(TreeHashtagDialogViewModel.class);
        registTreeHashtagDialogBinding = DataBindingUtil.inflate(inflater, R.layout.regist_tree_hashtag_dialog, null, false);
        registTreeHashtagDialogBinding.setVm(treeHashtagDialogVM);
        registTreeHashtagDialogBinding.setLifecycleOwner(this);
        // 디자인 뷰 요소
        hashtag_layout = registTreeHashtagDialogBinding.hashtagLayout;
        hashtag = registTreeHashtagDialogBinding.hashtag;
        hashtag_field = registTreeHashtagDialogBinding.hashtagField;


        if(text != null){
            hashtag.setText(text);
        }


        hashtag.addTextChangedListener(createTextWatcher(s -> {
            treeHashtagDialogVM.treeHashtagDTO.setHashtag(s);
        }));


        treeHashtagDialogVM.isValidationCheck.observe(getActivity(), aBoolean -> {
            hashtag_field.setError(aBoolean ? null : "올바른 형식이 아닙니다.");
        });


        builder.setView(registTreeHashtagDialogBinding.getRoot())
                .setPositiveButton("확인", (dialog, which) -> {
                    if(listener != null){
                        listener.onDialogPositiveClick(hashtag.getText().toString(), (process.equals("append")) ? "append" : "update");
                    }
                })
                .setNegativeButton("취소", (dialog, which) -> {});

        return builder.create();
    }


    private TextWatcher createTextWatcher(final Consumer<String> onUpdate) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != null&&charSequence != ""){
                    if(charSequence != null && !charSequence.toString().isEmpty()) {
                        String text = charSequence.toString();
                        onUpdate.accept(text);
                        treeHashtagDialogVM.hashtagTextFormCheck();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }


    public void setTreeHashtagDialogListener(TreeHashtagDialogListener listener) {
        this.listener = listener;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        registTreeHashtagDialogBinding = null;
        hashtag_layout = null;
        hashtag_field = null;
        listener = null;
        hashtag = null;
    }


}
