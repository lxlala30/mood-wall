package com.example.tool.dto;

import lombok.Data;


/*
 * 标签类
 */
@Data
public class Note {

    private Integer id;

    // 标签内容
    private String content;

    private String color;

    private String createTime;

    // 新增：点赞数字段，默认0
    private Integer likeCount = 0;

    // 新增图片URL字段
    private String imageUrl;

    // 全参getter/setter

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}