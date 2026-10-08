package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.AssignmentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;

@Service
public class AssignmentService {
	
	private final AssignmentRepository assignmentRepository;
	public AssignmentService(AssignmentRepository assignmentRepository) {
		this.assignmentRepository = assignmentRepository;
	}

	private void validateAssignment(AssignmentBean assignment) {

	    if (assignment.getBatchId() == null) {
	        throw new IllegalArgumentException("Please select a batch");
	    }

	    if (assignment.getLessonId() == null) {
	        throw new IllegalArgumentException("Please select a lesson");
	    }

	    if (assignment.getTitle() == null || assignment.getTitle().isBlank()) {
	        throw new IllegalArgumentException("Assignment title is required");
	    }

	    if (assignment.getStartAt() == null) {
	        throw new IllegalArgumentException("Start date is required");
	    }

	    if (assignment.getDueAt() == null) {
	        throw new IllegalArgumentException("Due date is required");
	    }

	    if (!assignment.getDueAt().isAfter(assignment.getStartAt())) {
	        throw new IllegalArgumentException( "Due date must be after start date");
	    }

	    if (assignment.getTotalMark() == null || assignment.getTotalMark().signum() <= 0) {
	        throw new IllegalArgumentException( "Total mark must be greater than zero");
	    }

	    if (assignment.getPassMark() == null || assignment.getPassMark().signum() < 0
	            || assignment.getPassMark() .compareTo(assignment.getTotalMark()) > 0) {
	        throw new IllegalArgumentException("Invalid pass mark");
	    }
	    
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

	    if (assignment.getStatus() == null || !List.of("DRAFT", "PUBLISHED", "CLOSED")
	                    .contains(assignment.getStatus())) {
	        throw new IllegalArgumentException("Invalid assignment status");
	    }
	}
	public int createAssignment(AssignmentBean assignment) {
		validateAssignment(assignment);
		return assignmentRepository.save(assignment);
	}
	public List<AssignmentBean> getAllAssignments() {
		return assignmentRepository.findAll();
	}
	
	public List<AssignmentBean> getAssignmentsByPage( int page,int size) {
	    return assignmentRepository.findAll(page, size);
	}

	public int getTotalAssignments() {
	    return assignmentRepository.countAll();
	}

	public List<AssignmentBean> searchAssignments(String title, Integer batchId, String status) {
		return assignmentRepository.search(title, batchId, status);
	}

	public AssignmentBean getAssignmentById(Integer id) {
		return assignmentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + id));
	}

	public void updateAssignment(AssignmentBean assignment) {
	    getAssignmentById(assignment.getAssignmentId());
	    validateAssignment(assignment);
	    int result = assignmentRepository.update(assignment);
	    if (result == 0) {
	        throw new IllegalArgumentException("Assignment update failed");
	    }
	}

	public void deleteAssignment(Integer id) {

	    getAssignmentById(id);
	    if (assignmentRepository.hasSubmissions(id)) {
	        throw new IllegalArgumentException(
	        		"Cannot delete this assignment because students have submitted it.");
	    }

	    int result = assignmentRepository.deleteById(id);
	    if (result == 0) {
	        throw new IllegalArgumentException("Assignment delete failed");
	    }
	}	
}
