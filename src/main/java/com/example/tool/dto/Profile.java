package com.example.tool.dto;

public class Profile {
    private Long id;
    private String name;
    private String avatarUrl;
    private String title;
    private String bio;
    private String skills; // 逗号分隔
    private String quickLinks; // 逗号分隔
    private String footerText;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getQuickLinks() { return quickLinks; }
    public void setQuickLinks(String quickLinks) { this.quickLinks = quickLinks; }

    public String getFooterText() { return footerText; }
    public void setFooterText(String footerText) { this.footerText = footerText; }
}
