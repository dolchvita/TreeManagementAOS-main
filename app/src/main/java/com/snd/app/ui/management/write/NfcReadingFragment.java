package com.snd.app.ui.management.write;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.ui.management.TreeManagementActivity;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NfcReadingFragment extends TMFragment {
    public NfcAdapter nfcAdapter;
    public PendingIntent nfcPendingIntent;
    Disposable disposable;
    SharedPreferenceManager sharedPreferencesManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nfc_write_fr, container, false);
        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LottieAnimationView animationView =view.findViewById(R.id.nfc_write_lottie);
        animationView.playAnimation();
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        initNfc();
    }



    private void initNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        nfcPendingIntent = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);

        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(getActivity(), new NfcAdapter.ReaderCallback() {
                @SuppressLint("CheckResult")
                @Override
                public void onTagDiscovered(Tag tag) {
                    disposable = Observable.just(tag)
                            .subscribeOn(Schedulers.io())
                            .map(new io.reactivex.functions.Function<Tag, String>() {
                                @Override
                                public String apply(Tag tag) throws Exception {
                                    byte[] id = tag.getId();
                                    String idHex = bytesToHexString(id).toUpperCase();
                                    Log.d(TAG, "** NFC 아이디 추출 ** " + id);
                                    Log.d(TAG, "** NFC 아이디 가공 ** " + idHex);

                                    saveIdHex(idHex);

                                    return "success";
                                }
                            })
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String result) throws Exception {
                                    ((TreeManagementActivity)getActivity()).switchFragment(7);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.e(TAG, "NFC processing error", throwable);
                                }
                            });
                }
            }, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
        }
    }



    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }



    public void saveIdHex(String idHex){
        Log.d(TAG, "saveIdHex " + idHex);

        sharedPreferencesManager.saveString("idHex", idHex);
        if (sharedPreferencesManager != null) {
            sharedPreferencesManager.saveString("idHex", idHex);
        }
        // 화면 전환
        ((TreeManagementActivity)getActivity()).switchFragment(7);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        nfcAdapter=null;
        nfcPendingIntent=null;
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }



}
