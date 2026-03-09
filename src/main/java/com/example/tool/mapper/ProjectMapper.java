package com.example.tool.mapper;

import com.example.tool.dto.Project;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProjectMapper {
    // 获取所有启用的项目（前台展示）
    @Select("SELECT * FROM project WHERE enabled = 1 ORDER BY sort_order ASC")
    List<Project> getEnabledProjects();

    // 获取所有项目（后台管理）
    @Select("SELECT * FROM project ORDER BY sort_order ASC")
    List<Project> getAllProjects();

    // 新增项目
    @Insert("INSERT INTO project(name, description, tech_stack, project_url, sort_order, enabled) " +
            "VALUES(#{name}, #{description}, #{techStack}, #{projectUrl}, #{sortOrder}, #{enabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 自增主键返回
    int insertProject(Project project);

    // 更新项目
    @Update("UPDATE project SET " +
            "name = #{name}, " +
            "description = #{description}, " +
            "tech_stack = #{techStack}, " +
            "project_url = #{projectUrl}, " +
            "sort_order = #{sortOrder}, " +
            "enabled = #{enabled} " +
            "WHERE id = #{id}")
    int updateProject(Project project);

    // 删除项目
    @Delete("DELETE FROM project WHERE id = #{id}")
    int deleteProject(Long id);
}
