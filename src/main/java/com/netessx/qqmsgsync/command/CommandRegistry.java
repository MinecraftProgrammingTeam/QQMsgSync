package com.netessx.qqmsgsync.command;

import io.github.kloping.qqbot.api.v2.GroupMessageEvent;
import com.netessx.qqmsgsync.QQMsgSync;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 命令注册中心 —— 反射扫描 @Command 方法，缓存后提供 O(1) 分发。
 *
 * 用法：
 * <pre>
 * CommandRegistry registry = new CommandRegistry(plugin, new CommandHandler());
 * registry.dispatch("绑定群聊", event);
 * </pre>
 */
public class CommandRegistry {

    private final QQMsgSync plugin;
    private final Object handlerInstance;
    private final Map<String, CommandEntry> commands = new HashMap<>();

    public CommandRegistry(QQMsgSync plugin, Object handlerInstance) {
        this.plugin = plugin;
        this.handlerInstance = handlerInstance;
        scanMethods();
    }

    /**
     * 分发命令。
     *
     * @param fullCommand 完整命令文本（不带 "/"），如 "绑定群聊" 或 "kick player1 reason"
     * @param event       群消息事件
     * @return true 找到了匹配的命令（已执行或权限被拒），false 未找到
     */
    public boolean dispatch(String fullCommand, GroupMessageEvent event) {
        // 拆分命令名和参数："/kick player1 reason" → name="kick", args=["player1","reason"]
        String[] parts = fullCommand.split("\\s+", 2);
        String commandName = parts[0];
        String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

        CommandEntry entry = commands.get(commandName);
        if (entry == null) {
            return false;
        }
        try {
            if (!entry.permissionChecker.check(event, plugin)) {
                String denyMsg = entry.denyMessage;
                if (denyMsg != null && !denyMsg.isEmpty()) {
                    event.getRawMessage().send(denyMsg, event.getRawMessage());
                }
                return true;
            }
            if (entry.hasArgs) {
                entry.method.invoke(handlerInstance, event, plugin, args);
            } else {
                entry.method.invoke(handlerInstance, event, plugin);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING,
                    "Error executing command: /" + commandName, e);
        }
        return true;
    }

    private void scanMethods() {
        for (Method method : handlerInstance.getClass().getDeclaredMethods()) {
            Command cmd = method.getAnnotation(Command.class);
            if (cmd == null) {
                continue;
            }

            // 验证签名：
            //   2 参数: void method(GroupMessageEvent, QQMsgSync)
            //   3 参数: void method(GroupMessageEvent, QQMsgSync, String[])
            Class<?>[] params = method.getParameterTypes();
            boolean hasArgs;
            if (params.length == 2
                    && GroupMessageEvent.class.isAssignableFrom(params[0])
                    && QQMsgSync.class.isAssignableFrom(params[1])) {
                hasArgs = false;
            } else if (params.length == 3
                    && GroupMessageEvent.class.isAssignableFrom(params[0])
                    && QQMsgSync.class.isAssignableFrom(params[1])
                    && String[].class.isAssignableFrom(params[2])) {
                hasArgs = true;
            } else {
                plugin.getLogger().warning(
                        "Method " + method.getName()
                                + " has @Command but wrong signature. "
                                + "Expected: void methodName(GroupMessageEvent, QQMsgSync[, String[]])");
                continue;
            }

            // 实例化权限检查器
            PermissionChecker checker;
            try {
                checker = cmd.permissionChecker().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING,
                        "Failed to instantiate PermissionChecker for command /" + cmd.value()
                                + ", falling back to NoPermissionChecker", e);
                checker = new NoPermissionChecker();
            }

            String name = cmd.value();
            if (commands.containsKey(name)) {
                plugin.getLogger().warning(
                        "Duplicate command name: /" + name
                                + ". Method " + method.getName() + " overrides previous.");
            }

            commands.put(name, new CommandEntry(method, checker, cmd.denyMessage(), hasArgs));
            plugin.getLogger().info("Registered command: /" + name);
        }
    }

    private record CommandEntry(
            Method method,
            PermissionChecker permissionChecker,
            String denyMessage,
            boolean hasArgs
    ) {}
}
