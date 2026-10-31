package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Learning_Managnment_System.JWD_70_lms.Repository.FinalGradeRepository;

@Service
public class FinalGradeService {

    // အဖွဲ့သတ်မှတ်ချက်ရလာရင် ဒီတန်ဖိုးတွေကို ပြောင်းနိုင်ပါတယ်
    private static final BigDecimal ASSIGNMENT_WEIGHT = new BigDecimal("0.30");
    private static final BigDecimal EXAM_WEIGHT = new BigDecimal("0.60");
    private static final BigDecimal ATTENDANCE_WEIGHT = new BigDecimal("0.10");
    private static final BigDecimal PASS_MARK = new BigDecimal("40");
    private final FinalGradeRepository repository;

    public FinalGradeService(FinalGradeRepository repository) {
        this.repository = repository;
    }

    public List<Map<String, Object>> getBatches(int teacherId) {
        return repository.findTeacherBatches(teacherId);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStudents( int batchId, int teacherId) {
        repository.requireBatch(batchId, teacherId, false);
        return repository.findStudents(batchId);
    }

    @Transactional
    public int calculate(int batchId, int teacherId) {
    	
        Map<String, Object> batch = repository.requireBatch(batchId, teacherId, true);
        List<Map<String, Object>> students = repository.findStudents(batchId);
        requireStudents(students);

        List<Map<String, Object>> assignments = repository.findAssignments(batchId);
        List<Map<String, Object>> exams = repository.findExams(batchId, number(batch.get("courseId")));
        checkAssessments(batchId, assignments, exams);

        int calculated = 0;

        for (Map<String, Object> student : students) {
            // ထုတ်ပြန်ပြီးသားအမှတ်ကို မပြောင်းပါ။
            if (student.get("publishedAt") != null) {
                continue;
            }

            int enrollmentId = number(student.get("enrollmentId"));
            Scores scores = compute(batchId, enrollmentId, assignments, exams);

            repository.saveDraft(
                    enrollmentId,
                    scores.assignment(),
                    scores.exam(),
                    scores.attendance(),
                    scores.total(),
                    scores.letter(),
                    scores.result(),
                    teacherId);

            calculated++;
        }

        if (calculated == 0) {
            throw new IllegalArgumentException("All eligible students already have published grades.");
        }
        return calculated;
    }

    @Transactional
    public int publish(int batchId, int teacherId) {
    	
        Map<String, Object> batch =repository.requireBatch(batchId, teacherId, true);
        List<Map<String, Object>> students = repository.findStudents(batchId);
        requireStudents(students);

        List<Map<String, Object>> assignments = repository.findAssignments(batchId);
        List<Map<String, Object>> exams = repository.findExams(batchId, number(batch.get("courseId")));
        checkAssessments(batchId, assignments, exams);

        int published = 0;

        for (Map<String, Object> student : students) {
            if (student.get("publishedAt") != null) {
                continue;
            }

            if (student.get("gradeId") == null) {
                throw new IllegalArgumentException("Please calculate all grades before publishing.");
            }

            int enrollmentId = number(student.get("enrollmentId"));
            // Calculate နောက်ပိုင်း အမှတ်ပြောင်းထားသလား ပြန်စစ်ပါ။
            Scores current = compute( batchId, enrollmentId, assignments, exams);

            boolean matches =
                    same(student.get("assignmentScore"), current.assignment())
                    && same(student.get("examScore"), current.exam())
                    && same(student.get("attendancePercent"), current.attendance())
                    && same(student.get("totalScore"), current.total())
                    && current.letter().equals(student.get("gradeLetter"))
                    && current.result().equals(student.get("result"));

            if (!matches) {
                throw new IllegalArgumentException("Source marks changed. Calculate again, review, then publish.");
            }

            if (repository.publish(enrollmentId, teacherId) != 1) {
                throw new IllegalArgumentException( "Unable to publish grade.");
            }
            published++;
        }

        if (published == 0) {
            throw new IllegalArgumentException( "No unpublished grades are available.");
        }
        return published;
    }


private Scores compute(
        int batchId,
        int enrollmentId,
        List<Map<String, Object>> assignments,
        List<Map<String, Object>> exams) {

    // =========================================
    // 1. ASSIGNMENT SCORE
    // =========================================
    BigDecimal assignmentEarned = BigDecimal.ZERO;
    BigDecimal assignmentMaximum = BigDecimal.ZERO;

    for (Map<String, Object> assignment : assignments) {

        int assignmentId = number(assignment.get("assignmentId"));
        BigDecimal maximum = decimal(assignment.get("totalMark"));

        requirePositive(maximum, "Assignment total mark");
        assignmentMaximum = assignmentMaximum.add(maximum);

        List<Map<String, Object>> submissions =
                repository.findSubmission(assignmentId, enrollmentId);

        // Submission မရှိလျှင် 0 mark
        if (submissions.isEmpty()) {
            continue;
        }

        Map<String, Object> submission = submissions.get(0);
        String status = String.valueOf(submission.get("status"));

        // Draft submission ကို မတွက်ပါ
        if ("DRAFT".equals(status)) {
            continue;
        }

        if (!"GRADED".equals(status) || submission.get("mark") == null) {
            throw new IllegalArgumentException(
                    "Enrollment " + enrollmentId
                    + ": assignment \"" + assignment.get("title")
                    + "\" is not fully graded.");
        }

        BigDecimal mark = decimal(submission.get("mark"));
        checkMark(mark, maximum, "Assignment mark");

        assignmentEarned = assignmentEarned.add(mark);
    }

    // =========================================
    // 2. EXAM SCORE - LATEST ATTEMPT ONLY
    // =========================================
    BigDecimal examEarned = BigDecimal.ZERO;
    BigDecimal examMaximum = BigDecimal.ZERO;

    for (Map<String, Object> exam : exams) {

        int examId = number(exam.get("examId"));
        BigDecimal maximum = decimal(exam.get("totalMark"));

        requirePositive(maximum, "Exam total mark");
        examMaximum = examMaximum.add(maximum);

		// Exam တစ်ခုစီအတွက် နောက်ဆုံး attempt တစ်ခုတည်းကိုယူပါ
		List<Map<String, Object>> attempts = repository.findLatestAttempt(examId, enrollmentId);

		// Attempt မရှိလျှင် 0 mark
		if (attempts.isEmpty()) {
			continue;
		}

		Map<String, Object> latestAttempt = attempts.get(0);
        String status = String.valueOf(latestAttempt.get("status"));

        // နောက်ဆုံး attempt က EXPIRED ဖြစ်လျှင် 0 mark
        // အရင် attempt ရမှတ်ကို ပြန်မယူပါ
        if ("EXPIRED".equals(status)) {
            continue;
        }

		// နောက်ဆုံး attempt ကို grade မပြီးသေးလျှင် မတွက်ပါ
		if (!"GRADED".equals(status) || latestAttempt.get("score") == null
				|| !List.of("PASSED", "FAILED").contains(String.valueOf(latestAttempt.get("result")))) {

			throw new IllegalArgumentException("Enrollment " + enrollmentId + ": latest attempt for exam \""
					+ exam.get("title") + "\" is not fully graded.");
		}

        BigDecimal latestScore = decimal(latestAttempt.get("score"));
        checkMark(latestScore, maximum, "Exam score");

        examEarned = examEarned.add(latestScore);
    }

    // =========================================
    // 3. ATTENDANCE SCORE
    // =========================================
    int attended = 0;
    int countedSessions = 0;

	for (Map<String, Object> attendance : repository.findAttendance(batchId, enrollmentId)) {

        String status = (String) attendance.get("status");

		if (status == null) {
			throw new IllegalArgumentException("Enrollment " + enrollmentId + ": attendance is missing for session "
					+ attendance.get("sessionId") + ".");
		}

		switch (status) {
		case "PRESENT", "LATE" -> {
			attended++;
			countedSessions++;
		}

            case "ABSENT" -> countedSessions++;

            // Excused session ကို denominator မှ ဖယ်ပါ
            case "EXCUSED" -> {
            }

            default -> throw new IllegalArgumentException(
                    "Invalid attendance status: " + status);
        }
    }

	if (countedSessions == 0) {
		throw new IllegalArgumentException(
				"Enrollment " + enrollmentId + ": no countable completed attendance sessions.");
	}

    // =========================================
    // 4. RAW PERCENTAGES
    // =========================================
	BigDecimal assignmentPercent = percentage(assignmentEarned, assignmentMaximum);
	BigDecimal examPercent = percentage(examEarned, examMaximum);
	BigDecimal attendancePercent = percentage(BigDecimal.valueOf(attended), BigDecimal.valueOf(countedSessions));

    // =========================================
    // 5. APPLY WEIGHTS
    // Assignment 30%, Exam 60%, Attendance 10%
    // =========================================
	BigDecimal assignmentContribution = assignmentPercent.multiply(ASSIGNMENT_WEIGHT);
	BigDecimal examContribution = examPercent.multiply(EXAM_WEIGHT);
	BigDecimal attendanceContribution = attendancePercent.multiply(ATTENDANCE_WEIGHT);

    BigDecimal total = assignmentContribution
            .add(examContribution)
            .add(attendanceContribution)
            .setScale(2, RoundingMode.HALF_UP);

    // =========================================
    // 6. FINAL RESULT
    // =========================================
    String result = total.compareTo(PASS_MARK) >= 0
            ? "PASSED"
            : "FAILED";

	return new Scores(assignmentPercent.setScale(2, RoundingMode.HALF_UP),
			examPercent.setScale(2, RoundingMode.HALF_UP), attendancePercent.setScale(2, RoundingMode.HALF_UP), total,
			gradeLetter(total), result);
}


    private void checkAssessments(int batchId,
            List<Map<String, Object>> assignments,
            List<Map<String, Object>> exams) {

        if (assignments.isEmpty() || exams.isEmpty()) {
            throw new IllegalArgumentException( "This grading policy requires at least one assignment and one exam.");
        }

        for (Map<String, Object> assignment : assignments) {
            if (!"CLOSED".equals(assignment.get("status"))) {
                throw new IllegalArgumentException(
                        "Close assignment \"" + assignment.get("title")+ "\" before calculating final grades.");
            }
        }

        for (Map<String, Object> exam : exams) {
            if (!"CLOSED".equals(exam.get("status"))) {
                throw new IllegalArgumentException(
                        "Close exam \"" + exam.get("title") + "\" before calculating final grades.");
            }
        }

        if (repository.countScheduledSessions(batchId) > 0) {
            throw new IllegalArgumentException( "Complete or cancel scheduled class sessions first.");
        }
    }

    private void requireStudents(List<Map<String, Object>> students) {
        if (students.isEmpty()) {
            throw new IllegalArgumentException("This batch has no ACTIVE or COMPLETED students.");
        }
    }

    private BigDecimal percentage(BigDecimal earned, BigDecimal maximum) {
        requirePositive(maximum, "Maximum total");
        return earned.multiply(new BigDecimal("100")).divide(maximum, 8, RoundingMode.HALF_UP);
    }

    private String gradeLetter(BigDecimal total) {
        if (total.compareTo(new BigDecimal("80")) >= 0) return "A";
        if (total.compareTo(new BigDecimal("70")) >= 0) return "B";
        if (total.compareTo(new BigDecimal("60")) >= 0) return "C";
        if (total.compareTo(PASS_MARK) >= 0) return "D";
        return "F";
    }

    private void requirePositive(BigDecimal value, String label) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(label + " must be greater than 0.");
        }
    }

	private void checkMark(BigDecimal mark, BigDecimal maximum, String label) {
		if (mark == null || maximum == null || mark.signum() < 0 || mark.compareTo(maximum) > 0) {
			throw new IllegalArgumentException(label + " must be between 0 and " + maximum + ".");
		}
	}


    private boolean same(Object saved, BigDecimal current) {
        return saved != null
                && decimal(saved).compareTo(current) == 0;
    }

    private BigDecimal decimal(Object value) {
        return value == null ? null : new BigDecimal(value.toString());
    }

    private int number(Object value) {
        return ((Number) value).intValue();
    }

    private record Scores(BigDecimal assignment,
            BigDecimal exam, BigDecimal attendance,
            BigDecimal total,
            String letter,String result) {
    }
}
