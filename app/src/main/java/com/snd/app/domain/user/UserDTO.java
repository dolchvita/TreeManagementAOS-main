package com.snd.app.domain.user;

import java.util.List;


import lombok.Data;


@Data
public class UserDTO {
    private String id;
    private String password;
    private String name;
    private String company;
    private String position;
    private String phone;
    private String email;
    private String authority;
    private List<Double> inserted;
    private boolean certification;
}
