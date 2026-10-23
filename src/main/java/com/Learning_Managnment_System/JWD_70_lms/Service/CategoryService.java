package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CategoryRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CategoryBean;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryBean> getAllCategories() {
        return categoryRepository.findAll();
    }

}
