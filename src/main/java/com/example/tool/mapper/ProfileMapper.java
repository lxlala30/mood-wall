package com.example.tool.mapper;

import com.example.tool.dto.Profile;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProfileMapper {
    // 获取唯一的个人信息（只存一条）
//    @Select("SELECT * FROM profile LIMIT 1")
    @Select("SELECT * FROM profile WHERE id = #{id}  LIMIT 1")
    Profile getProfile(@Param("id") Long id);

    // 获取所有用户列表（只返回 id, name, title 即可）
    @Select("SELECT id, name, title FROM profile ORDER BY id ASC")
    List<Profile> getAllProfiles();

    // 更新个人信息
    @Update("UPDATE profile SET " +
            "name = #{name}, " +
            "avatar_url = #{avatarUrl}, " +
            "title = #{title}, " +
            "bio = #{bio}, " +
            "skills = #{skills}, " +
            "quick_links = #{quickLinks}, " +
            "footer_text = #{footerText}, " +
            "update_time = CURRENT_TIMESTAMP " +
            "WHERE id = #{id}")
    int updateProfile(Profile profile);

    // 插入新用户
    @Insert("INSERT INTO profile(name, avatar_url, title, bio, skills, quick_links, footer_text) " +
            "VALUES(#{name}, #{avatarUrl}, #{title}, #{bio}, #{skills}, #{quickLinks}, #{footerText})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertProfile(Profile profile);

    // 删除用户
    @Delete("DELETE FROM profile WHERE id = #{id}")
    int deleteProfile(@Param("id") Long id);
}
