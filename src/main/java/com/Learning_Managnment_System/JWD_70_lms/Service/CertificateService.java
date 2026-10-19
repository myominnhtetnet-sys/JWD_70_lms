package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CertificateRepository;
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

    public boolean issueCertificate(CertificateBean cert) {
        return certificateRepository.save(cert) > 0;
    }

    public boolean revokeCertificate(Long certificateId, String reason) {
        return certificateRepository.revokeCertificate(certificateId, reason) > 0;
    }

    // 🟢 ကျောင်းသားများ၏ Enrollment, Course နှင့် Batch အချက်အလက်များကို Modal Dropdown အတွက် ဆွဲထုတ်ပေးခြင်း
    public List<CertificateBean> getAllEligibleEnrollmentsForCertificate() {
        return certificateRepository.findAllEnrollmentsWithStudentAndCourse();
    }

}