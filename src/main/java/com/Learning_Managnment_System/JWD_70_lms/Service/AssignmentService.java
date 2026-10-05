package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.AssignmentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;

@Service
public class AssignmentService {
	private final AssignmentRepository assignmentRepository;

	// Constructor Injection
	public AssignmentService(AssignmentRepository assignmentRepository) {
		this.assignmentRepository = assignmentRepository;
	}

	// =====================================
	// VALIDATION
	// =====================================

	private void validateAssignment(AssignmentBean assignment) {

	    // 1. Batch Validation
	    if (assignment.getBatchId() == null) {
	        throw new IllegalArgumentException("Please select a batch");
	    }

	    // 2. Lesson Validation
	    if (assignment.getLessonId() == null) {
	        throw new IllegalArgumentException("Please select a lesson");
	    }

	    // 3. Title Validation
	    if (assignment.getTitle() == null
	            || assignment.getTitle().isBlank()) {

	        throw new IllegalArgumentException("Assignment title is required");
	    }

	    // 4. Start At Validation
	    if (assignment.getStartAt() == null) {
	        throw new IllegalArgumentException("Start date is required");
	    }

	    // 5. Due At Validation
	    if (assignment.getDueAt() == null) {
	        throw new IllegalArgumentException("Due date is required");
	    }

	    // 6. Date Comparison
	    if (!assignment.getDueAt().isAfter(assignment.getStartAt())) {
	        throw new IllegalArgumentException(
	                "Due date must be after start date");
	    }

	    // 7. Total Mark Validation
	    if (assignment.getTotalMark() == null
	            || assignment.getTotalMark().signum() <= 0) {

	        throw new IllegalArgumentException(
	                "Total mark must be greater than zero");
	    }

	    // 8. Pass Mark Validation
	    if (assignment.getPassMark() == null
	            || assignment.getPassMark().signum() < 0
	            || assignment.getPassMark()
	                    .compareTo(assignment.getTotalMark()) > 0) {

	        throw new IllegalArgumentException("Invalid pass mark");
	    }

	    // 9. Late Deadline Validation
	    if (Boolean.TRUE.equals(assignment.getAllowLateSubmit())) {

	        if (assignment.getLateDeadline() == null) {
	            throw new IllegalArgumentException(
	                    "Late deadline is required when late submission is allowed");
	        }

	        if (!assignment.getLateDeadline().isAfter(assignment.getDueAt())) {
	            throw new IllegalArgumentException(
	                    "Late deadline must be after due date");
	        }
	    }

	    // 10. Status Validation
	    if (assignment.getStatus() == null
	            || !List.of("DRAFT", "PUBLISHED", "CLOSED")
	                    .contains(assignment.getStatus())) {

	        throw new IllegalArgumentException("Invalid assignment status");
	    }
	}
	public int createAssignment(AssignmentBean assignment) {
		validateAssignment(assignment);
		return assignmentRepository.save(assignment);
	}
	
	
	
	
	
//    // =====================================
//    // 1. CREATE ASSIGNMENT
//    // =====================================
//
//    public int createAssignment(AssignmentBean assignment) {
//
//        // Title Validation
//        if (assignment.getTitle() == null || assignment.getTitle().isBlank()) {
//            throw new IllegalArgumentException("Assignment title is required");
//        }
//
//        // Total Mark Validation
//        if (assignment.getTotalMark() == null ||
//                assignment.getTotalMark().signum() <= 0) {
//
//            throw new IllegalArgumentException("Total mark must be greater than zero");
//        }
//
//        // Pass Mark Validation
//        if (assignment.getPassMark() == null ||
//                assignment.getPassMark().signum() < 0 ||
//                assignment.getPassMark()
//                        .compareTo(assignment.getTotalMark()) > 0) {
//
//            throw new IllegalArgumentException("Invalid pass mark");
//        }
//
//        // Save to Database
//        return assignmentRepository.save(assignment);
//    }
//

	// =====================================
	// 2. GET ALL ASSIGNMENTS
	// =====================================

	public List<AssignmentBean> getAllAssignments() {
		return assignmentRepository.findAll();
	}
	// =====================================
	// PAGINATION
	// =====================================

	public List<AssignmentBean> getAssignmentsByPage(
	        int page,
	        int size) {
	    return assignmentRepository.findAll(page, size);
	}

	public int getTotalAssignments() {

	    return assignmentRepository.countAll();
	}

	public List<AssignmentBean> searchAssignments(String title, Integer batchId, String status) {

		return assignmentRepository.search(title, batchId, status);
	}
	// =====================================
	// 3. GET ASSIGNMENT BY ID
	// =====================================

	public AssignmentBean getAssignmentById(Integer id) {

		return assignmentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + id));
	}

	// =====================================
	// 4. UPDATE ASSIGNMENT
	// =====================================

	public void updateAssignment(AssignmentBean assignment) {
	    getAssignmentById(assignment.getAssignmentId());
	    validateAssignment(assignment);
	    int result = assignmentRepository.update(assignment);
	    if (result == 0) {
	        throw new IllegalArgumentException("Assignment update failed");
	    }
	}

	// =====================================
	// 5. DELETE ASSIGNMENT
	// =====================================

	public void deleteAssignment(Integer id) {

	    // 1. Check assignment exists
	    getAssignmentById(id);

	    // 2. Check whether students have submitted
	    if (assignmentRepository.hasSubmissions(id)) {
	        throw new IllegalArgumentException(
	                "Cannot delete this assignment because students have submitted it."
	        );
	    }

	    // 3. Delete assignment
	    int result = assignmentRepository.deleteById(id);

	    if (result == 0) {
	        throw new IllegalArgumentException(
	                "Assignment delete failed"
	        );
	    }
	}	
}