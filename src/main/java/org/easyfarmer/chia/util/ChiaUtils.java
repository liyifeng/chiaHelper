package org.easyfarmer.chia.util;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.easyfarmer.chia.cmd.ChiaForkResult;
import org.easyfarmer.chia.cmd.ChiaKeys;
import org.easyfarmer.chia.cmd.ContentBuilder;
import org.easyfarmer.chia.cmd.KeysShow;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liyifeng
 * @version 1.0
 * @date 2021/10/21 10:45 上午
 */
public class ChiaUtils {

    public static String get_logged_in_fingerprint() {
        return execChiaRpcCmd("chia rpc wallet get_logged_in_fingerprint");
    }


    public static String list2String(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        if (lines != null) {
            for (String line : lines) {
                sb.append(line);
            }
        }

        return sb.toString();
    }


    public static File getChiaCmdPathFile() {
        return getCmdPathByProgramPath("chia-blockchain");
    }

    public static File getCmdPathByProgramPath(String programFolderName) {
        if (StrUtil.isBlank(programFolderName)) {
            //throw new RuntimeException("主程序目录名称为空，无法获取该币种的完整程序目录");
            return null;
        }
        String coinBasePath = String.format("%s\\AppData\\Local\\%s", System.getProperty("user.home"), programFolderName);
        File file = new File(coinBasePath);
        if (!file.exists()) {
            throw new RuntimeException("币种主程序目录不存在：" + coinBasePath);
        }
        String appVersionFolder = findAppVersionFolder(file);
        if (appVersionFolder == null) {
            throw new RuntimeException("没找到该币种的版本目录，正常情况下版本目录应该是app-x.x.x格式的文件夹:" + file.getAbsolutePath());
        }

        File coinProgramPathFile = new File(coinBasePath + File.separator + appVersionFolder, "resources" + File.separator + "app.asar.unpacked" + File.separator + "daemon");
        if (!coinProgramPathFile.exists()) {
            throw new RuntimeException("没有找到正确的主程序目录，可能该币种的主程序目录不规范:" + coinProgramPathFile.getAbsolutePath());
        }

        return coinProgramPathFile;
    }

    public static String findAppVersionFolder(File file) {
        for (File f : file.listFiles()) {
            if (f.isDirectory() && StrUtil.startWith(f.getName(), "app-")) {
                return f.getName();
            }
        }
        return null;
    }

    public static String getPlotIdByPlotName(String plotName) {
        if (StrUtil.isBlank(plotName)) {
            return null;
        }
        int end = plotName.contains(".") ? plotName.indexOf(".") : plotName.length();
        return plotName.substring(plotName.lastIndexOf("-") + 1, end);
    }

    private static final BigDecimal multiplicandToMills = new BigDecimal("1000");

    /**
     * 将日志中解析到的秒值解析成毫秒值
     */
    public static Long formatMillsFromStr(String seconds) {
        if (StrUtil.isBlank(seconds)) {
            return null;
        }
        return new BigDecimal(seconds.trim()).multiply(multiplicandToMills).longValue();
    }

    public static List<ChiaKeys> readChiaKeys() {
        KeysShow show = new KeysShow("chia", false);
        try {
            exec4Result(show);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (show != null) {
            return show.getKeyList();
        }
        return new ArrayList<>();
    }

    public static <T extends ChiaForkResult> T exec4Result(T obj) throws Exception {
        if (obj.getCmdPath() == null) {
            return null;
        }
        List<String> lines = CommandUtils.exec(obj.getCmdPath() + File.separator + obj.getCmd(), null, null, true);
        ContentBuilder.setValue(obj, lines);
        obj.setSuccess(true);
        return obj;
    }

    public static JSONObject get_wallets() {
        String resp = execChiaRpcCmd("chia rpc wallet get_wallets");
        if (resp != null) {
            return JSON.parseObject(resp);
        }
        return null;
    }

    public static JSONObject get_wallet_balance(Integer walletId) {
        String resp = execChiaRpcCmd("chia rpc wallet get_wallet_balance {\\\"wallet_id\\\":" + walletId + "}");
        if (resp != null) {
            return JSON.parseObject(resp);
        }
        return null;

    }

    private static String execChiaRpcCmd(String cmd) {
        try {
            List<String> list = CommandUtils.exec(cmd, null, getChiaCmdPathFile());
            return list2String(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ./chia wallet send -f 2959519667  -a 0.1 -m 0.000000000001 -t xch1mhfe8har266ap9u3gh2qeqmxhqzvk4ramqxp7glnwu2drskgs4yqulfa29
     * Submitting transaction...
     * Transaction submitted to nodes: [{'peer_id': '587348e668165bafae3038105a0c08de25e333167ecc1d464c17be9d66276045', 'inclusion_status': 'SUCCESS', 'error_msg': None}]
     * Run 'chia wallet get_transaction -f 2959519667 -tx 0x90b981bd812a1f37a5403babd9d0dc602d62c39abd42c00099b7977c6f8d3acc' to get status
     *
     * @param targetWalletAddress
     * @param balance
     * @param transferFee
     */
    public static List<String> transfer(String fingerprint, String targetWalletAddress, String balance, String transferFee) {
        try {
            String cmd = String.format("chia wallet send -f %s  -a %s -m %s -t %s", fingerprint, balance, (transferFee == null ? "0" : transferFee), targetWalletAddress);
            System.out.println(String.format("向%s转账%d，费用：%d", targetWalletAddress, balance, transferFee));
            List<String> list = CommandUtils.exec(cmd, null, getChiaCmdPathFile());

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static BigDecimal CHIA_DIVISOR = new BigDecimal("1000000000000");

    public static String mojo2xch(BigDecimal mojoCount) {
        return new DecimalFormat("0.000000000000").format(mojoCount.divide(CHIA_DIVISOR, 12, RoundingMode.HALF_UP));
    }

    public static String xch2mojo(BigDecimal xch) {
        return new DecimalFormat("0.000000000000").format(xch.multiply(CHIA_DIVISOR));
    }

    public static void main(String[] args) {
        Long mojo = 22360035380619L;
        System.out.println(new BigDecimal(mojo).longValue());
        System.out.println(mojo2xch(new BigDecimal(mojo)));
        String fee = ChiaUtils.mojo2xch(new BigDecimal(3L));
        System.out.println(fee);
    }


    //  Usage: chia wallet send [OPTIONS]
    //
    //
    //-f, --fingerprint INTEGER       Set the fingerprint to specify which wallet
    //  to use
    //
    //-i, --id INTEGER                Id of the wallet to use  [default: 1;
    //  required]
    //
    //          -a, --amount TEXT               How much chia to send, in XCH  [required]
    //          -e, --memo TEXT                 Additional memo for the transaction
    //-m, --fee TEXT                  Set the fees for the transaction, in XCH
    //                                [default: 0; required]
    //
    //          -t, --address TEXT              Address to send the XCH  [required]
    //          -o, --override                  Submits transaction without checking for
    //  unusual values


    //查询余额
    //chia rpc wallet get_wallet_balance "{\"wallet_id\":1}"
    //{
    //    "success": true,
    //        "wallet_balance": {
    //    "confirmed_wallet_balance": 22460035380620,
    //            "fingerprint": 2959519667,
    //            "max_send_amount": 22460035380620,
    //            "pending_change": 0,
    //            "pending_coin_removal_count": 0,
    //            "spendable_balance": 22460035380620,
    //            "unconfirmed_wallet_balance": 22460035380620,
    //            "unspent_coin_count": 98,
    //            "wallet_id": 1
    //}
    //}
}
