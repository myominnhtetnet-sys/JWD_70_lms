package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.LessonRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.LessonBean;

@Repository
public class LessonRepository {

    private final JdbcTemplate jdbcTemplate;
    private final LessonRowMapper lessonRowMapper;

    public LessonRepository(JdbcTemplate jdbcTemplate, LessonRowMapper lessonRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.lessonRowMapper = lessonRowMapper;
    }

    public List<LessonBean> findAll() {
        String sql = """
                SELECT lesson_id, title
                FROM lessons
                ORDER BY lesson_id
                """;

        return jdbcTemplate.query(sql, lessonRowMapper);
    }
    public Optional<LessonBean> findById(Integer id) {

        String sql = """
                SELECT lesson_id, title
                FROM lessons
                WHERE lesson_id = ?
                """;

        List<LessonBean> lessons =
                jdbcTemplate.query(sql, lessonRowMapper, id);

        return lessons.stream().findFirst();
    }
}