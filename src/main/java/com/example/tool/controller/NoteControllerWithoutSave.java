package com.example.tool.controller;

import cn.hutool.core.date.DateUtil;
import com.example.tool.dto.Note;
import com.example.tool.util.OnlineUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/note/without/save")
@CrossOrigin
public class NoteControllerWithoutSave {

    private static final Logger logger = LoggerFactory.getLogger(NoteControllerWithoutSave.class);

    private final List<Note> noteList = new ArrayList<>();
    private final AtomicInteger idGen = new AtomicInteger(1);
    // 注入在线用户管理器
    private final OnlineUserManager onlineUserManager;

    // 构造器注入
    public NoteControllerWithoutSave(OnlineUserManager onlineUserManager) {
        this.onlineUserManager = onlineUserManager;
    }

    @PostConstruct
    public void init() {
        Note note = new Note();
        note.setId(idGen.getAndIncrement());
        note.setContent("欢迎来到 MoodWall，写下你的心情～");
        note.setColor("#ffeaa7");
        note.setCreateTime(DateUtil.now());
        noteList.add(note);
    }

    /**
     * 获取便签列表（同时记录用户访问）
     */
    @GetMapping("/list")
    public List<Note> list(HttpServletRequest request) {
        // 记录用户访问，更新在线状态
        onlineUserManager.recordUserAccess(request);
        return noteList;
    }

    /**
     * 新增便签（同时记录用户访问）
     */
    @PostMapping("/add")
    public List<Note> add(@RequestBody Note note, HttpServletRequest request) {
        onlineUserManager.recordUserAccess(request);

        note.setId(idGen.getAndIncrement());
        note.setCreateTime(DateUtil.now());
        // 初始化点赞数为0
        if (note.getLikeCount() == null) {
            note.setLikeCount(0);
        }
        noteList.add(note);
        logger.info("add() -> 新增标签content:{}", note.getContent());
        return noteList;
    }

    /**
     * 删除便签
     */
    @DeleteMapping("/delete/{id}")
    public List<Note> delete(@PathVariable Integer id, HttpServletRequest request) {
        onlineUserManager.recordUserAccess(request);

        logger.info("delete() -> 待删除的标签id:{}", id);
        noteList.removeIf(note -> note.getId().equals(id));
        logger.info("delete() -> 已有的标签size:{}", noteList.size());
        return noteList;
    }

    /**
     * 点赞接口
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
        // 补充当前请求用户的IP
        result.put("currentIp", onlineUserManager.getIpAddress(request));
        return result;
    }
}
