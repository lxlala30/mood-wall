package com.example.tool.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.example.tool.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.Map;

@RestController
@RequestMapping("/api/tool")
public class ToolController {

    /**
     * 终端执行打包指令
     * mvn package -DskipTests
     */
    private static final Logger logger = LoggerFactory.getLogger(ToolController.class);

    // 1. JSON格式化
    @PostMapping("/json/format")
    public Result<String> jsonFormat(@RequestBody Map<String, String> map) {
        try {
            String json = map.get("json");
            logger.info("JSON格式化 json:{}", json);
            String pretty = JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(json));
            logger.info("JSON格式化 res:{}", pretty);
            return Result.ok(pretty);
        } catch (Exception e) {
            logger.info("JSON.toJsonPrettyStr err:{}", e.getMessage());
            e.printStackTrace();
            return Result.error("解析json异常,请确认数据格式!");
        }
    }

    // 2. 时间戳转日期
    @PostMapping("/time/toDate")
    public Result<String> timestampToDate(@RequestBody Map<String, String> map) {
        try {
            String ts = map.get("timestamp");
            logger.info("时间戳转日期 ts:{}", ts);
            long t = Long.parseLong(ts);
            String date = DateUtil.date(t).toString();
            logger.info("时间戳转日期 date:{}", date);
            return Result.ok(date);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理异常，请确认数据是否正确！");
        }
    }

    // 3. URL编码
    @PostMapping("/url/encode")
    public Result<String> urlEncode(@RequestBody Map<String, String> map) {
        try {
            String str = map.get("str");
            logger.info("URL编码 str:{}", str);
            String res = URLEncodeUtil.encode(str, Charset.forName("UTF-8"));
            logger.info("URL编码 res:{}", res);
            return Result.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理异常，请确认数据是否正确！");
        }
    }

    // 4. URL解码
    @PostMapping("/url/decode")
    public Result<String> urlDecode(@RequestBody Map<String, String> map) {
        try {
            String str = map.get("str");
            logger.info("URL解码 str:{}", str);
            String res = URLDecoder.decode(str, Charset.forName("UTF-8"));
            logger.info("URL解码 res:{}", res);
            return Result.ok(res);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理异常，请确认数据是否正确！");
        }
    }

    // 5. Base64编码
    @PostMapping(value = "/base64/encode", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> base64Encode(@RequestBody Map<String, String> map) {
        try {
            String str = map.get("str");
            logger.info("Base64编码 str:{}", str);
            String res = Base64.encode(str);
            logger.info("Base64编码 res:{}", res);
            return Result.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理异常，请确认数据是否正确！");
        }
    }

    // 6. Base64解码
    @PostMapping("/base64/decode")
    public Result<String> base64Decode(@RequestBody Map<String, String> map) {
        try {
            String str = map.get("str");
            logger.info("Base64解码 str:{}", str);
            String res = Base64.decodeStr(str);
            logger.info("Base64解码 res:{}", res);
            return Result.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理异常，请确认数据是否正确！");
        }
    }

    // 7. MD5
    @PostMapping("/hash/md5")
    public Result<String> md5(@RequestBody Map<String, String> map) {
        try {
            String str = map.get("str");
            logger.info("MD5 str:{}", str);
            return Result.ok(SecureUtil.md5(str));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理异常，请确认数据是否正确！");
        }
    }

    // 8. 随机密码
    @PostMapping("/password/generate")
    public Result<String> genPwd(@RequestBody Map<String, Integer> map) {
        try {
            int len = map.getOrDefault("len", 16);
            String pwd = IdUtil.randomUUID().replaceAll("[^a-zA-Z0-9]", "");
            logger.info("随机密码 pwd:{}", pwd);
            return Result.ok(pwd.substring(0, len));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("处理异常，请确认数据是否正确！");
        }
    }

    // 测试接口联通
    @GetMapping(value = "/encode", produces = MediaType.TEXT_PLAIN_VALUE)
    public String encodeToBase64(@RequestParam String str) {
        logger.info("encodeToBase64() -> str:{}", str);
        return str;
    }
}
