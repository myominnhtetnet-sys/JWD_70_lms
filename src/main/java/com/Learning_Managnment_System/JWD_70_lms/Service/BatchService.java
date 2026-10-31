package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.BatchRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean.*;

@Service
public class BatchService {
	
	@Autowired
	private BatchRepository batchRepository;

    public List<BatchInfo> getAllBatches() {
        return batchRepository.findAllBatches();
    }

    public BatchInfo getBatchById(Long batchId) {
        return batchRepository.findBatchById(batchId);
    }

    public void createBatch(BatchInfo batchInfo) {
        batchRepository.saveBatch(batchInfo);
    }

    public void updateBatch(Long batchId, BatchInfo batchInfo) {
        batchRepository.updateBatch(batchId, batchInfo);
    }

    public void deleteBatch(Long batchId) {
        if (batchRepository.hasEnrolledStudents(batchId)) {
            throw new RuntimeException("Student is attended that batch.");
        }
        batchRepository.deleteBatch(batchId);
    }

    public void assignInstructor(Long batchId, Long instructorId) {
        batchRepository.assignInstructor(batchId, instructorId);
    }
}