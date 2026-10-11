package com.redirecttip;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class MessageUtil {

    private MessageUtil() {
    }

    /**
     * 将带 & 颜色代码的文本转换为 Adventure Component
     * 支持 &0-&9 &a-f &k-o &r, 以及十六进制颜色 &#RRGGBB / &x&R&R&G&G&B&B
     */
    public static Component colorize(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        // 将 &#RRGGBB 统一转换为 &x&R&R&G&G&B&B 格式, 再交给 Adventure 解析
        String converted = text.replaceAll(
                "&#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])",
                "&x&$1&$1&$2&$2&$3&$3&$4&$4&$5&$5&$6&$6");
        return LegacyComponentSerializer.legacyAmpersand().deserialize(converted);
    }
}
