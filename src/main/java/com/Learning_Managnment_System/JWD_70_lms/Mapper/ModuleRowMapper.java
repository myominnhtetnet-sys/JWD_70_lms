package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.ModuleBean;

public class ModuleRowMapper implements RowMapper<ModuleBean>{

	@Override
	public ModuleBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		ModuleBean module = new ModuleBean();
        module.setModuleId(rs.getInt("module_id"));
        module.setCourseId(rs.getInt("course_id"));
        module.setTitle(rs.getString("title"));
        return module;
	}

}
