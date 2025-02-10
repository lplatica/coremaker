package com.example.user.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class UserDto implements Serializable {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;

}
