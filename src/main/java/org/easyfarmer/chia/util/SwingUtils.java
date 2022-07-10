package org.easyfarmer.chia.util;

import java.awt.*;
import java.net.URI;

/**
 * @author liyifeng
 * @version 1.0
 * @date 2022/7/10 2:37 下午
 */
public class SwingUtils {
    public static void jump2Url(String url) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(url));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
