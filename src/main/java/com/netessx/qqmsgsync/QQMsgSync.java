package com.netessx.qqmsgsync;

import com.netessx.qqmsgsync.command.CommandHandler;
import com.netessx.qqmsgsync.command.CommandRegistry;
import io.github.kloping.qqbot.Starter;
import io.github.kloping.qqbot.api.Intents;
import io.github.kloping.qqbot.api.v2.GroupMessageEvent;
import io.github.kloping.qqbot.entities.qqpd.message.RawMessage;
import io.github.kloping.qqbot.impl.ListenerHost;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class QQMsgSync extends JavaPlugin {
    public static Starter starter;
    public static String QGNumber;
    public static QQMsgSync instance;

    @Override
    public void onEnable() {
        instance = this;
        // config.yml
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        QGNumber = getConfig().getString("qbot-qgnumber");

        starter = new Starter(getConfig().getString("qbot-appid"),getConfig().getString("qbot-token"), getConfig().getString("qbot-secret"));
        starter.getConfig().setCode(Intents.PUBLIC_GROUP_INTENTS.getCode());
        starter.APPLICATION.logger.setPrefix("[qgpd-bot]");
        starter.setReconnect(true);
        starter.run();

        CommandRegistry commandRegistry = new CommandRegistry(this, new CommandHandler());

        starter.registerListenerHost(new ListenerHost() {

            @EventReceiver
            private void event(GroupMessageEvent event) {
                RawMessage message = event.getRawMessage();
                String content = message.getContent();

                if (content.startsWith("//")){
                    Bukkit.getScheduler().runTask(instance, () -> getServer().dispatchCommand(getServer().getConsoleSender(), event.getRawMessage().getContent().replace("//", "")));
                    event.getRawMessage().send("已执行！", event.getRawMessage());
                }else if (content.startsWith("/")) {
                    commandRegistry.dispatch(content.substring(1), event);
                } else if (!content.startsWith("#")) {
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
