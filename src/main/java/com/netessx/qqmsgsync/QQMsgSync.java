package com.netessx.qqmsgsync;

import io.github.kloping.qqbot.Starter;
import io.github.kloping.qqbot.api.Intents;
import io.github.kloping.qqbot.api.v2.GroupMessageEvent;
import io.github.kloping.qqbot.entities.qqpd.message.RawMessage;
import io.github.kloping.qqbot.impl.ListenerHost;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class QQMsgSync extends JavaPlugin {
    public static Starter starter;
    public static String QGNumber;

    @Override
    public void onEnable() {
        // config.yml
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        QGNumber = getConfig().getString("qbot-qgnumber");

        starter = new Starter(getConfig().getString("qbot-appid"),getConfig().getString("qbot-token"), getConfig().getString("qbot-secret"));
        starter.getConfig().setCode(Intents.PUBLIC_GROUP_INTENTS.getCode());
        starter.APPLICATION.logger.setPrefix("[qgpd-bot]");
        starter.setReconnect(true);
        starter.run();

        starter.registerListenerHost(new ListenerHost() {

            @EventReceiver
            private void event(GroupMessageEvent event) {
                RawMessage message = event.getRawMessage();
                String content = message.getContent();

                if (content.startsWith("/")){
                    if (content.equals("/绑定群聊")){
                        if (!Objects.equals(event.getSender().getOpenid(), getConfig().getString("admin-openid"))){
                            message.send("您不是机器人管理员，没有权限进行该操作。\n如果您是，请您执行“/获取ID”后修改config.yml！");
                        }else {
                            getConfig().set("qbot-qgnumber", message.getSrcGuildId());
                            QGNumber = message.getSrcGuildId();
                            message.send("已绑定该群聊，OpenID：" + QGNumber);
                            saveConfig();
                            getLogger().info(ChatColor.GREEN + "Config Saved Successfully!");
                        }
                    }else if (content.equals("/获取ID")){
                        message.send("您的OpenID为：" + event.getSender().getOpenid() + "\n请修改插件config.yml，将您设置为机器人管理员。然后在群聊中发送“/绑定群聊”来绑定。");
                    }else if (content.equals("/关于")){
                        message.send("MC消息同步机器人，支持双向消息同步。\n由XzyStudio制作，开源在 https://github.com/MinecraftProgrammingTeam/QQMsgSync 欢迎star支持！");
                    }
                }else if (!content.startsWith("#")){
                    if (Objects.equals(message.getSrcGuildId(), QGNumber)) {
                        String username = event.getSender().getMeta().getString("username");
                        getServer().broadcastMessage(ChatColor.GOLD + getConfig().getString("say-prefix") + ChatColor.RESET + " <" + ChatColor.AQUA + username + ChatColor.RESET + "> " + content);
                    }
                }
            }
        });

        getServer().getPluginManager().registerEvents(new eventHandler(), this);
        getLogger().info(ChatColor.GREEN + "More info on " + ChatColor.BLUE + "https://github.com/MinecraftProgrammingTeam/QQMsgSync");
        getLogger().info(ChatColor.GREEN + "Enabled QQMessageSync Plugin!");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.GREEN + "Plugin disabled.");
    }
}
