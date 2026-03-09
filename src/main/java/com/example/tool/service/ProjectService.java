package com.example.tool.service;

import com.example.tool.dto.Project;
import com.example.tool.mapper.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectMapper projectMapper;

    // 获取启用的项目（前台）
    public List<Project> getEnabledProjects() {
        return projectMapper.getEnabledProjects();
    }

    // 获取所有项目（后台）
    public List<Project> getAllProjects() {
        return projectMapper.getAllProjects();
    }

    // 保存项目（新增/更新）
    public void saveProject(Project project) {
        if (project.getId() == null || project.getId() == 0) {
            // 新增
            if (project.getSortOrder() == null) {
                project.setSortOrder(999);
            }
            if (project.getEnabled() == null) {
                project.setEnabled(true);
            }
            projectMapper.insertProject(project);
        } else {
            // 更新
            projectMapper.updateProject(project);
        }
    }

    // 删除项目
    public void deleteProject(Long id) {
        projectMapper.deleteProject(id);
    }
}
