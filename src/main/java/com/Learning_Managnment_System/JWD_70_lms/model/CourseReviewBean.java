package com.Learning_Managnment_System.JWD_70_lms.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter
@Entity
@Table(name = "course_reviews", schema = "lms_db")
public class CourseReviewBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    // Joins table automatically to pull data columns from your users schema
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment")
    private String comment;

    @Column(name = "is_visible", nullable = false)
    private Integer isVisible = 1; // 1 = Visible, 0 = Hidden

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

  
}
