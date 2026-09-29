package com.Learning_Managnment_System.JWD_70_lms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public boolean registerStudent(StudentBean student) {
        int rowsAffected = studentRepository.saveStudent(student);
        return rowsAffected > 0;
    }
}
