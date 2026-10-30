//package com.Learning_Managnment_System.JWD_70_lms.model;
//
//
//import java.time.LocalDate;
//
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//public class AdminUserBean {
//
//	 private int user_id;
//	    private int role_id;
//	    private String role_name;
//	    private String full_name;
//	    private String email;
//	    private String phone;
//	    private String password_hash;
//	    private LocalDate dob;
//	    private String gender;
//	    private String address;
//	    private String status;
//}

package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class AdminUserBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    private int role_id;
    private String role_name;
    private String full_name;
    
    @Column(unique = true)
    private String email;
    
    private String phone;
    private String password_hash;
    private LocalDate dob;
    private String gender;
    private String address;
    
    // Status (ACTIVE / SUSPENDED)
    private String status = "ACTIVE"; 

    // Soft Delete အတွက် (Trash ထဲရောက်မရောက် - true ဆိုရင် ဖျက်ပြီးသား)
    @Column(name = "is_deleted")
    private boolean deleted = false;
}