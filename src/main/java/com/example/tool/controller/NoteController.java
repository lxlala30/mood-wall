package com.example.tool.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONUtil;
import com.example.tool.dto.Note;
import com.example.tool.util.FileMd5Util;
import com.example.tool.util.NotePersistenceUtil;
import com.example.tool.util.OnlineUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.concurrent.ConcurrentHashMap;
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
    // 便签数据文件持久化工具类
    private final NotePersistenceUtil persistenceUtil;

    // ========== 新增：图片MD5映射（内存+文件持久化） ==========
    // 内存中存储：MD5 -> 图片URL（线程安全）
    private final Map<String, String> imageMd5Map = new ConcurrentHashMap<>();
    // MD5映射文件路径（和便签数据同目录）
    private String md5MapFilePath;

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
        // 初始化MD5映射
        initImageMd5Map();
    }

    // ========== 新增：初始化MD5映射（从文件加载） ==========
    public void initImageMd5Map() {
        // 初始化MD5映射文件路径（存储在便签数据目录下）
        md5MapFilePath = persistenceUtil.getDir() + "/image_md5_map.json";
        File md5File = new File(md5MapFilePath);

        logger.info("initImageMd5Map() -> path:{}", md5MapFilePath);

        // 如果文件存在，加载已有的MD5映射
        if (md5File.exists()) {
            try {
                String jsonStr = FileReader.create(md5File).readString();
                Map<String, String> savedMap = JSONUtil.toBean(jsonStr, Map.class);
                imageMd5Map.putAll(savedMap);
                logger.info("initImageMd5Map() -> 加载 {} 个已上传图片的MD5映射", imageMd5Map.size());
            } catch (Exception e) {
                logger.error("加载图片MD5映射失败", e);
            }
        }
    }

    // ========== 新增：保存MD5映射到文件（持久化） ==========
    private void saveImageMd5Map() {
        try {
            String jsonStr = JSONUtil.toJsonPrettyStr(imageMd5Map);
            FileWriter.create(new File(md5MapFilePath)).write(jsonStr);
        } catch (Exception e) {
            logger.error("保存图片MD5映射失败", e);
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
        noteList.forEach(note -> {
            if (note.getId().equals(id)) {
                logger.info("delete() -> 待删除的便签value:{}", note.getContent());
            }
        });
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

    // ========== 改造后的上传接口 ==========
    @PostMapping("/upload")
    public Map<String, Object> uploadImage(@RequestParam("image") MultipartFile file, HttpServletRequest request) {
        onlineUserManager.recordUserAccess(request);
        Map<String, Object> result = new HashMap<>();

        // 1. 基础校验
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "上传文件为空");
            return result;
        }

        try {
            // 2. 计算文件MD5（核心：唯一标识文件内容）
            String fileMd5 = FileMd5Util.getFileMd5(file);
            logger.info("uploadImage() -> 待上传文件MD5：{}", fileMd5);

            // 3. 校验是否已存在（MD5已在映射中）
            if (imageMd5Map.containsKey(fileMd5)) {
                String existUrl = imageMd5Map.get(fileMd5);
                result.put("success", true);
                result.put("imageUrl", existUrl);
                result.put("message", "图片已存在，无需重复上传");
                result.put("isDuplicate", true); // 标记为重复文件
                logger.info("uploadImage() -> 图片已存在，URL：{}", existUrl);
                return result;
            }

            // 4. 定义上传目录（改为配置化路径，避免硬编码）
            String uploadDir = persistenceUtil.getDir() + "uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 创建目录（自动创建多级）
            }

            // 5. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 6. 保存文件
            File destFile = new File(uploadDir + newFileName);
            file.transferTo(destFile);

            // 7. 构建访问URL（适配服务器IP）
            // 优先用服务器公网IP（避免localhost/127.0.0.1）
            String serverIp = request.getHeader("X-Real-IP");
            if (serverIp == null || serverIp.isEmpty() || "unknown".equalsIgnoreCase(serverIp)) {
                serverIp = request.getServerName();
            }
            int port = request.getServerPort();
            String imageUrl = String.format("http://%s:%d/api/note/uploads/%s", serverIp, port, newFileName);

            // 8. 记录MD5映射（内存+文件持久化）
            imageMd5Map.put(fileMd5, imageUrl);
            saveImageMd5Map(); // 保存到文件，重启不丢失

            // 9. 返回结果
            result.put("success", true);
            result.put("imageUrl", imageUrl);
            result.put("message", "上传成功");
            result.put("isDuplicate", false); // 标记为新文件
            logger.info("uploadImage() -> 图片上传成功，URL：{}", imageUrl);
            return result;

        } catch (Exception e) {
            logger.error("图片上传失败", e);
            result.put("success", false);
            result.put("message", "上传失败：" + e.getMessage());
            return result;
        }
    }

    // ========== 新增：暴露上传文件访问接口（必须！否则前端无法访问图片） ==========
    @GetMapping("/uploads/{fileName}")
    public void getUploadedImage(@PathVariable String fileName, HttpServletRequest request,
                                 javax.servlet.http.HttpServletResponse response) {
        onlineUserManager.recordUserAccess(request);
        try {
            // 读取文件
            String uploadDir = persistenceUtil.getDir() + "uploads/";
            File file = new File(uploadDir + fileName);
            if (!file.exists()) {
                response.setStatus(404);
                response.getWriter().write("图片不存在");
                return;
            }

            // 响应图片（自动识别类型）
            FileUtil.writeToStream(file, response.getOutputStream());
            response.setContentType(FileUtil.getMimeType(file.getPath()));
        } catch (Exception e) {
            logger.error("获取上传图片失败", e);
            response.setStatus(500);
        }
    }

    // 每天凌晨2点执行 新增定时任务，删除 MD5 映射中不存在的文件（防止手动删除文件后映射残留）
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanInvalidImages() {
        String uploadDir = persistenceUtil.getDir() + "uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            // 计算文件MD5，判断是否在映射中
            String fileMd5 = FileMd5Util.getFileMd5(file);
            if (!imageMd5Map.containsKey(fileMd5)) {
                FileUtil.del(file); // 删除无映射的文件
                logger.info("cleanInvalidImages() -> 删除无效图片：{}", file.getName());
            }
        }
    }

}
