package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserBean {

    private int userId;
    private int roleId;

    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;

    private LocalDate dob;
    private String gender;
    private String address;

    private String roleName;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}