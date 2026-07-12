package com.netessx.qqmsgsync.command;

import io.github.kloping.qqbot.api.v2.GroupMessageEvent;
import com.netessx.qqmsgsync.QQMsgSync;

/**
 * 权限检查器 —— 校验命令发送者是否有权执行该命令。
 * 实现类必须有无参构造器（由 CommandRegistry 反射实例化）。
 */
@FunctionalInterface
public interface PermissionChecker {

    /**
     * @param event  群消息事件
     * @param plugin 插件实例，用于读取配置
     * @return true 允许执行，false 拒绝
     */
    boolean check(GroupMessageEvent event, QQMsgSync plugin);
}
