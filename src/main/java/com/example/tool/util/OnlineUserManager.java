package com.example.tool.util;

import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在线用户管理工具类
 */
@Component
public class OnlineUserManager {
    // 存储在线用户：IP -> 最后活跃时间
    private final Map<String, Long> onlineUserMap = new ConcurrentHashMap<>();
    // 在线人数计数器
    private final AtomicInteger onlineCount = new AtomicInteger(0);
    // 清理过期用户的间隔（3分钟）
    private static final long EXPIRE_TIME = 180 * 1000;

    public OnlineUserManager() {
        // 定时清理3分钟内无活跃的用户
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                onlineUserMap.entrySet().removeIf(entry -> (now - entry.getValue()) > EXPIRE_TIME);
                onlineCount.set(onlineUserMap.size());
            }
        }, 0, 60 * 1000); // 每分钟检查一次
    }

    /**
     * 记录用户访问（更新活跃时间/新增用户）
     */
    public void recordUserAccess(HttpServletRequest request) {
        String ip = getIpAddress(request);
        long now = System.currentTimeMillis();
        boolean isNewUser = !onlineUserMap.containsKey(ip);

        onlineUserMap.put(ip, now);
        if (isNewUser) {
            onlineCount.incrementAndGet();
        }
    }

    /**
     * 获取真实IP地址，优先返回IPv4
     */
    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多IP情况（如代理）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // 本地回环地址处理：优先返回IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    /**
     * 获取当前在线人数
     */
    public int getOnlineCount() {
        return onlineCount.get();
    }

    /**
     * 获取在线用户IP列表
     */
    public List<String> getOnlineIpList() {
        return new ArrayList<>(onlineUserMap.keySet());
    }
}
