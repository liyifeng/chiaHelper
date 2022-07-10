package org.easyfarmer.chia;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.easyfarmer.chia.util.SwingUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

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
            String tip = ad.getString("tip");
            // 没过期
            if (expireMills != null && System.currentTimeMillis() < expireMills.longValue()) {
                if (StrUtil.isNotBlank(imgUrl)) {
                    try {
                        BufferedImage image = ImageIO.read(new URL(imgUrl));
                        APP.app.topAdLabel.setIcon(new ImageIcon(image));
                        //BufferedImage image = ImageIO.read(new FileInputStream(imgPath));
                    } catch (Exception e) {
                        e.printStackTrace();
                        APP.app.topAdLabel.setText(title);
                    }
                }
                if (StrUtil.isNotBlank(link)) {
                    APP.app.topAdLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            SwingUtils.jump2Url(link);
                        }
                    });
                    APP.app.topAdLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }else{
                    APP.app.topAdLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                if(StrUtil.isNotBlank(tip)){
                    APP.app.topAdLabel.setToolTipText(tip);
                }
            }
            APP.app.pack();
        }
    }


}
