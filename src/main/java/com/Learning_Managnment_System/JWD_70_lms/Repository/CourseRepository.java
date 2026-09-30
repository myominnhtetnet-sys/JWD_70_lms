package com.Learning_Managnment_System.JWD_70_lms.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

@Repository
public interface CourseRepository extends JpaRepository<CourseBean, Integer>, JpaSpecificationExecutor<CourseBean> {
    // Keep this interface empty! 
    // JpaRepository and JpaSpecificationExecutor give you all the CRUD and filtering methods out of the box.
}
