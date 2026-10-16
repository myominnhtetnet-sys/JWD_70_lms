package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CertificateRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CertificateBean;

@Service
public class CertificateService {
	
	private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

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

}
