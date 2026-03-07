package com.example.tool.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 便签持久化配置类（绑定application.properties中的配置）
 */
@Component
@ConfigurationProperties(prefix = "note.persistence")
public class NotePersistenceProperties {
    // 存储目录（对应note.persistence.dir）
    private String dir;
    // 文件名（对应note.persistence.filename）
    private String filename;

    // 拼接完整的文件路径（便捷方法）
    public String getFullFilePath() {
        // 确保目录以/结尾，避免路径拼接错误
        String finalDir = dir.endsWith("/") ? dir : dir + "/";
        return finalDir + filename;
    }

    // Getter & Setter（必须有，否则无法绑定配置）
    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
