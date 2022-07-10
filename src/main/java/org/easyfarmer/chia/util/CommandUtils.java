package org.easyfarmer.chia.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 执行系统命令
 *
 * @author liyifeng
 * @version 1.0
 * @date 2021/6/23 9:43 上午
 */
public class CommandUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandUtils.class);

    public static List<String> exec(String cmd, String[] envp, File cmdPath) throws Exception {
        LOGGER.info("执行命令:{}，path:{}", cmd, (cmdPath != null ? cmdPath.getAbsolutePath() : "null"));
        StringBuilder result = new StringBuilder();

        Process process = null;
        BufferedReader bufrIn = null;
        BufferedReader bufrError = null;
        List<String> list = new ArrayList<>();
        try {
            // 执行命令, 返回一个子进程对象（命令在子进程中执行）
            process = Runtime.getRuntime().exec(cmd, envp, cmdPath);

            // 方法阻塞, 等待命令执行完成（成功会返回0）
            process.waitFor();

            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            bufrIn = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));
            bufrError = new BufferedReader(new InputStreamReader(process.getErrorStream(), Charset.defaultCharset()));

            // 读取输出
            String line = null;
            while ((line = bufrIn.readLine()) != null) {
                result.append(line).append('\n');
                //System.out.println(line);
                list.add(line);
            }
            while ((line = bufrError.readLine()) != null) {
                //System.out.println(line);
                result.append(line).append('\n');
                list.add(line);
            }

        } catch (Exception ex) {
                ex.printStackTrace();
        } finally {
            closeStream(bufrIn);
            closeStream(bufrError);

            // 销毁子进程
            if (process != null) {
                process.destroy();
            }
        }

        LOGGER.debug("执行命令返回结果:{}", result);
        // 返回执行结果
        return list;
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                // nothing
            }
        }
    }

    public static List<String> exec(String cmd, String[] envp, File cmdPath, boolean autoAppend) throws Exception {
        if (autoAppend && OSInfo.isWindows()) {
            cmd = "cmd /c " + cmd;
        }
        return exec(cmd, envp, cmdPath);
    }
    public static void linuxKillProcess(Long processId) throws Exception {
        CommandUtils.exec("kill -9 " + processId, null, null);
    }

    public static void windowsKillProcess(Long pid) throws Exception {
        String cmd = String.format("taskkill /f /t /pid %s ", pid);
        exec(cmd, null, null);
    }



}
