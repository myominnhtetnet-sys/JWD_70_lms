package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.EnrollmentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.EnrollmentBean;
import com.Learning_Managnment_System.JWD_70_lms.model.EnrollmentDetailBean;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    // Enrollment Detail
    public EnrollmentDetailBean getEnrollmentDetailById(int id) {
        EnrollmentDetailBean detail = enrollmentRepo.getEnrollmentById(id);
        if (detail != null) {
            detail.setInstallments(enrollmentRepo.getInstallmentsByEnrollmentId(id));
            detail.setPayments(enrollmentRepo.getPaymentsByEnrollmentId(id));
        }
        return detail;
    }

    // Search Enrollments
    public List<EnrollmentBean> searchEnrollments(String keyword, String status, String paymentStatus) {
        return enrollmentRepo.searchEnrollments(keyword, status, paymentStatus);
    }

 // Dropdown Data & Statistics
    public List<Map<String, Object>> getStudents() {
        return enrollmentRepo.getStudents();
    }

    public List<Map<String, Object>> getBatches() {
        return enrollmentRepo.getBatches();
    }
    public int countEnrollments() {
        return enrollmentRepo.countEnrollments();
    }

    public int countActiveEnrollments() {
        return enrollmentRepo.countActiveEnrollments();
    }

    public int countPendingEnrollments() {
        return enrollmentRepo.countPendingEnrollments();
    }

    public BigDecimal totalEnrollmentAmount() {
        return enrollmentRepo.totalEnrollmentAmount();
    }

    // Business Logic - Create Enrollment
    public int createEnrollment(EnrollmentBean enrollment) {
        BigDecimal finalAmount = enrollment.getOriginal_amount()
                .subtract(enrollment.getDiscount_amount())
                .subtract(enrollment.getScholarship_amount());

        enrollment.setFinal_amount(finalAmount);
        return enrollmentRepo.createEnrollment(enrollment);
    }

    // Status Updates
    public int updateEnrollmentStatus(int enrollmentId, String status) {
        return enrollmentRepo.updateEnrollmentStatus(enrollmentId, status);
    }

    public int updatePaymentStatus(int enrollmentId, String paymentStatus) {
        return enrollmentRepo.updatePaymentStatus(enrollmentId, paymentStatus);
    }
}