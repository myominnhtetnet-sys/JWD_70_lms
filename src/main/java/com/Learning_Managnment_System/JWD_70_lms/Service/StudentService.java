package com.Learning_Managnment_System.JWD_70_lms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentBean;

@Service
public class StudentService {

	@Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    private StudentRepository studentRepository;

    public boolean registerStudent(StudentBean student) {
        // 1. Hash the plain-text password from the form
        String hashedPassword = passwordEncoder.encode(student.getPasswordHash());
        
        // 2. Set the hashed password back to the student bean
        student.setPasswordHash(hashedPassword);

        // 3. Save to database via repository
        int rowsAffected = studentRepository.createStudent(student);
        return rowsAffected > 0;
    }
}