package com.example.tool.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.json.JSONUtil;
import com.example.tool.config.NotePersistenceProperties;
import com.example.tool.dto.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 便签数据文件持久化工具类（配置化版本）
 */
@Component // 改为Spring组件，方便注入配置
public class NotePersistenceUtil {
    private static final Logger logger = LoggerFactory.getLogger(NotePersistenceUtil.class);

    // 注入配置类（从application.properties读取配置）
    private final NotePersistenceProperties persistenceProperties;

    // 完整文件路径（初始化后赋值）
    private String fullFilePath;

    // 锁对象，保证多线程读写安全
    private static final Object LOCK = new Object();

    // 构造器注入配置类
    public NotePersistenceUtil(NotePersistenceProperties persistenceProperties) {
        this.persistenceProperties = persistenceProperties;
    }

    /**
     * 初始化：拼接路径 + 创建目录
     */
    @PostConstruct
    public void init() {
        // 1. 获取完整文件路径
        this.fullFilePath = persistenceProperties.getFullFilePath();
        logger.info("NotePersistenceUtil -> 数据文件路径初始化：{}", fullFilePath);

        // 2. 自动创建存储目录（如果不存在）
        try {
            String dirPath = persistenceProperties.getDir();
            FileUtil.mkdir(dirPath);
            logger.info("NotePersistenceUtil -> 数据存储目录初始化完成：{}", dirPath);
        } catch (Exception e) {
            logger.error("NotePersistenceUtil -> 创建数据目录失败", e);
        }
    }

    public String getDir() {
        return persistenceProperties.getDir();
    }

    /**
     * 从文件加载便签列表
     */
    public List<Note> loadNotes() {
        synchronized (LOCK) {
            // 如果文件不存在，返回空列表
            if (!FileUtil.exist(fullFilePath)) {
                logger.info("loadNotes() -> 数据文件不存在，初始化空列表：{}", fullFilePath);
                return new ArrayList<>();
            }

            try {
                // 读取文件内容并反序列化为List<Note>
                FileReader reader = new FileReader(fullFilePath);
                String jsonStr = reader.readString();
                List<Note> noteList = JSONUtil.toList(jsonStr, Note.class);
                logger.info("loadNotes() -> 成功加载 {} 条便签数据", noteList.size());
                return noteList;
            } catch (Exception e) {
                logger.error("loadNotes() -> 加载数据失败，返回空列表", e);
                return new ArrayList<>();
            }
        }
    }

    /**
     * 将便签列表保存到文件
     */
    public void saveNotes(List<Note> noteList) {
        synchronized (LOCK) {
            try {
                // 如果列表为空，仍保存空数组（避免文件丢失）
                List<Note> saveList = CollectionUtil.isEmpty(noteList) ? new ArrayList<>() : noteList;
                // 序列化为格式化的JSON字符串（可读性好）
                String jsonStr = JSONUtil.toJsonPrettyStr(saveList);
                // 写入文件（覆盖原有内容）
                FileWriter writer = new FileWriter(fullFilePath);
                writer.write(jsonStr);
                logger.info("saveNotes() -> 成功保存 {} 条便签数据到：{}", saveList.size(), fullFilePath);
            } catch (Exception e) {
                logger.error("saveNotes() -> 保存数据失败", e);
            }
        }
    }

    /**
     * 获取最大ID（用于初始化idGen，避免ID重复）
     */
    public static int getMaxNoteId(List<Note> noteList) {
        if (CollectionUtil.isEmpty(noteList)) {
            return 0;
        }
        return noteList.stream()
                .map(Note::getId)
                .filter(id -> id != null)
                .max(Integer::compare)
                .orElse(0);
    }
}
