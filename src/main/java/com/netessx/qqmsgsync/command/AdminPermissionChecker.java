package com.netessx.qqmsgsync.command;

import io.github.kloping.qqbot.api.v2.GroupMessageEvent;
import com.netessx.qqmsgsync.QQMsgSync;

import java.util.Objects;

/**
 * 管理员权限检查器 —— 仅 config.yml 中 admin-openid 对应的用户可执行命令。
 */
public class AdminPermissionChecker implements PermissionChecker {

    @Override
    public boolean check(GroupMessageEvent event, QQMsgSync plugin) {
        String adminOpenId = plugin.getConfig().getString("admin-openid");
        return Objects.equals(event.getSender().getOpenid(), adminOpenId);
    }
}
