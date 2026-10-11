package com.redirecttip;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "redirecttip",
        name = "RedirectTip",
        version = "0.3",
        description = "群组服玩家跳转/离开提示插件",
        authors = {"RedirectTip"}
)
public class RedirectTipPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private PluginConfig config;

    @Inject
    public RedirectTipPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        config = new PluginConfig(dataDirectory, logger);

        server.getEventManager().register(this, new RedirectListener(this, server, config, logger));

        CommandManager commandManager = server.getCommandManager();
        CommandMeta meta = commandManager.metaBuilder("redirecttip")
                .aliases("rtip")
                .plugin(this)
                .build();
        commandManager.register(meta, new ReloadCommand(this));

        logger.info("RedirectTip 已启用! 配置文件: {}", dataDirectory.resolve("config.yml"));
    }

    /** 重载配置文件 */
    public void reloadConfig() {
        config.reload();
    }
}
