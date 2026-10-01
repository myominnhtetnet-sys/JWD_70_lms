package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import com.Learning_Managnment_System.JWD_70_lms.model.Review;

@Repository
public interface CourseRepository extends JpaRepository<CourseBean, Long>, JpaSpecificationExecutor<CourseBean> {

    // 1. Find a course by its unique string URL slug
    Optional<CourseBean> findBySlug(String slug);

    // 2. Fetch all reviews for a course (isolated manually to prevent JPA parser errors)
    @Query("SELECT r FROM Review r WHERE r.course.course_id = :courseId ORDER BY r.createdAt DESC")
    List<Review> findReviewsByCourseId(@Param("courseId") Long courseId);
}
