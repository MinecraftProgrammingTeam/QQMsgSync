package com.netessx.qqmsgsync.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个方法为 QQ 机器人命令处理器。方法签名支持两种形式：
 * <ul>
 *   <li>void methodName(GroupMessageEvent, QQMsgSync) —— 无参数命令</li>
 *   <li>void methodName(GroupMessageEvent, QQMsgSync, String[]) —— 接收参数</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    /** 命令名（不带 "/"），如 "绑定群聊" */
    String value();

    /** 权限检查器类，默认允许所有人 */
    Class<? extends PermissionChecker> permissionChecker() default NoPermissionChecker.class;

    /** 权限拒绝时发送的消息，为空则不发送 */
    String denyMessage() default "";
}
