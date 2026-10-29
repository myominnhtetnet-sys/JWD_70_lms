package com.Learning_Managnment_System.JWD_70_lms.Service;



import com.Learning_Managnment_System.JWD_70_lms.Repository.ScholarshipRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.ScholarshipBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScholarshipService {

	@Autowired
     ScholarshipRepository scholarshipRepository;


    // ==========================================
    // SCHOLARSHIP METHODS
    // ==========================================

    public List<ScholarshipBean> getAllScholarships() {
        return scholarshipRepository.getAllScholarships();
    }

    public ScholarshipBean getScholarshipById(Integer id) {
        return scholarshipRepository.getScholarshipById(id);
    }

    public int createScholarship(ScholarshipBean scholarship) {
        return scholarshipRepository.createScholarship(scholarship);
    }

    public int updateScholarship(Integer id, ScholarshipBean scholarship) {
        return scholarshipRepository.updateScholarship(id, scholarship);
    }

    public int deleteScholarship(Integer id) {
        return scholarshipRepository.deleteScholarship(id);
    }
}