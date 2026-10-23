package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.BatchRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean.*;

@Service
public class BatchService {
	
	private final BatchRepository batchRepository;

    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public List<BatchInfo> getAllBatches() {
        return batchRepository.findAllBatches();
    }
    
	/*
	 * public List<BatchInfo> getAllActiveBatches() { return
	 * batchRepository.findAllBatches(); // (သို့) Active ဖြစ်တာတွေပဲ
	 * သီးသန့်ထုတ်ချင်ရင် repo မှာ query အသစ်ရေးနိုင်ပါတယ်၊ လက်ရှိမှာတော့
	 * getAllBatches() ကိုပဲ ပြန်ညွှန်းပေးလိုက်ပါတယ်) }
	 */
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