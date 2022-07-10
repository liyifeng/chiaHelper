package org.easyfarmer.chia.cmd;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fingerprint: 2959519667
 * Master public key (m): b837626666fb6bcbec7ad23e86082f90cf69b617f950ab9d62d3284fbe860e8fc927d432b0556803770b1a13b04749ff
 * Farmer public key (m/12381/8444/0/0): 89f503ccdd9635d4c29e9bda2b9381d25a8518ea18012ea6c755273cee2fad6fd2b5d5b18caecfe7e87e9a529224d8a5
 * Pool public key (m/12381/8444/1/0): b26ce75657b25e73386de98292520da9e552acac35d4830dac2429fe1d9c4d758a1bad5cf0edb12721e7ffd75e29ef27
 * First wallet address: xch1z6nu6nf8dqrjcn6smnmgczqljghgendazve9953dw2qynmruk54qals56z
 * chia keys show 的运行结果
 *
 * @author liyifeng
 * @version 1.0
 * @date 2021/11/28 10:51 下午
 */
public class KeysShow extends ChiaForkResult {

    private List<ChiaKeys> keyList = new ArrayList<>();

    public KeysShow(String coinTypeName, boolean includeKeyword) {
        super("keys show " + (includeKeyword ? " --show-mnemonic-seed" : ""), coinTypeName);
        setProgramFolderName("chia-blockchain");
        super.initProgramAbsolutePath();
    }

    public List<ChiaKeys> getKeyList() {
        return keyList;
    }

    public void setKeyList(List<ChiaKeys> keyList) {
        this.keyList = keyList;
    }

    @Override
    protected Map<String, String> initFieldMap() {
//        Map<String,String> map = new HashMap<>();
//        map.put("fingerprint","Fingerprint");
//        map.put("firstWalletAddress","First wallet address");
//        return map;
        return null;
    }

    private ChiaKeys temp;

    private boolean findKeyWord = false;
    @Override
    public void resolveLine(String line,int lineIndex) throws Exception {
        if (StrUtil.isNotBlank(line)) {
            if(findKeyWord){
                findKeyWord = false;
                List<String> keyWordsList = resolveKeywordsLine(line);
                temp.setKeywords(keyWordsList);
            }
            if (line.contains("Fingerprint")) {
                temp = new ChiaKeys();
                keyList.add(temp);
                temp.setFingerprint(line.split(":")[1].trim());
            } else if (line.contains("Master public key")) {
                temp.setMasterPublicKey(line.split(":")[1].trim());
            } else if (line.contains("Farmer public key")) {
                temp.setFarmerPublicKey(line.split(":")[1].trim());
            } else if (line.contains("Pool public key")) {
                temp.setPoolPublicKey(line.split(":")[1].trim());
            } else if (line.contains("First wallet address")) {
                temp.setFirstWalletAddress(line.split(":")[1].trim());
            }else if (line.contains("Mnemonic seed (24 secret words)")) {
                findKeyWord = true;
            }else{


            }
        }
    }

    public static List<String> resolveKeywordsLine(String words) {
        List<String> keyWordList = new ArrayList<>();
        String[] newKeyWords = words.trim().split("\\s+");
        for (String word : newKeyWords) {
            if (StrUtil.isNotBlank(word)) {

                keyWordList.add(word);
            }
        }
        return keyWordList;
    }
}
