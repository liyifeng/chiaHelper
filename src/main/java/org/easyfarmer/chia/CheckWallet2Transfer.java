package org.easyfarmer.chia;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.easyfarmer.chia.util.ChiaUtils;

/**
 * @author liyifeng
 * @version 1.0
 * @date 2022/7/9 10:32 下午
 */
public class CheckWallet2Transfer implements Runnable {

    private static boolean monitor = false;
    private static String targetWalletAddress;
    private static Integer transferFee; //转账手续费

    public static void stopMonitor() {
        targetWalletAddress = null;
        monitor = false;
    }

    public static void startMonitor(String walletAddress, Integer fee) {
        monitor = true;
        targetWalletAddress = walletAddress;
        transferFee = fee;
    }

    @Override
    public void run() {
        while (true) {
            try {
//                APP.app.addLog("check...");
                if (!monitor || targetWalletAddress == null || targetWalletAddress.trim().length() != 62) {
                    continue;
                }

                JSONObject json = ChiaUtils.get_wallets();
                Integer walletId = getChiaWalletId(json);
                if (walletId == null) {
                    //logger.warn("获取钱包账户ID失败:{}", json);
                    continue;
                }

                JSONObject walletBalanceJson = ChiaUtils.get_wallet_balance(walletId);
                if (chiaRpcSuccess(walletBalanceJson) && walletBalanceJson.containsKey("wallet_balance")) {
                    JSONObject balanceDataJson = walletBalanceJson.getJSONObject("wallet_balance");
                    Long balance = balanceDataJson.getLong("confirmed_wallet_balance");
                    if (balance > 0) {
                        //钱包账户有余额
                        ChiaUtils.transfer(targetWalletAddress, balance, transferFee);
                    }

                }
            } catch (Exception e) {
                //logger.error("报错", e);
            } finally {
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Integer getChiaWalletId(JSONObject json) {
        // 执行成功
        if (chiaRpcSuccess(json) && json.containsKey("wallets")) {
            JSONArray walletArray = json.getJSONArray("wallets");
            if (walletArray != null) {
                for (Object o : walletArray) {
                    JSONObject j = (JSONObject) o;
                    if (j.getInteger("type") != null && j.getInteger("type").intValue() == 0) {
                        return j.getInteger("id");
                    }
                }
            }
        }

        return null;
    }

    private boolean chiaRpcSuccess(JSONObject json) {
        return json != null && json.containsKey("success") && json.getBoolean("success");
    }
    // 获得钱包列表
    //            chia rpc wallet get_wallets
    //            {
    //                "success": true,
    //                    "wallets": [
    //                {
    //                    "data": "",
    //                        "id": 1,
    //                        "name": "Chia Wallet",
    //                        "type": 0
    //                },
    //                {
    //                    "data": "",
    //                        "id": 2,
    //                        "name": "Pool wallet",
    //                        "type": 9
    //                },
    //                {
    //                    "data": "",
    //                        "id": 3,
    //                        "name": "Pool wallet",
    //                        "type": 9
    //                },
    //                {
    //                    "data": "",
    //                        "id": 4,
    //                        "name": "Pool wallet",
    //                        "type": 9
    //                },
    //                {
    //                    "data": "509deafe3cd8bbfbb9ccce1d930e3d7b57b40c964fa33379b18d628175eb7a8f00",
    //                        "id": 5,
    //                        "name": "Chia Holiday 2021 Token",
    //                        "type": 6
    //                },
    //                {
    //                    "data": "",
    //                        "id": 6,
    //                        "name": "Pool wallet",
    //                        "type": 9
    //                }
    //]
    //            }
}
