package com.example.tool.service;

import com.example.tool.dto.Profile;
import com.example.tool.mapper.ProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {
    @Autowired
    private ProfileMapper profileMapper;

    // 获取个人信息
    public Profile getProfile(Long userId) {
        return profileMapper.getProfile(userId);
    }

    // 获取所有用户列表
    public List<Profile> getAllProfiles() {
        return profileMapper.getAllProfiles();
    }

    // 更新个人信息
    public void updateProfile(Profile profile) {
        profileMapper.updateProfile(profile);
    }

    public void saveProfile(Profile profile) {
        if (profile.getId() == null) {
            // 新增：插入新用户
            profileMapper.insertProfile(profile);
        } else {
            // 更新：修改现有用户
            profileMapper.updateProfile(profile);
        }
    }

    public void deleteProfile(Long id) {
        profileMapper.deleteProfile(id);
    }
}
