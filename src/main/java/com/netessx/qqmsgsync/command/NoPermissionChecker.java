package com.netessx.qqmsgsync.command;

import io.github.kloping.qqbot.api.v2.GroupMessageEvent;
import com.netessx.qqmsgsync.QQMsgSync;

/**
 * 默认权限检查器，始终允许。
 * 用于 @Command 注解中 permissionChecker 的默认值。
 */
public class NoPermissionChecker implements PermissionChecker {

    @Override
    public boolean check(GroupMessageEvent event, QQMsgSync plugin) {
        return true;
    }
}
