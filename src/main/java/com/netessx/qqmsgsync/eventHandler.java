package com.netessx.qqmsgsync;

import com.alibaba.fastjson2.JSON;
import org.bukkit.Location;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class eventHandler implements Listener {
    @EventHandler
    public void PlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        sendMsg("<" + event.getPlayer().getName() + "> " + message);
    }

    @EventHandler
    public void PlayerLogin(PlayerJoinEvent event) {
        sendMsg(event.getJoinMessage());
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        sendMsg(event.getQuitMessage());
    }

    @EventHandler
    public void PlayerRespawn(PlayerRespawnEvent event) {
        sendMsg("玩家<" + event.getPlayer().getName() + ">重生在了(" + event.getRespawnLocation().getX() + "," + event.getRespawnLocation().getY() + "," + event.getRespawnLocation().getZ() + ")");
    }

    @EventHandler
    public void EntityDeath(PlayerDeathEvent event) {
        sendMsg(event.getDeathMessage());
    }

    @EventHandler
    public void PlayerAdvancementDone(PlayerAdvancementDoneEvent event){
        AdvancementDisplay achievement = event.getAdvancement().getDisplay();
        if (achievement == null) return;
        sendMsg("玩家<" + event.getPlayer().getName() + ">达成成就 " + achievement.getTitle() + "\n" + achievement.getDescription());
    }

    @EventHandler
    public void PlayerTeleport(PlayerTeleportEvent event){
        Map<PlayerTeleportEvent.TeleportCause, String> cause = new HashMap<>();
        cause.put(PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT, "由紫影果进行传送");
        cause.put(PlayerTeleportEvent.TeleportCause.COMMAND, "由命令进行传送");
        cause.put(PlayerTeleportEvent.TeleportCause.DISMOUNT, "Indicates the teleportation was caused by a player exiting a vehicle");
        cause.put(PlayerTeleportEvent.TeleportCause.END_GATEWAY, "由末地折跃门进行传送");
        cause.put(PlayerTeleportEvent.TeleportCause.END_PORTAL, "由末地传送门进行传送");
        cause.put(PlayerTeleportEvent.TeleportCause.ENDER_PEARL, "玩家抛出末影珍珠传送");
        cause.put(PlayerTeleportEvent.TeleportCause.EXIT_BED, "Indicates the teleportation was caused by a player exiting a bed");
        cause.put(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL, "由地狱传送门进行传送");
        cause.put(PlayerTeleportEvent.TeleportCause.PLUGIN, "由插件进行传送");
        cause.put(PlayerTeleportEvent.TeleportCause.SPECTATE, "由旁观者菜单传送到一个实体/玩家");
        cause.put(PlayerTeleportEvent.TeleportCause.UNKNOWN, "由除已知枚举外的其他情况进行了传送");
        Location from =  event.getFrom();
        Location to = event.getTo();
        assert to != null;
        sendMsg(
                String.format("玩家<%s>从 世界%s(%.1f,%.1f,%.1f) 传送到了 世界%s(%.1f,%.1f,%.1f)\n原因：%s",
                        event.getPlayer().getName(),
                        Objects.requireNonNull(from.getWorld()).getName(),
                        from.getX(), from.getY(), from.getZ(),
                        Objects.requireNonNull(to.getWorld()).getName(),
                        to.getX(), to.getY(), to.getZ(),
                        cause.get(event.getCause())
                )
        );
    }

    private void sendMsg(String msg){
        Map<String, String> headers = Map.of();
        Map<String, Object> body = new HashMap<>();
        body.put("content", msg);
        body.put("msg_type", 0);
        QQMsgSync.starter.getBot().groupBaseV2.send(QQMsgSync.QGNumber, JSON.toJSONString(body), headers);
    }
}
