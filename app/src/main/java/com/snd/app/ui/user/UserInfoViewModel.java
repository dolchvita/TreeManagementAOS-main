package com.snd.app.ui.user;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.snd.app.common.TMViewModel;
import com.snd.app.domain.user.UserDTO;
import com.snd.app.repository.user.UserUseCase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

public class UserInfoViewModel extends TMViewModel {
    public MutableLiveData<String> Authorization = new MutableLiveData<>();
    public MutableLiveData<String> id = new MutableLiveData<>();

    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> company = new ObservableField<>();
    public ObservableField<String> position = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>();
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> inserted = new ObservableField<>();
    public ObservableField<String> authority = new ObservableField<>();

    @Inject
    UserUseCase userUseCase;
    private UserDTO userDTO;


    public UserInfoViewModel() {
        userUseCase = getAppComponent().userUseCase();
    }


    // 유저회원 정보 by UserId
    public void loadUserDTO(){
        userUseCase.loadUserDTO(Authorization.getValue(), id.getValue());
    }
    public LiveData<UserDTO> getUserDTO(){
        return userUseCase.getUserDTO();
    }


    public void setTextViewByUserInfo(UserDTO userDTO){
        this.userDTO = userDTO;
        name.set(userDTO.getName());
        company.set(userDTO.getCompany());
        position.set(userDTO.getPosition());
        phone.set(userDTO.getPhone());
        email.set(userDTO.getEmail());
        inserted.set(getBasicInserted(userDTO.getInserted()));
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