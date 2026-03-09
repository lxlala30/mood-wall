package com.example.tool.controller;

import com.example.tool.dto.Project;
import com.example.tool.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    // 获取启用的项目（前台）
    @GetMapping("/list")
    public List<Project> getEnabledProjects() {
        List<Project> projects = projectService.getEnabledProjects();
        logger.info("getEnabledProjects() -> projects:{}", projects.size());
        return projectService.getEnabledProjects();
    }

    // 获取所有项目（后台）
    @GetMapping("/admin/list")
    public List<Project> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        logger.info("getAllProjects() -> projects:{}", projects.size());
        return projects;
    }

    // 保存项目（新增/更新）
    @PostMapping("/admin/save")
    public String saveProject(@RequestBody Project project) {
        try {
            logger.info("deleteProject() -> name:{}", project.getName());
            projectService.saveProject(project);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

    // 删除项目
    @DeleteMapping("/admin/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        try {
            logger.info("deleteProject() -> id:{}", id);
            projectService.deleteProject(id);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }
}