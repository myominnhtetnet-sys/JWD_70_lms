package com.Learning_Managnment_System.JWD_70_lms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.StudentDashboardRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentDashboardService {

	@Autowired
    StudentDashboardRepository dashboardRepository;

    public Map<String, Object> getStudentDashboardData(String email) {
        Map<String, Object> dataMap = new HashMap<>();
        
        // Initialize fallback zeros
        dataMap.put("enrolledCount", 0);
        dataMap.put("pendingCount", 0);
        dataMap.put("passedCount", 0);
        dataMap.put("certCount", 0);
        dataMap.put("activeCourses", List.of());

        if (email == null) {
            return dataMap;
        }

        Integer userId = dashboardRepository.findUserIdByEmail(email);
        if (userId != null) {
            dataMap.put("enrolledCount", dashboardRepository.getEnrolledCount(userId));
            dataMap.put("pendingCount", dashboardRepository.getPendingTasksCount(userId));
            dataMap.put("passedCount", dashboardRepository.getPassedExamsCount(userId));
            dataMap.put("certCount", dashboardRepository.getCertificatesCount(userId));
            dataMap.put("activeCourses", dashboardRepository.getActiveCourses(userId));
            dataMap.put("studentId", "STU-" + String.format("%03d", userId));
        }

        return dataMap;
    }
}
