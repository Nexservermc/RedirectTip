package com.redirecttip;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

public class RedirectListener {

    private final RedirectTipPlugin plugin;
    private final ProxyServer server;
    private final PluginConfig config;
    private final Logger logger;

    public RedirectListener(RedirectTipPlugin plugin, ProxyServer server, PluginConfig config, Logger logger) {
        this.plugin = plugin;
        this.server = server;
        this.config = config;
        this.logger = logger;
    }

    /**
     * 玩家连接上某个子服时触发(首次上线 + 子服间跳转)
     * 消息广播给全群组所有玩家,触发事件的玩家本人也会收到(延迟发送,见 broadcast)
     */
    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        String serverId = event.getServer().getServerInfo().getName();
        String serverName = config.getServerDisplayName(serverId);

        String message;
        String tag;
        if (event.getPreviousServer().isEmpty()) {
            // 首次进入代理(上线),第一次连接子服
            if (!config.isAnnounceOnFirstJoin()) {
                return;
            }
            tag = "加入";
            message = config.getJoinMessage()
                    .replace("%player%", player.getUsername())
                    .replace("%server%", serverName);
        } else {
            // 子服间跳转
            tag = "跳转";
            String previousId = event.getPreviousServer().get().getServerInfo().getName();
            String previousName = config.getServerDisplayName(previousId);
            message = config.getSwitchMessage()
                    .replace("%player%", player.getUsername())
                    .replace("%server%", serverName)
                    .replace("%from%", previousName);
        }
        broadcast(message, tag, player);
    }

    /**
     * 玩家离开代理(退出游戏)时触发,不是切换子服
     */
    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        // 只提示正常登录后离开的玩家(排除登录被拒绝、重名踢出等情况)
        if (event.getLoginStatus() != DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) {
            return;
        }
        String message = config.getLeaveMessage()
                .replace("%player%", event.getPlayer().getUsername());
        broadcast(message, "离开", event.getPlayer());
    }

    /**
     * 向全群组所有在线玩家广播消息,并同步打印到 Velocity 控制台
     *
     * @param self 触发事件的那名玩家本人。本人会延迟发送(默认1秒),避免客户端刚进入/切换
     *             服务器时聊天消息丢失;其他玩家即时收到。self 已不在线时(如退出代理)自动跳过
     */
    private void broadcast(String raw, String tag, Player self) {
        Component component = MessageUtil.colorize(raw);
        for (Player player : server.getAllPlayers()) {
            if (player.equals(self)) {
                // 延迟发送给本人,确保客户端已完全进入新服务器
                long delayMs = config.getSelfMessageDelayMs();
                if (delayMs <= 0) {
                    player.sendMessage(component);
                } else {
                    server.getScheduler().buildTask(plugin, () -> {
                        if (player.isActive()) {
                            player.sendMessage(component);
                        }
                    }).delay(delayMs, TimeUnit.MILLISECONDS).schedule();
                }
            } else {
                player.sendMessage(component);
            }
        }
        // 控制台打印(自动去掉颜色代码,便于阅读)
        String plain = PlainTextComponentSerializer.plainText().serialize(component);
        logger.info("[RedirectTip][{}] {}", tag, plain);
    }
}
