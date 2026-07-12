package com.netessx.qqmsgsync.command;

import io.github.kloping.qqbot.api.v2.GroupMessageEvent;
import com.netessx.qqmsgsync.QQMsgSync;

import java.util.Objects;

public class GroupAdminPermissionChecker implements PermissionChecker {

    @Override
    public boolean check(GroupMessageEvent event, QQMsgSync plugin) {
        String role = event.getSender().getMeta().getString("member_role");
        return Objects.equals(role, "owner") || Objects.equals(role, "admin");
    }
}
