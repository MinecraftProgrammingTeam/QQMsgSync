package com.netessx.qqmsgsync.command;

import io.github.kloping.qqbot.api.v2.GroupMessageEvent;
import io.github.kloping.qqbot.entities.qqpd.message.RawMessage;
import com.netessx.qqmsgsync.QQMsgSync;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * QQ 机器人命令处理器 —— 所有 "/" 开头的命令在此定义。
 *
 * 添加新命令只需在此类中添加一个 @Command 注解的方法。
 * 方法签名必须为：void methodName(GroupMessageEvent event, QQMsgSync plugin)
 */
public class CommandHandler {

    @Command(
            value = "绑定群聊",
            permissionChecker = AdminPermissionChecker.class,
            denyMessage = "您不是机器人管理员，没有权限进行该操作。\n如果您是，请您执行“/获取ID”后修改config.yml！"
    )
    public void bindGroup(GroupMessageEvent event, QQMsgSync plugin) {
        RawMessage message = event.getRawMessage();
        plugin.getConfig().set("qbot-qgnumber", message.getSrcGuildId());
        QQMsgSync.QGNumber = message.getSrcGuildId();
        message.send("已绑定该群聊，OpenID：" + QQMsgSync.QGNumber);
        plugin.saveConfig();
        plugin.getLogger().info(ChatColor.GREEN + "Config Saved Successfully!");
    }

    @Command(value = "获取ID")
    public void getOpenId(GroupMessageEvent event, QQMsgSync plugin) {
        event.getRawMessage().send(
                "您的OpenID为：" + event.getSender().getOpenid()
                        + "\n请修改插件config.yml，将您设置为机器人管理员。"
                        + "然后在群聊中发送“/绑定群聊”来绑定。",
                event.getRawMessage()
        );
    }

    @Command(value = "关于")
    public void about(GroupMessageEvent event, QQMsgSync plugin) {
        event.getRawMessage().send(
                "MC消息同步机器人，支持双向消息同步。\n"
                        + "由XzyStudio制作，开源在 "
                        + "https://github.com/MinecraftProgrammingTeam/QQMsgSync 欢迎star支持！"
        );
    }
}
