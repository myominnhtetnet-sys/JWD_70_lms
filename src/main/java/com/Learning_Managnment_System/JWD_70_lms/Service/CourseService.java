
package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Get all courses
    public List<CourseBean> getAllCourses() {
        return courseRepository.findAll();
    }

    // Get filtered courses with pagination
    public List<CourseBean> getFilteredCourses(
            CourseBean filters,
            String batchStatus,
            int page,
            int pageSize) {

        return courseRepository.findFilteredCourses(
                filters, batchStatus, page, pageSize);
    }
    
    public EnrollmentResult enrollStudentInCourse(int userId, int courseId) {
        try {
            // 1. Resolve Active Batch ID Mapped to Course Identifier
            List<Integer> batchIds = courseRepository.findActiveBatchIdByCourseId(courseId);
            if (batchIds.isEmpty()) {
                System.out.println("❌ ENROLLMENT FAILED: No OPEN or UPCOMING batch found for courseId: " + courseId);
                return new EnrollmentResult("NO_ACTIVE_BATCH", "Enrollment closed. No active open batch found for this course.", -1);
            }
            int batchId = batchIds.get(0);

            // 2. Prevent Duplicate Registration Overlaps
            if (courseRepository.checkDuplicateEnrollment(userId, batchId)) {
                System.out.println("ℹ️ ENROLLMENT NOTICE: Duplicate detected for userId: " + userId + ", batchId: " + batchId);
                return new EnrollmentResult("DUPLICATE", "You are already enrolled or have a pending checkout for this course!", batchId);
            }

            // 3. Resolve Pricing and Commit Entry Records
            Double coursePrice = courseRepository.getCoursePriceByCourseId(courseId);
            if (coursePrice == null) {
                coursePrice = 0.0;
            }

            System.out.println("📝 ATTEMPTING DB INSERT: userId=" + userId + ", batchId=" + batchId + ", price=" + coursePrice);
            int rows = courseRepository.insertEnrollment(userId, batchId, coursePrice);
            
            if (rows > 0) {
                System.out.println("✅ ENROLLMENT SUCCESS: Row written successfully to database!");
                return new EnrollmentResult("SUCCESS", "Enrollment initiated successfully! Please finalize tuition.", batchId);
            }

        } catch (Exception e) {
            // 🟢 CRUCIAL LOGGING: This prints out the exact SQL constraint violation directly into your terminal log stream
            System.err.println("❌ DATABASE WRITE CRASH -> " + e.getMessage());
            e.printStackTrace();
        }
        return new EnrollmentResult("FAILED", "Enrollment failed due to database layout constraints.", -1);
    }

    public static class EnrollmentResult {
        private final String status;
        private final String message;
        private final int targetBatchId;

        public EnrollmentResult(String status, String message, int targetBatchId) {
            this.status = status;
            this.message = message;
            this.targetBatchId = targetBatchId;
        }

        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public int getTargetBatchId() { return targetBatchId; }
    }

    // Get total pages for filtered courses
    public int getTotalPagesForFilters(
            CourseBean filters,
            String batchStatus,
            int pageSize) {

        if (pageSize <= 0) {
            throw new IllegalArgumentException(
                    "Page size must be greater than zero.");
        }

        int totalItems = courseRepository.countFilteredCourses(
                filters, batchStatus);

        return Math.max(1, (int) Math.ceil(
                (double) totalItems / pageSize));
    }

    // Get course by ID
    public CourseBean getCourseById(int courseId) {
        return courseRepository.findById(courseId);
    }

    // Get active and published courses
    public List<Map<String, Object>> getActivePublishedCourses() {
        return courseRepository.findActivePublishedCourses();
    }

    // Save course
    public void saveCourse(CourseBean course) {
        courseRepository.save(course);
    }

    // Update course
    public void updateCourse(CourseBean course) {
        courseRepository.update(course);
    }

    // Soft delete course
    public void deleteCourse(int courseId) {
        courseRepository.softDelete(courseId);
    }
}
