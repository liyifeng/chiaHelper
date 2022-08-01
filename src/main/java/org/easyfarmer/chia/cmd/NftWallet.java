package org.easyfarmer.chia.cmd;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Wallet id 6:
 * Current state: SELF_POOLING
 * Current state from block height: 1595774
 * Launcher ID: 23c8fd225a7559a6131808ad8ba7a0e1ec7ca61eabae6c364e604912cf2f5133
 * Target address (not for plotting): xch1n8hqu9ztr3dkf2jvn8xvwafmvtth0a6ksue8t74cevt4k5eg872sj6qlr5
 * Number of plots: 0
 * Owner public key: 815a3631df98795d0c481b6840053e5fc02f7b8e473079194fd41a84ce6fcdebba5c5da129a6a52fd151466966bb509a
 * Pool contract address (use ONLY for plotting - do not send money to this address): xch1x4zvuhp47jsam38nh5wtxug7ztjdmu0c0r5n259z4hhzukzh4etsyu6g07
 * Claimable balance: 0.0 xch (0 mojo)
 *
 * @author liyifeng
 * @version 1.0
 * @date 2022/7/10 10:50 下午
 */
public class NftWallet extends ChiaForkResult {
    private Integer walletId;
    private String currentState;
    private Long currentStateFromBlockHeight;
    private String launcherId;
    private String targetAddress;
    private String numberOfPlots;
    private String ownerPublicKey;
    private String poolContractAddress;
    private Long claimableBalanceMojo;
    private BigDecimal claimableBalanceXch;

    public NftWallet(String cmd) {
        super(cmd, "chia");
    }

    @Override
    protected Map<String, String> initFieldMap() {
        Map<String, String> map = new HashMap<>();
        map.put("currentState", "Current state");
        map.put("launcherId", "Launcher ID");
        map.put("numberOfPlots", "Number of plots");
        map.put("currentStateFromBlockHeight", "Current state from block height");
        map.put("targetAddress", "Target address (not for plotting)");
        map.put("ownerPublicKey", "Owner public key");
        map.put("poolContractAddress", "Pool contract address (use ONLY for plotting - do not send money to this address)");
        return map;
    }

    /**
     * Wallet id 6:
     * Claimable balance: 0.0 xch (0 mojo)
     *
     * @param line
     * @param lineIndex
     * @throws Exception
     */
    @Override
    public void resolveLine(String line, int lineIndex) throws Exception {
        if (StrUtil.isBlank(line)) {
            return;
        }
        if (line.contains("Wallet id")) {
            String walletIdStr = StrUtil.subBetween(line, "Wallet id ", ":");
            if (StrUtil.isNotBlank(walletIdStr) && NumberUtil.isNumber(walletIdStr)) {
                this.walletId = Integer.parseInt(walletIdStr);
            }
        } else if (line.contains("Claimable balance")) {
            String xchNumStr = StrUtil.subBetween(line, "Claimable balance: ", "xch");
            if (StrUtil.isNotBlank(xchNumStr)) {
                xchNumStr = xchNumStr.trim();
                if ("0.0".equals(xchNumStr)) {
                    this.claimableBalanceXch = new BigDecimal("0");
                } else {
                    try {
                        this.claimableBalanceXch = new BigDecimal(xchNumStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            String mojoNumStr = StrUtil.subBetween(line, "(", "mojo)");
            if (StrUtil.isNotBlank(mojoNumStr)) {
                mojoNumStr = mojoNumStr.trim();
                if ("0".equals(mojoNumStr)) {
                    this.claimableBalanceMojo = 0L;
                } else {
                    try {
                        this.claimableBalanceMojo = Long.parseLong(mojoNumStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Integer getWalletId() {
        return walletId;
    }

    public void setWalletId(Integer walletId) {
        this.walletId = walletId;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public Long getCurrentStateFromBlockHeight() {
        return currentStateFromBlockHeight;
    }

    public void setCurrentStateFromBlockHeight(Long currentStateFromBlockHeight) {
        this.currentStateFromBlockHeight = currentStateFromBlockHeight;
    }

    public String getLauncherId() {
        return launcherId;
    }

    public void setLauncherId(String launcherId) {
        this.launcherId = launcherId;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public String getNumberOfPlots() {
        return numberOfPlots;
    }

    public void setNumberOfPlots(String numberOfPlots) {
        this.numberOfPlots = numberOfPlots;
    }

    public String getOwnerPublicKey() {
        return ownerPublicKey;
    }

    public void setOwnerPublicKey(String ownerPublicKey) {
        this.ownerPublicKey = ownerPublicKey;
    }

    public String getPoolContractAddress() {
        return poolContractAddress;
    }

    public void setPoolContractAddress(String poolContractAddress) {
        this.poolContractAddress = poolContractAddress;
    }

    public Long getClaimableBalanceMojo() {
        return claimableBalanceMojo;
    }

    public void setClaimableBalanceMojo(Long claimableBalanceMojo) {
        this.claimableBalanceMojo = claimableBalanceMojo;
    }

    public BigDecimal getClaimableBalanceXch() {
        return claimableBalanceXch;
    }

    public void setClaimableBalanceXch(BigDecimal claimableBalanceXch) {
        this.claimableBalanceXch = claimableBalanceXch;
    }

    @Override
    public String toString() {
        return "NftWallet{" +
                "walletId=" + walletId +
                ", currentState='" + currentState + '\'' +
                ", currentStateFromBlockHeight=" + currentStateFromBlockHeight +
                ", launcherId='" + launcherId + '\'' +
                ", targetAddress='" + targetAddress + '\'' +
                ", numberOfPlots='" + numberOfPlots + '\'' +
                ", ownerPublicKey='" + ownerPublicKey + '\'' +
                ", poolContractAddress='" + poolContractAddress + '\'' +
                ", claimableBalanceMojo=" + claimableBalanceMojo +
                ", claimableBalanceXch=" + claimableBalanceXch +
                '}';
    }
}
