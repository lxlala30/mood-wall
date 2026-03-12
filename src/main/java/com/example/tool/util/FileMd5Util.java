package com.example.tool.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件MD5哈希工具类（纯JDK实现，无Hutool依赖）
 */
public class FileMd5Util {

    /**
     * 计算MultipartFile的MD5值
     */
    public static String getFileMd5(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return handleMd5Data(is);
        } catch (IOException e) {
            throw new RuntimeException("MD5算法不存在", e);
        }
    }

    /**
     * 计算本地文件的MD5值
     */
    public static String getFileMd5(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return handleMd5Data(is);
        } catch (IOException e) {
            throw new RuntimeException("计算文件MD5失败", e);
        }
    }

    /**
     * 处理MD5
     */
    private static String handleMd5Data(InputStream in) throws IOException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            return bytesToHex(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不存在", e);
        }
    }

    /**
     * 字节数组转16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
