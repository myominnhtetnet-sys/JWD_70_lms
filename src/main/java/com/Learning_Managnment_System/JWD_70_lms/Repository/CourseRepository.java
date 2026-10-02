package com.Learning_Managnment_System.JWD_70_lms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

@Repository
// Changing primary key type to Long to match your course_id type definition
public interface CourseRepository extends JpaRepository<CourseBean, Long>, JpaSpecificationExecutor<CourseBean> {
}
