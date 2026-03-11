package com.example.tool.controller;

import com.example.tool.dto.Profile;
import com.example.tool.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    // 获取个人信息（前台展示用）
    @GetMapping("/get")
    public Profile getProfile(@RequestParam Long id) {
        return profileService.getProfile(id);
    }

    // 新增：获取所有用户列表（用于下拉框）
    @GetMapping("/all/get")
    public List<Profile> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    // 新增/更新用户信息
    @PostMapping("/admin/update")
    public String saveProfile(@RequestBody Profile profile) {
        profileService.saveProfile(profile); // 新增：save 方法内部判断 id 是否为空
        return "success";
    }

    // 删除用户信息
    @DeleteMapping("/admin/delete/{id}")
    public String deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return "success";
    }
}
