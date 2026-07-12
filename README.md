# QQMsgSync

Minecraft 服务器 ↔ QQ 群消息 双向同步插件

---

## 功能

- QQ 群消息实时转发到 Minecraft 服务器聊天栏
- Minecraft 玩家聊天、加入/退出、死亡等信息自动同步到 QQ 群
- 基于注解的反射式命令框架，扩展命令只需加一个 `@Command` 注解

## 环境要求

- Minecraft 1.21+（Spigot / Paper）
- Java 21
- QQ 机器人（[bot-qqpd-java](https://github.com/kloping/bot-qqpd-java) 驱动）

## 快速开始

### 1. 安装

从 [Releases](https://github.com/MinecraftProgrammingTeam/QQMsgSync/releases) 下载最新 JAR，放入服务器的 `plugins/` 目录，启动服务器。

首次启动后会在 `plugins/QQMsgSync/` 生成 `config.yml`：

```yaml
qbot-appid: 12345678          # QQ 机器人 AppID
qbot-token: xxxxxx            # QQ 机器人 Token
qbot-secret: xxxxxx           # QQ 机器人 Secret
qbot-qgnumber: ""             # 绑定的群聊 OpenID（自动填写）
say-prefix: "[Q群消息]"        # MC 聊天栏前缀
admin-openid: ""              # 机器人管理员 OpenID
```

### 2. 配置

1. 修改 `qbot-appid`、`qbot-token`、`qbot-secret` 为你的 QQ 机器人信息
2. 重启服务器

### 3. 绑定群聊

在目标 QQ 群中发送：

```
/获取ID
```

将返回的 OpenID 填入 `config.yml` 的 `admin-openid`。然后在群内发送：

```
/绑定群聊
```

绑定成功后即可开始消息同步。

## 命令列表

| 命令 | 权限 | 说明 |
|------|------|------|
| `/获取ID` | 所有人 | 获取你的 OpenID |
| `/绑定群聊` | 机器人管理员 | 将当前群聊绑定为同步目标 |
| `/关于` | 所有人 | 查看插件信息 |

## 扩展命令（面向开发者）

命令采用 `@Command` 注解驱动，添加新命令只需在 `CommandHandler` 中加一个方法：

```java
// 无参数命令
@Command(value = "help")
public void help(GroupMessageEvent event, QQMsgSync plugin) {
    event.getRawMessage().send("可用命令: /获取ID, /绑定群聊, /关于");
}

// 带参数命令
@Command(value = "say", permissionChecker = GroupAdminPermissionChecker.class,
         denyMessage = "只有群管理员才能使用此命令")
public void say(GroupMessageEvent event, QQMsgSync plugin, String[] args) {
    String msg = String.join(" ", args);
    Bukkit.broadcastMessage("[群公告] " + msg);
}
```

方法签名支持两种形式：
- `void method(GroupMessageEvent event, QQMsgSync plugin)` — 无参数
- `void method(GroupMessageEvent event, QQMsgSync plugin, String[] args)` — 接收空格分隔的参数

### 内置权限检查器

| 类 | 判断逻辑 |
|------|------|
| `NoPermissionChecker` | 始终允许（`@Command` 默认值） |
| `AdminPermissionChecker` | 发送者 OpenID == `config.yml` 中 `admin-openid` |
| `GroupAdminPermissionChecker` | 发送者在群内角色为 `owner` 或 `admin` |

### 自定义权限检查器

实现 `PermissionChecker` 接口即可：

```java
public class MyChecker implements PermissionChecker {
    @Override
    public boolean check(GroupMessageEvent event, QQMsgSync plugin) {
        // 自定义逻辑，返回 true 允许执行
        return true;
    }
}
```

## 项目结构

```
src/main/java/com/netessx/qqmsgsync/
├── QQMsgSync.java              # 插件入口，注册 ListenerHost
├── eventHandler.java           # MC 事件监听 → QQ 群转发
└── command/
    ├── Command.java             # @Command 注解定义
    ├── CommandRegistry.java     # 命令注册中心（反射扫描、权限检查、分发）
    ├── CommandHandler.java      # 业务命令实现（添加命令改这里）
    ├── PermissionChecker.java   # 权限检查接口
    ├── NoPermissionChecker.java # 默认：始终允许
    ├── AdminPermissionChecker.java       # 机器人管理员
    └── GroupAdminPermissionChecker.java  # 群管理员
```

## CI / CD

每次 push 自动构建，打 `v*` 标签自动发布 Release。

- 构建产物命名：`qqmsgsync-{version}-{short-hash}.jar`
- 可在 [Actions](https://github.com/MinecraftProgrammingTeam/QQMsgSync/actions) 页面下载任意 commit 的构建产物

## 许可证

[Apache License 2.0](LICENSE) · 不得商用
