package com.example.tool.dto;

public class Project {
    private Long id;          // 主键
    private String name;      // 项目名称
    private String description; // 项目描述
    private String techStack; // 技术栈
    private String projectUrl; // 项目地址
    private Integer sortOrder; // 排序
    private Boolean enabled;  // 是否启用

    // Getters & Setters 完整实现
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }

    public String getProjectUrl() { return projectUrl; }
    public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}