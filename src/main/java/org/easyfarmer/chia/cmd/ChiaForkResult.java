package org.easyfarmer.chia.cmd;

import cn.hutool.core.util.StrUtil;
import org.easyfarmer.chia.util.Constant;
import org.easyfarmer.chia.util.OSInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ChiaForkResult {

    protected Map<String, String> fieldMap;
    protected String cmd;
    protected String coinTypeName;
    protected boolean stopResolve = false; //是否停止解析
    protected List<String> originResult;

    protected boolean success; //有些命令需要知道是否成功
    private String cmdPath; // 程序命令目录
    private String programFolderName; // 主程序目录名称C:\Users\whi\AppData\Local\apple-blockchain 当中的apple-blockchain


    public String initProgramAbsolutePath() {
        if (StrUtil.isNotBlank(this.cmdPath)) {
            return this.cmdPath;
        }
        if (StrUtil.isBlank(programFolderName)) {
            return null;
//            throw new RuntimeException("主程序目录名称为空，无法获取该币种的完整程序目录");
        }
        if (OSInfo.isWindows()) {
            return initWindowsCmdPath();

        } else if (OSInfo.isLinux()) {

            return initLinuxCmdPath();
        }
        return null;
    }

    private String initLinuxCmdPath() {
        File chiaDir = new File(Constant.USER_HOME, programFolderName);
        if (!chiaDir.exists()) {
//            throw new RuntimeException("币种主程序目录不存在：" + coinBasePath);
            return null;
        } else {

        }
        File venvDir = new File(chiaDir, "/venv/bin");
        if (venvDir.exists()) {
            this.cmdPath = venvDir.getAbsolutePath();
        }
        return this.cmdPath;

    }

    private String initWindowsCmdPath() {
        String coinBasePath = String.format("%s\\AppData\\Local\\%s", Constant.USER_HOME, programFolderName);
        File file = new File(coinBasePath);
        if (!file.exists()) {
//            throw new RuntimeException("币种主程序目录不存在：" + coinBasePath);
            return null;
        }
        String appVersionFolder = findAppVersionFolder(file);
        if (appVersionFolder == null) {
//            throw new RuntimeException("没找到该币种的版本目录，正常情况下版本目录应该是app-x.x.x格式的文件夹:" + file.getAbsolutePath());
//            return null;
        }
        if (appVersionFolder == null) {
            appVersionFolder = "";
        } else {
            appVersionFolder += File.separator;
        }
        File coinProgramPathFile = new File(coinBasePath + File.separator + appVersionFolder + "resources" + File.separator + "app.asar.unpacked" + File.separator + "daemon");
        if (!coinProgramPathFile.exists()) {
//            throw new RuntimeException("没有找到正确的主程序目录，可能该币种的主程序目录不规范:" + coinProgramPathFile.getAbsolutePath());
            return null;
        }

        this.cmdPath = coinProgramPathFile.getAbsolutePath();
        return this.cmdPath;
    }

    public String findAppVersionFolder(File file) {
        for (File f : file.listFiles()) {
            if (f.isDirectory() && f.getName().startsWith("app-")) {
                return f.getName();
            }
        }
        return null;
    }

    /**
     * 标准的kVX形式可以用该map指定对应关系，非规则的字段使用resolveLine单独解析
     *
     * @return
     */
    protected abstract Map<String, String> initFieldMap();

    //
    public Map<String, String> getFieldMap() {
        return fieldMap;
    }


    // 特殊情况自己解析
    public abstract void resolveLine(String line, int lineIndex) throws Exception;

    public void resolveLines(List<String> lines) {
        if (lines != null && lines.size() > 0) {
            originResult = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                originResult.add(line);
                try {
                    if (stopResolve) {
                    } else {
                        resolveLine(line, i);
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    public ChiaForkResult(String cmd, String coinTypeName) {
        this.cmd = cmd;
        this.coinTypeName = coinTypeName;
        this.fieldMap = initFieldMap();
        initProgramAbsolutePath();
    }

    public String getCmd() {

        return coinTypeName + " " + cmd;
    }

    public void setFieldMap(Map<String, String> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCoinTypeName() {
        return coinTypeName;
    }

    public void setCoinTypeName(String coinTypeName) {
        this.coinTypeName = coinTypeName;
    }

    public void stopResolve() {
        this.stopResolve = true;
    }

    public List<String> getOriginResult() {
        return originResult;
    }

    public void setOriginResult(List<String> originResult) {
        this.originResult = originResult;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isStopResolve() {
        return stopResolve;
    }

    public void setStopResolve(boolean stopResolve) {
        this.stopResolve = stopResolve;
    }

    public String getCmdPath() {
        return cmdPath;
    }

    public void setCmdPath(String cmdPath) {
        this.cmdPath = cmdPath;
    }

    public String getProgramFolderName() {
        return programFolderName;
    }

    public void setProgramFolderName(String programFolderName) {
        this.programFolderName = programFolderName;
    }
}
