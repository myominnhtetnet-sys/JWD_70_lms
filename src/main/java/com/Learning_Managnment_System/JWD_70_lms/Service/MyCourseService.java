package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.MyCourseRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.LessonProgressBean;
import com.Learning_Managnment_System.JWD_70_lms.model.MyCourseBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCourseService {

    @Autowired
    private MyCourseRepository myCourseRepository;

    /**
     * 🟢 THE HYBRID CONTAINER ENGINE
     * Implements a custom map that intercepts bean getter calls dynamically 
     * so both .lessonTitle and .containsKey('file_path') execute without throwing exceptions.
     */
    public static class HybridTemplateModel extends HashMap<String, Object> {
        public HybridTemplateModel(Map<String, Object> sourceMap) {
            if (sourceMap != null) {
                putAll(sourceMap);
            }
        }
        
        // Dynamic interception map layer hooks for all Thymeleaf variable lookups:
        public Object getLessonId() { return get("lessonid"); }
        public Object getLessonTitle() { return get("lessontitle"); }
        public Object getContentType() { return get("contenttype"); }
        public Object getDurationMin() { return get("durationmin"); }
        public Object getVideoUrl() { return get("videourl"); }
        public Object getContent() { return get("content"); }
        public Object getModuleTitle() { return get("moduletitle"); }
        public Object getCourseId() { return get("courseid"); }
        public Object getCourseTitle() { return get("coursetitle"); }
        public boolean isDone() { return Boolean.TRUE.equals(get("done")); }
    }

    public List<Map<String, Object>> getEnrolledCoursesProgressByEmail(String email) {
        return myCourseRepository.findEnrolledCoursesProgressByEmail(email);
    }

    public MyCourseBean getCourseMetaDetails(int courseId, String email) {
        return myCourseRepository.findCourseMetaDetails(courseId, email);
    }

    /**
     * 🟢 3. FETCH INDIVIDUAL CLASSROOM TIMELINE SYLLABUS LIST
     * ⚡ FIXED: Now forwards the logged-in student's email to isolate row checklist metrics.
     */
    public List<LessonProgressBean> getCourseLessonTimeline(int courseId, String batchCode, String email) {
        return myCourseRepository.findCourseLessonTimeline(courseId, batchCode, email);
    }

    public Map<String, Object> getActiveLessonDetails(int lessonId, int courseId, String email) {
        Map<String, Object> dbResult = myCourseRepository.findActiveLessonCoreMap(lessonId);
        Map<String, Object> lowerMap = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : dbResult.entrySet()) {
            lowerMap.put(entry.getKey().toLowerCase(), entry.getValue());
            lowerMap.put(entry.getKey(), entry.getValue());
        }

        try {
            Map<String, Object> extras = myCourseRepository.findLessonExtraFields(lessonId);
            lowerMap.put("videoUrl", extras.get("video_url"));
            lowerMap.put("videourl", extras.get("video_url"));
            lowerMap.put("content", extras.get("content"));
        } catch (Exception e) {
            lowerMap.put("videoUrl", null);
            lowerMap.put("content", "Text content body unassigned for this lesson chapter.");
        }

        String lessonTitle = (String) lowerMap.get("lessontitle");
        Integer doneCount = myCourseRepository.checkIsLessonDone(courseId, lessonId, lessonTitle, email);
        boolean isDoneStatus = (doneCount != null && doneCount > 0);
        
        lowerMap.put("done", isDoneStatus);
        
        return new HybridTemplateModel(lowerMap);
    }

    public Map<String, Object> getCourseWorkspaceMetadata(String email, int courseId) {
        Map<String, Object> dbResult = myCourseRepository.findCourseWorkspaceMetadata(email, courseId);
        Map<String, Object> lowerMap = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : dbResult.entrySet()) {
            lowerMap.put(entry.getKey().toLowerCase(), entry.getValue());
            lowerMap.put(entry.getKey(), entry.getValue());
        }
        return new HybridTemplateModel(lowerMap);
    }

    public List<LessonProgressBean> getSidebarLessons(String email, int courseId) {
        return myCourseRepository.findSidebarLessons(email, courseId);
    }

    public int getVerifiedBatchId(String email, int courseId) {
        return myCourseRepository.findActiveBatchId(email, courseId)
                .orElseThrow(() -> new IllegalArgumentException("No active enrollment found for user: " + email));
    }

    @Transactional
    public void markLessonAsCompleted(int batchId, int lessonId, String studentEmail) {
        String plainLessonTitle;
        try {
            plainLessonTitle = myCourseRepository.findLessonTitleById(lessonId);
        } catch (Exception e) {
            plainLessonTitle = "Completed Session";
        }
        String isolatedSessionTitle = plainLessonTitle + " [" + studentEmail + "]";
        myCourseRepository.insertClassSession(batchId, lessonId, isolatedSessionTitle);
    }

    @Transactional
    public void regressLessonProgress(int batchId, int lessonId, String studentEmail) {
        String plainLessonTitle;
        try {
            plainLessonTitle = myCourseRepository.findLessonTitleById(lessonId);
        } catch (Exception e) {
            plainLessonTitle = "";
        }
        String isolatedSessionTitle = plainLessonTitle + " [" + studentEmail + "]";
        myCourseRepository.deleteClassSession(batchId, lessonId, isolatedSessionTitle);
    }

    @Transactional
    public boolean finalizeCourseManualCheckout(String email, int courseId) {
        return myCourseRepository.updateEnrollmentToCompleted(email, courseId);
    }
}
