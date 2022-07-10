package org.easyfarmer.chia.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.charset.Charset;

public class OSInfo {
    private static String OS = System.getProperty("os.name").toLowerCase();

    private static OSInfo _instance = new OSInfo();

    private OSInfo() {
    }

    public static void setOsName(String name){
        OS = name;
    }

    public static boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    public static boolean isMacOS() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0;
    }

    public static boolean isMacOSX() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

    public static boolean isOS2() {
        return OS.indexOf("os/2") >= 0;
    }

    public static boolean isSolaris() {
        return OS.indexOf("solaris") >= 0;
    }

    public static boolean isSunOS() {
        return OS.indexOf("sunos") >= 0;
    }

    public static boolean isMPEiX() {
        return OS.indexOf("mpe/ix") >= 0;
    }

    public static boolean isHPUX() {
        return OS.indexOf("hp-ux") >= 0;
    }

    public static boolean isAix() {
        return OS.indexOf("aix") >= 0;
    }

    public static boolean isOS390() {
        return OS.indexOf("os/390") >= 0;
    }

    public static boolean isFreeBSD() {
        return OS.indexOf("freebsd") >= 0;
    }

    public static boolean isIrix() {
        return OS.indexOf("irix") >= 0;
    }

    public static boolean isDigitalUnix() {
        return OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0;
    }

    public static boolean isNetWare() {
        return OS.indexOf("netware") >= 0;
    }

    public static boolean isOSF1() {
        return OS.indexOf("osf1") >= 0;
    }

    public static boolean isOpenVMS() {
        return OS.indexOf("openvms") >= 0;
    }


    public static boolean isUbuntu() {
        if (isLinux()) {
            File file = new File("/etc/issue");
            if (file.exists()) {
                try {
                    String content = FileUtil.readString(file, Charset.defaultCharset());
                    return StrUtil.isNotBlank(content) && StrUtil.containsIgnoreCase(content, "ubuntu");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static boolean isCentos() {
        if (isLinux()) {
            return new File("/etc/redhat-release").exists();
        }

        return false;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(OSInfo.isWindows());// 判断是否为windows系统
    }

    public static String getOsName() {
//        if (isWindows()) {
//            return "Windows";
//        }
//        if(isLinux()){
//            return "Linux";
//        }
//        if(isMacOS()){
//            return "Mac";
//        }
        return OS;
    }

    public static Integer getOsType() {
        if (isWindows()) {
            return 1;
        }
        if (isLinux()) {
            return 2;
        }
        if (isMacOS()) {
            return 3;
        }
        return -1;

    }
}
