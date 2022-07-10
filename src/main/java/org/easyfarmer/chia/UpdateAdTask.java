package org.easyfarmer.chia;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.easyfarmer.chia.util.SwingUtils;

import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author liyifeng
 * @version 1.0
 * @date 2022/7/10 1:19 下午
 */
public class UpdateAdTask implements Runnable {

    @Override
    public void run() {

        while (true) {
            try {
                String jsonRespString = HttpUtil.get("https://fork-linux-file.c4dig.cn/chiaHelper/api/ad.json");
                if (StrUtil.isNotBlank(jsonRespString)) {
                    JSONObject json = JSON.parseObject(jsonRespString);
                    if (json != null && json.containsKey("code") && json.getInteger("code").intValue() == 200) {
                        if (json.containsKey("data")) {
                            JSONObject data = json.getJSONObject("data");

                            JSONObject ad = data.getJSONObject("topAd");
                            fillTopAd(ad);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(3600000L); //每个小时刷新一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fillTopAd(JSONObject ad) {
        if (ad != null) {
            Long expireMills = ad.getLong("expireMills");
            String imgUrl = ad.getString("imgUrl");
            String link = ad.getString("link");
            String title = ad.getString("title");
            // 没过期
            if (expireMills != null && System.currentTimeMillis() < expireMills.longValue()) {
                if (StrUtil.isNotBlank(imgUrl)) {
                    try {

// 该方法会将图像加载到内存，从而拿到图像的详细信息。

                    // TODO 待完成
                        String imgPath = null;
                        BufferedImage image = ImageIO.read(new FileInputStream(imgPath));
                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                    } catch (IOException e) {

                        e.printStackTrace();

                    }
                    APP.app.topAdLabel.setIcon(null);
                    APP.app.topAdLabel.setText("");
                }
                if (StrUtil.isNotBlank(link)) {
                    APP.app.topAdLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            SwingUtils.jump2Url(link);
                        }
                    });
                }
            }
        }
    }


}
