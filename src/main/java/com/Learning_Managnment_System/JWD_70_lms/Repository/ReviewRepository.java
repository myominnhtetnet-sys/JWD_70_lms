package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // By using explicit JPQL targeting r.course, we stop Spring's auto-parser from looking for "course" inside CourseBean
    @Query("SELECT r FROM Review r WHERE r.course.course_id = :courseId ORDER BY r.createdAt DESC")
    List<Review> findReviewsByCourseId(@Param("courseId") Long courseId);
}
