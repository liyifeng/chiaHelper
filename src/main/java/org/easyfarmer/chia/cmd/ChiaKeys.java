package org.easyfarmer.chia.cmd;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author liyifeng
 * @version 1.0
 * @date 2021/11/28 10:52 下午
 */
public class ChiaKeys {
    private String fingerprint;
    private String masterPublicKey;
    private String farmerPublicKey;
    private String poolPublicKey;
    private String firstWalletAddress;

    private String puzzleHash; // 方便监控时候使用

    private List<String> keywords = new ArrayList<>(24);

    public String getPuzzleHash() {
        return puzzleHash;
    }

    public void setPuzzleHash(String puzzleHash) {
        this.puzzleHash = puzzleHash;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getMasterPublicKey() {
        return masterPublicKey;
    }

    public void setMasterPublicKey(String masterPublicKey) {
        this.masterPublicKey = masterPublicKey;
    }

    public String getFarmerPublicKey() {
        return farmerPublicKey;
    }

    public void setFarmerPublicKey(String farmerPublicKey) {
        this.farmerPublicKey = farmerPublicKey;
    }

    public String getPoolPublicKey() {
        return poolPublicKey;
    }

    public void setPoolPublicKey(String poolPublicKey) {
        this.poolPublicKey = poolPublicKey;
    }

    public String getFirstWalletAddress() {
        return firstWalletAddress;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setFirstWalletAddress(String firstWalletAddress) {
        this.firstWalletAddress = firstWalletAddress;
    }

    @Override
    public String toString() {
        return fingerprint;
    }
}
