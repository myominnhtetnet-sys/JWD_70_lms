package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.BatchDetailRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchDetailBean.*;

@Service
public class BatchDetailService {
	
	@Autowired
    private BatchDetailRepository batchDetailRepository;

    public List<BatchInfo> getAllBatches() {
        return batchDetailRepository.findAllBatches();
    }

    public List<StudentInfo> getBatchStudents(Long batchId) {
        return batchDetailRepository.findStudentsByBatchId(batchId);
    }

    public List<ClassSessionInfo> getBatchSessions(Long batchId) {
        return batchDetailRepository.findSessionsByBatchId(batchId);
    }

    public void saveClassSession(Long batchId, ClassSessionInfo sessionInfo) {
        batchDetailRepository.saveSession(batchId, sessionInfo);
    }

    public List<StudentProgress> getBatchStudentProgress(Long batchId) {
        return batchDetailRepository.findStudentProgressByBatchId(batchId);
    }

    public List<AnnouncementInfo> getBatchAnnouncements(Long batchId) {
        return batchDetailRepository.findAnnouncementsByBatchId(batchId);
    }
    
    public List<LessonInfo> getAllLessons() {
        return batchDetailRepository.findAllLessons();
    }
}