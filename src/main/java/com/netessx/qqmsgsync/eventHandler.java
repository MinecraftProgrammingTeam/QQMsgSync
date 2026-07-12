package com.netessx.qqmsgsync;

import com.alibaba.fastjson2.JSON;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;

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

    private void sendMsg(String msg){
        Map<String, String> headers = Map.of();
        Map<String, Object> body = new HashMap<>();
        body.put("content", msg);
        body.put("msg_type", 0);
        QQMsgSync.starter.getBot().groupBaseV2.send(QQMsgSync.QGNumber, JSON.toJSONString(body), headers);
    }
}
