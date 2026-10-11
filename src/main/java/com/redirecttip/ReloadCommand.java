package com.redirecttip;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReloadCommand implements SimpleCommand {

    private final RedirectTipPlugin plugin;

    public ReloadCommand(RedirectTipPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (invocation.arguments().length == 0) {
            source.sendMessage(Component.text("用法: /redirecttip reload — 重载配置文件", NamedTextColor.YELLOW));
            return;
        }
        if ("reload".equalsIgnoreCase(invocation.arguments()[0])) {
            plugin.reloadConfig();
            source.sendMessage(Component.text("RedirectTip 配置已重载!", NamedTextColor.GREEN));
        } else {
            source.sendMessage(Component.text("未知参数, 用法: /redirecttip reload", NamedTextColor.RED));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("redirecttip.reload");
    }
}
