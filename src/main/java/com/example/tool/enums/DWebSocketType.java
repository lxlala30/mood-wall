package com.example.tool.enums;

import lombok.Getter;

/**
 *  * WebSocket 消息类型枚举
 *  * 用于区分不同业务场景的推送消息，如示例消息、系统通知、订单消息等
 *  
 */
@Getter
public enum DWebSocketType {
    // 测试用示例消息
    DEMO("示例消息"),
    // 平台级系统通知
    NOTICE("系统通知"),
    // 订单相关业务消息
    ORDER("订单消息");

    // 消息类型描述
    private final String desc;

    // 构造方法，初始化消息类型描述
    DWebSocketType(String desc) {
        this.desc = desc;
    }
}
