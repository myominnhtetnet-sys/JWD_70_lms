package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CertificateRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;
import com.Learning_Managnment_System.JWD_70_lms.model.CertificateBean;

@Service
public class CertificateService {
	
	@Autowired
	private CertificateRepository certificateRepository;

    public List<CertificateBean> getAllCertificates() {
        return certificateRepository.findAllWithDetails();
    }

    public CertificateBean getCertificateById(Long id) {
        return certificateRepository.findById(id);
    }

    public boolean revokeCertificate(Long certificateId, String reason) {
        return certificateRepository.revokeCertificate(certificateId, reason) > 0;
    }

    public List<BatchBean.BatchInfo> getAllBatches() {
        return certificateRepository.findAllCompletedBatches();
    }

    public String issueCertificatesByBatch(Long batchId, Integer issuedBy) {
        if (batchId == null || batchId <= 0) {
            return "ERROR: Please select a valid training batch.";
        }
        int totalCompletedStudents = certificateRepository.countCompletedStudentsByBatchId(batchId);
        if (totalCompletedStudents == 0) {
            return "WARNING: Cannot issue certificates. There are no students with 'COMPLETED' status in this batch.";
        }

        int alreadyIssuedCount = certificateRepository.countIssuedCertificatesByBatchId(batchId);
        if (alreadyIssuedCount >= totalCompletedStudents) {
            return "WARNING: Duplicate action prevented. Certificates have already been issued for all completed students in this batch.";
        }
        int issuedCount = certificateRepository.issueCertificatesForBatch(batchId, issuedBy);

        if (issuedCount > 0) {
            return "SUCCESS: Successfully issued " + issuedCount + " certificate(s) for the selected batch!";
        } else {
            return "ERROR: Failed to issue certificates. Please try again.";
        }
    }
}