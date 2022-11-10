package org.easyfarmer.chia;

import cn.hutool.core.io.resource.FileResource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.easyfarmer.chia.util.Constant;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigUtils {

    private static final String CONFIG_NAME = "config.properties";
    private static final File CONFIG_File = new File(Constant.USER_DIR, CONFIG_NAME);
    private static Properties properties = new Properties();
    private static final String address_key = "address";
    private static final String fee_key = "fee";

    static {
        loadConfig();
    }

    private static void loadConfig() {
        if (!CONFIG_File.exists()) {
            try {
                CONFIG_File.createNewFile();

                properties.put(address_key, "");
                properties.put(fee_key, "0");
                saveToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FileResource fileResource = new FileResource(CONFIG_File);
            try {
                properties.load(fileResource.getStream());
            } catch (IOException e) {

            }
        }
    }

    // 收钱地址
    public static String getReceivedAddress() {
        return properties.getProperty(address_key);
    }

    public static Integer getFee() {
        String obj = properties.getProperty(fee_key);
        if (StrUtil.isNotBlank(obj) && NumberUtil.isInteger(obj)) {
            return Integer.parseInt(obj);
        }
        return null;
    }

    public static void save(String address, String fee) {
        properties.put(address_key, address);
        properties.put(fee_key, fee);
        saveToFile();
    }

    private static void saveToFile() {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(CONFIG_File));
            properties.store(bos, "user settings");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {

                }
            }
        }
    }

}
