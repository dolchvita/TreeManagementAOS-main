package com.snd.app.ui.write;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.snd.app.R;
import com.snd.app.common.TMFragment;
import com.snd.app.data.singleton.SharedPreferenceManager;
import com.snd.app.databinding.NfcWriteFrBinding;
import com.snd.app.repository.tree.TreeViewModel;

import java.io.IOException;
import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


// 등록 버튼 누르면 가장 먼저 동작하는 프레그먼트
public class NfcLoadingFragment extends TMFragment {
    NfcWriteFrBinding nfcWriteFrBinding;
    public NfcAdapter nfcAdapter;
    TreeViewModel treeVM;
    public PendingIntent nfcPendingIntent;
    SharedPreferenceManager sharedPreferencesManager;
    Disposable disposable;
    private Tag tag;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        nfcWriteFrBinding = DataBindingUtil.inflate(inflater, R.layout.nfc_write_fr, container, false);
        nfcWriteFrBinding.setLifecycleOwner(getActivity());
        treeVM = new ViewModelProvider(getActivity()).get(TreeViewModel.class);
        nfcWriteFrBinding.setTreeVM(treeVM);
        sharedPreferencesManager = SharedPreferenceManager.getInstance(getActivity().getApplicationContext());
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        nfcPendingIntent = PendingIntent.getActivity(getContext(), 0,
                new Intent(getContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
        return nfcWriteFrBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LottieAnimationView animationView = nfcWriteFrBinding.nfcWriteLottie;
        animationView.playAnimation();
    }


    @Override
    public void onResume() {
        super.onResume();
        initNfcAdapter();
    }


    void initNfcAdapter(){
        if (getActivity() != null && nfcAdapter != null) {
            Bundle options = new Bundle();
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);
            nfcAdapter.enableReaderMode(getActivity(),
                    tag -> {
                        disposable = Observable.just(tag)
                                .subscribeOn(Schedulers.io()) // 이 작업을 비동기적으로 수행합니다.
                                .map(new io.reactivex.functions.Function<Tag, String>() {
                                    @Override
                                    public String apply(Tag tag) throws Exception {
                                        NfcLoadingFragment.this.tag = tag;
                                        textForNdef();

                                        byte[] id = tag.getId();
                                        String idHex = bytesToHexString(id).toUpperCase();
                                        saveIdHex(idHex);

                                        return "success";
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread()) // 결과를 메인 쓰레드에서 받습니다.
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String result) throws Exception {
                                        Log.d(TAG, "**되나 보자 ** " + result);
                                        ((RegistTreeInfoActivity)getActivity()).initMapLoadingFr();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.e(TAG, "NFC processing error", throwable);
                                    }
                                });
                    },
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                    options);
        }
    }


    void textForNdef() {
        Ndef mNdef = Ndef.get(tag);
        if (mNdef!= null) {
            NdefRecord mRecord = NdefRecord.createTextRecord("kr", "이 칩은 여주시의 자산이므로 훼손 시 처벌받을 수 있습니다.");
            NdefMessage mMsg = new NdefMessage(mRecord);
            try {
                mNdef.connect();
                mNdef.writeNdefMessage(mMsg);
            } catch (FormatException e) {
                Log.d(TAG, "FormatException" + e.getMessage());
            } catch (TagLostException e) {
                Log.d(TAG, "TagLostException" + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "IOException 1 " + e.getMessage());
            } finally {
                try {
                    mNdef.close();
                } catch (IOException e) {
                    Log.d(TAG, "IOException 2 " + e.getMessage());
                }
            }
            try {
                // NFC 암호화
                transceiveToNfcA();
            }catch (IOException e){
                Log.d(TAG, "암호화 걸림" + e.getMessage());
            }
        }
    }


    // NFC 암호화
    public void transceiveToNfcA() throws IOException {
        NfcA nfcA = NfcA.get(tag);
        byte[] pwd = new byte[] { (byte)0x70, (byte)0x61, (byte)0x73, (byte)0x73 };  // p a s s
        byte[] pack = new byte[] { (byte)0x98, (byte)0x76 };
        if(nfcA!=null) {
            nfcA.connect();
            byte[] result = nfcA.transceive(new byte[] {
                    (byte)0xA2,  /* CMD = WRITE */
                    (byte)0x2C,  /* PAGE = 44 */
                    pack[0], pack[1], 0, 0
            });
            result = nfcA.transceive(new byte[] {
                    (byte)0xA2,  /* CMD = WRITE */
                    (byte)0x2B,  /* PAGE = 43 */
                    pwd[0], pwd[1], pwd[2], pwd[3]
            });
            nfcA.transceive(new byte[] {
                    (byte)0xA2,  /* CMD = WRITE */
                    (byte)0x29,  /* PAGE = 41 */
                    (byte)0x04, 0, 0, 0
            });
            nfcA.close();
        }
    }


    public void saveIdHex(String idHex){
        Log.d(TAG, "saveIdHex " + idHex);
        if (sharedPreferencesManager != null) {
            sharedPreferencesManager.saveString("idHex", idHex);
        }
        // 화면 전환
        ((RegistTreeInfoActivity)getActivity()).initMapLoadingFr();
    }


    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    // NFC 복호화
    private void unlockTag(Tag tag) throws IOException {
        NfcA nfcA = NfcA.get(tag);
        byte[] pwd = new byte[] { (byte)0x70, (byte)0x61, (byte)0x73, (byte)0x73 };  // p a s s

        if (nfcA != null) {
            nfcA.connect();
            // PWD 인증
            byte[] pack = new byte[] { (byte)0x98, (byte)0x76 };
            byte[] authCommand = new byte[] { (byte)0x1B, pwd[0], pwd[1], pwd[2], pwd[3] };  // (0x1B -> 비밀번호 검증 명령어)
            byte[] response = nfcA.transceive(authCommand);  // -> 이게 암호검증 로직 거치고 pack 반환

            if (Arrays.equals(pack, response)) {
                // 인증 성공
                // 비밀번호 초기화 (빈값으로)
                byte[] resultRestPassword = nfcA.transceive(new byte[] {
                        (byte)0xA2,  /* CMD = WRITE */
                        (byte)0x2B,  /* PAGE = 43 */
                        (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF
                });
                // pack 초기화 (빈값으로, 뒤에 2자리는 rufi 였나 뭔가임)
                byte[] resultResetPack = nfcA.transceive(new byte[] {
                        (byte)0xA2,  /* CMD = WRITE */
                        (byte)0x2C,  /* PAGE = 44 */
                        (byte)0xFF, (byte)0xFF, 0, 0
                });
                // auth0 초기화
                byte[] resultAuth0 = nfcA.transceive(new byte[] {
                        (byte)0xA2,  /* CMD = WRITE */
                        (byte)0x29,  /* PAGE = 41 */
                        (byte)0x04, 0, 0, (byte)0xFF
                });
            }
            nfcA.close();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        // nfcAdapter 는 onPause에서만 해제된다.
        if(nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(getActivity());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        nfcWriteFrBinding = null;
        sharedPreferencesManager = null;
        nfcPendingIntent = null;
        if(disposable != null&& !disposable.isDisposed()){
            disposable.dispose();
        }
        tag = null;
    }


}
