package com.example.tool.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    /**
     * 根路径 "/" 重定向到 tool_server.html
     */
    @GetMapping("/12")
    public String redirectToIndex() {
        // 方式 1：内部转发（地址栏不变）
        String path = "forward:/onetwo.html";
        logger.info("redirectToIndex() -> path:{}", path);

        // 方式 2：重定向（地址栏会显示 /tool_server.html）
        // return "redirect:/tool_server.html";
        return path;
    }

    /**
     * 根路径 "/" 重定向到 nono.html
     */
    @GetMapping("/nono")
    public String redirectToNoNo() {
        String path = "forward:/nono.html";
        logger.info("redirectToNoNo() -> path:{}", path);
        return path;
    }

    /**
     * 根路径 "/" 重定向到 tool_server.html
     */
    @GetMapping("/")
    public String redirectToPath() {
        // 方式 2：重定向（地址栏会显示 /index_v0.html）
//        String path = "redirect:/index_v0.html";
        String path = "forward:/index.html";
        logger.info("redirectToPath() -> path:{}", path);
        return path;
    }

    /**
     * 根路径 "/" 重定向到 note.html
     */
    @GetMapping("/note")
    public String redirectToNote() {
        String path = "forward:/note_server.html";
        logger.info("redirectToNote() -> path:{}", path);
        return path;
    }
    @GetMapping("/note_admin1")
    public String redirectToNoteAdmin() {
        String path = "forward:/note_admin.html";
        logger.info("redirectToNote() -> path:{}", path);
        return path;
    }

    /**
     * 根路径 "/" 重定向到 tool.html
     */
    @GetMapping("/tool")
    public String redirectToTool() {
        String path = "redirect:/tool.html";
        logger.info("redirectToTool() -> path:{}", path);
        return path;
    }
}
