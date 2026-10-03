package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.AssignmentRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    // Constructor Injection
    public AssignmentService(
            AssignmentRepository assignmentRepository) {

        this.assignmentRepository = assignmentRepository;
    }
  


    // =====================================
    // 1. CREATE ASSIGNMENT
    // =====================================

    public int createAssignment(AssignmentBean assignment) {

        // Title Validation
        if (assignment.getTitle() == null ||
                assignment.getTitle().isBlank()) {

            throw new IllegalArgumentException(
                    "Assignment title is required");
        }

        // Total Mark Validation
        if (assignment.getTotalMark() == null ||
                assignment.getTotalMark().signum() <= 0) {

            throw new IllegalArgumentException(
                    "Total mark must be greater than zero");
        }

        // Pass Mark Validation
        if (assignment.getPassMark() == null ||
                assignment.getPassMark().signum() < 0 ||
                assignment.getPassMark()
                        .compareTo(assignment.getTotalMark()) > 0) {

            throw new IllegalArgumentException(
                    "Invalid pass mark");
        }

        // Save to Database
        return assignmentRepository.save(assignment);
    }


    // =====================================
    // 2. GET ALL ASSIGNMENTS
    // =====================================

    public List<AssignmentBean> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    // =====================================
    // 3. GET ASSIGNMENT BY ID
    // =====================================

    public AssignmentBean getAssignmentById(Integer id) {

        return assignmentRepository.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Assignment not found: " + id
                    )
                );
    }


    // =====================================
    // 4. UPDATE ASSIGNMENT
    // =====================================

    public void updateAssignment(AssignmentBean assignment) {

        // Check whether assignment exists
        getAssignmentById(assignment.getAssignmentId());

        // Update
        int result = assignmentRepository.update(assignment);

        if (result == 0) {
            throw new IllegalArgumentException(
                    "Assignment update failed");
        }
    }


    // =====================================
    // 5. DELETE ASSIGNMENT
    // =====================================

    public void deleteAssignment(Integer id) {

        // Check whether assignment exists
        getAssignmentById(id);

        // Delete
        int result = assignmentRepository.deleteById(id);

        if (result == 0) {
            throw new IllegalArgumentException("Assignment delete failed");
        }
    }
}