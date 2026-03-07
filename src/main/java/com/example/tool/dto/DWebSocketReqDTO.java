package com.example.tool.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;


@Data
// 开启链式调用，方便构建对象
@Accessors(chain = true)
public class DWebSocketReqDTO<T> implements Serializable {

    private static final long serialVersionUID = 1292707161671865097L;
    private String type; // 消息类型，对应DWebSocketType的枚举值
    private Long userId; // 目标推送用户ID
    private T body; // 消息体内容，泛型适配不同业务场景
}
