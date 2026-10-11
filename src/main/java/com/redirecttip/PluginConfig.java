package com.redirecttip;

import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PluginConfig {

    private final Path dataDirectory;
    private final Logger logger;

    private boolean announceOnFirstJoin = true;
    private long selfMessageDelayMs = 1000;
    private final Map<String, String> serverNames = new HashMap<>();
    private String joinMessage = "&a[+] &e%player% &7加入了服务器";
    private String switchMessage = "&e[»] &e%player% &7从 &e%from% &7跳转到了 &e%server%";
    private String leaveMessage = "&c[-] &e%player% &7离开了服务器";

    public PluginConfig(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        reload();
    }

    /** 重新加载配置 */
    public void reload() {
        Path file = dataDirectory.resolve("config.yml");
        try {
            // 首次运行: 从 jar 中复制默认配置
            if (Files.notExists(file)) {
                Files.createDirectories(dataDirectory);
                try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                    if (in == null) {
                        logger.warn("jar 中未找到默认 config.yml, 将使用代码内置默认值");
                    } else {
                        Files.copy(in, file);
                        logger.info("已生成默认配置文件: {}", file);
                    }
                }
            }

            YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(file).build();
            ConfigurationNode root = loader.load();

            announceOnFirstJoin = root.node("announce-on-first-join").getBoolean(true);
            selfMessageDelayMs = root.node("self-message-delay-ms").getLong(1000);

            serverNames.clear();
            ConfigurationNode namesNode = root.node("server-names");
            for (Object key : namesNode.childrenMap().keySet()) {
                String id = String.valueOf(key);
                serverNames.put(id, namesNode.node(key).getString(id));
            }

            joinMessage = root.node("messages", "join").getString("&a[+] &e%player% &7加入了服务器");
            switchMessage = root.node("messages", "switch").getString("&e[»] &e%player% &7从 &e%from% &7跳转到了 &e%server%");
            leaveMessage = root.node("messages", "leave").getString("&c[-] &e%player% &7离开了服务器");
        } catch (IOException e) {
            logger.error("加载 config.yml 失败", e);
        }
    }

    public boolean isAnnounceOnFirstJoin() {
        return announceOnFirstJoin;
    }

    /**发给触发玩家本人的消息延迟(毫秒),0 表示立即发送 */
    public long getSelfMessageDelayMs() {
        return selfMessageDelayMs;
    }

    /** 根据子服 ID 获取显示名称, 未配置则返回 ID 本身 */
    public String getServerDisplayName(String serverId) {
        return serverNames.getOrDefault(serverId, serverId);
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public String getSwitchMessage() {
        return switchMessage;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }
}
