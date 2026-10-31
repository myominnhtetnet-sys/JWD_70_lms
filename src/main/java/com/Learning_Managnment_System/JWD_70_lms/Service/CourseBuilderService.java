package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseBuilderRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBuilderBean;
import com.Learning_Managnment_System.JWD_70_lms.model.InstructorBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ModuleBean;
import com.Learning_Managnment_System.JWD_70_lms.model.LessonBean;
import com.Learning_Managnment_System.JWD_70_lms.model.MaterialBean;

@Service
public class CourseBuilderService {
	
	@Autowired
	private CourseBuilderRepository courseRepository;

    public List<CourseBuilderBean> getAllCourses() {
        return courseRepository.findAllCourses();
    }

    public CourseBuilderBean getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    // 🟢 Instructor စာရင်းကို List<InstructorBean> ဖြင့် ပြန်ပေးရန်
    public List<InstructorBean> getAllInstructors() {
        return courseRepository.findAllInstructors();
    }

    public void createCourse(CourseBuilderBean course, int currentUserId) {
        courseRepository.save(course, currentUserId);
    }

    public void updateCourse(Long courseId, CourseBuilderBean course) {
        courseRepository.update(courseId, course);
    }

    public void deleteCourse(Long courseId) {
        courseRepository.delete(courseId);
    }

    // --- 1. COURSE INSTRUCTORS SERVICES ---
    public List<InstructorBean> getCourseInstructors(Long courseId) {
        return courseRepository.findInstructorsByCourseId(courseId);
    }

    public void assignInstructor(Long courseId, Long instructorId) {
        courseRepository.saveInstructorAssignment(courseId, instructorId);
    }

    public void removeInstructor(Long courseId, Long instructorId) {
        courseRepository.deleteInstructorAssignment(courseId, instructorId);
    }

    // --- 2. COURSE MODULES SERVICES ---
    public List<ModuleBean> getModules(Long courseId) {
        return courseRepository.findModulesByCourseId(courseId);
    }

    public void createModule(Long courseId, String title) {
        courseRepository.saveModule(courseId, title);
    }

    public void deleteModule(Long moduleId) {
        courseRepository.deleteModule(moduleId);
    }

    // --- 3. LESSONS SERVICES ---
    public List<LessonBean> getLessons(Long courseId) {
        return courseRepository.findLessonsByCourseId(courseId);
    }

    public void createLesson(Long courseId, String title, String videoUrl) {
        courseRepository.saveLesson(courseId, title, videoUrl);
    }

    public void deleteLesson(Long lessonId) {
        courseRepository.deleteLesson(lessonId);
    }

    // --- 4. MATERIALS SERVICES ---
    public List<MaterialBean> getMaterials(Long courseId) {
        return courseRepository.findMaterialsByCourseId(courseId);
    }

    public void createMaterial(Long courseId, String title, String filePath) {
        courseRepository.saveMaterial(courseId, title, filePath);
    }

    public void deleteMaterial(Long materialId) {
        courseRepository.deleteMaterial(materialId);
    }
}