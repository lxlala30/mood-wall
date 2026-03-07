package com.example.tool.controller;

import cn.hutool.core.date.DateUtil;
import com.example.tool.dto.Note;
import com.example.tool.util.NotePersistenceUtil;
import com.example.tool.util.OnlineUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/note")
@CrossOrigin
public class NoteController {

    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    private final List<Note> noteList = new ArrayList<>();
    private final AtomicInteger idGen = new AtomicInteger(1);
    // 注入在线用户管理器
    private final OnlineUserManager onlineUserManager;

    private final NotePersistenceUtil persistenceUtil;

    public NoteController(OnlineUserManager onlineUserManager, NotePersistenceUtil persistenceUtil) {
        this.onlineUserManager = onlineUserManager;
        this.persistenceUtil = persistenceUtil; // 实例化后赋值
    }

    /**
     * 初始化：从文件加载数据，若为空则创建默认便签
     */
    @PostConstruct
    public void init() {
        // 1. 从文件加载持久化的便签数据
        List<Note> savedNotes = persistenceUtil.loadNotes();
        noteList.addAll(savedNotes);

        // 2. 初始化ID生成器（避免ID重复）
        int maxId = NotePersistenceUtil.getMaxNoteId(savedNotes);
        idGen.set(maxId + 1);
        logger.info("init() -> ID生成器初始化值：{}", maxId + 1);

        // 3. 如果加载的数据为空，创建默认便签
        if (noteList.isEmpty()) {
            Note note = new Note();
            note.setId(idGen.getAndIncrement());
            note.setContent("欢迎来到 MoodWall，写下你的心情～😜");
            note.setColor("#ffeaa7");
            note.setCreateTime(DateUtil.now());
            note.setLikeCount(0);
            noteList.add(note);
            // 保存默认便签到文件
            persistenceUtil.saveNotes(noteList);
            logger.info("init() -> 初始化默认便签");
        }
    }

    /**
     * 获取便签列表（同时记录用户访问）
     */
    @GetMapping("/list")
    public List<Note> list(HttpServletRequest request) {
        onlineUserManager.recordUserAccess(request);
        return noteList;
    }

    /**
     * 新增便签（同时记录用户访问 + 保存到文件）
     */
    @PostMapping("/add")
    public List<Note> add(@RequestBody Note note, HttpServletRequest request) {
        onlineUserManager.recordUserAccess(request);

        note.setId(idGen.getAndIncrement());
        note.setCreateTime(DateUtil.now());
        if (note.getLikeCount() == null) {
            note.setLikeCount(0);
        }
        noteList.add(note);
        logger.info("add() -> 新增便签content:{}，imageUrl:{}", note.getContent(), note.getImageUrl());

        // 新增后保存到文件
        persistenceUtil.saveNotes(noteList);
        return noteList;
    }

    /**
     * 删除便签（同时记录用户访问 + 保存到文件）
     */
    @DeleteMapping("/delete/{id}")
    public List<Note> delete(@PathVariable Integer id, HttpServletRequest request) {
        onlineUserManager.recordUserAccess(request);

        logger.info("delete() -> 待删除的便签id:{}", id);
        noteList.removeIf(note -> note.getId().equals(id));
        logger.info("delete() -> 剩余便签数量:{}", noteList.size());

        // 删除后保存到文件
        persistenceUtil.saveNotes(noteList);
        return noteList;
    }

    /**
     * 点赞接口（同时记录用户访问 + 保存到文件）
     */
    @PostMapping("/like/{id}")
    public List<Note> like(@PathVariable Integer id, HttpServletRequest request) {
        onlineUserManager.recordUserAccess(request);

        noteList.stream()
                .filter(note -> note.getId().equals(id))
                .findFirst()
                .ifPresent(note -> {
                    int newLikeCount = note.getLikeCount() + 1;
                    note.setLikeCount(newLikeCount);
                    logger.info("like() -> 便签id:{} 点赞数更新为:{}", id, newLikeCount);
                });

        // 点赞后保存到文件
        persistenceUtil.saveNotes(noteList);
        return noteList;
    }

    /**
     * 获取在线人数和IP列表
     */
    @GetMapping("/online")
    public Map<String, Object> getOnlineInfo(HttpServletRequest request) {
        onlineUserManager.recordUserAccess(request);

        Map<String, Object> result = new HashMap<>();
        result.put("onlineCount", onlineUserManager.getOnlineCount());
        result.put("ipList", onlineUserManager.getOnlineIpList());
        result.put("currentIp", onlineUserManager.getIpAddress(request));
        return result;
    }

    /**
     * 图片上传接口
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadImage(@RequestParam("image") MultipartFile file, HttpServletRequest request) {
        onlineUserManager.recordUserAccess(request);
        Map<String, Object> result = new HashMap<>();

        // 1. 校验文件
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "上传文件为空");
            return result;
        }

        try {
            // 2. 定义上传目录（项目根目录下的 uploads 文件夹）
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 创建目录
            }

            // 3. 生成唯一文件名（避免重复）
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 4. 保存文件
            File destFile = new File(uploadDir + newFileName);
            file.transferTo(destFile);

            // 5. 构建图片访问URL（适配本地/服务器环境）
            String serverIp = request.getServerName();
            int port = request.getServerPort();
            String imageUrl = String.format("http://%s:%d/uploads/%s", serverIp, port, newFileName);

            // 6. 返回结果
            result.put("success", true);
            result.put("imageUrl", imageUrl);
            result.put("message", "上传成功");
            logger.info("uploadImage() -> 图片上传成功，URL:{}", imageUrl);
            return result;

        } catch (Exception e) {
            logger.error("图片上传失败", e);
            result.put("success", false);
            result.put("message", "上传失败：" + e.getMessage());
            return result;
        }
    }
}