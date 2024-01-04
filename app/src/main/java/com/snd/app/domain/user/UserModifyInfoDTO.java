package com.snd.app.domain.user;

import lombok.Data;

@Data
public class UserModifyInfoDTO extends UserDTO{
    private boolean isChangingPassword;
}
