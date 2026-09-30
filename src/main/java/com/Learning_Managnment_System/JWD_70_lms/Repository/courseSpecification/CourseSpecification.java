package com.Learning_Managnment_System.JWD_70_lms.Repository.courseSpecification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import jakarta.persistence.criteria.Predicate;

public class CourseSpecification {

    public static Specification<CourseBean> filterBy(CourseBean filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Category ID Filter
            if (filter.getCategory_id() != null) {
                predicates.add(cb.equal(root.get("category_id"), filter.getCategory_id()));
            }
            
            // 2. Title Filter (Case-Insensitive wildcards)
            if (filter.getTitle() != null && !filter.getTitle().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + filter.getTitle().trim().toLowerCase() + "%"));
            }
            
            // 3. Level Filter
            if (filter.getLevel() != null && !filter.getLevel().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("level"), filter.getLevel().trim()));
            }
            
            // 4. Maximum Price Bound
            if (filter.getPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getPrice()));
            }
            
            // 5. Binary Integer Flags
            if (filter.getAllow_discount() != null) {
                predicates.add(cb.equal(root.get("allow_discount"), filter.getAllow_discount()));
            }
            if (filter.getAllow_installment() != null) {
                predicates.add(cb.equal(root.get("allow_installment"), filter.getAllow_installment()));
            }
            if (filter.getAllow_scholarship() != null) {
                predicates.add(cb.equal(root.get("allow_scholarship"), filter.getAllow_scholarship()));
            }

            // 6. Exclude Soft-Deleted Entries
            predicates.add(cb.isNull(root.get("deleted_at"))); 

            return cb.and(predicates.toArray(new Predicate[0]));

        };
    }
}
