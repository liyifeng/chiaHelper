/*
 * Created by JFormDesigner on Sat Jul 09 20:21:57 CST 2022
 */

package org.easyfarmer.chia.view;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatLightLaf;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import net.miginfocom.swing.MigLayout;
import org.easyfarmer.chia.CheckWallet2Transfer;
import org.easyfarmer.chia.util.ConfigUtils;
import org.easyfarmer.chia.UpdateAdTask;
import org.easyfarmer.chia.util.ChiaUtils;
import org.easyfarmer.chia.util.Constant;
import org.easyfarmer.chia.util.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * @author li yifeng
 */
public class AutoTransferDialog extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(AutoTransferDialog.class);

    public static AutoTransferDialog autoTransferDialog;

    public AutoTransferDialog() {

        System.setProperty("log4j2.configurationFile", "log4j2.xml");

        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            //logger.error("设置主题失败", ex);
        }
        setSize(new Dimension(565, 408));
        //addComponentListener(new ComponentAdapter() {
        //    @Override
        //    public void componentResized(ComponentEvent e) {
        //        System.out.println("窗口大小：" + getSize());
        //    }
        //});
        initComponents();
        //setTitle("奇亚钱包自动转账工具 - www.easyfarmer.org出品");
        loadLastConfigContent();
        autoClaimCheckBox.setSelected(true);
        sourceUrlLabel.setForeground(Color.BLUE);
        sourceUrlLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtils.jump2Url("https://github.com/liyifeng/chiaHelper");
            }
        });
        sourceUrlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (Constant.test) {
            chiaWalletAddressTextField.setText("xch1z6nu6nf8dqrjcn6smnmgczqljghgendazve9953dw2qynmruk54qals56z");
        }


    }

    public static void main(String[] args) {
        autoTransferDialog = new AutoTransferDialog();
        autoTransferDialog.loadFingerprint();

        Thread checkWalletThread = new Thread(new CheckWallet2Transfer());
        checkWalletThread.start();
        Thread updateAdThread = new Thread(new UpdateAdTask());
        updateAdThread.start();

        autoTransferDialog.setVisible(true);
        autoTransferDialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }


    String fingerprintDefaultValue = "读取失败";

    private void loadFingerprint() {
        String respJsonStr = ChiaUtils.get_logged_in_fingerprint();
        String fingerprint = fingerprintDefaultValue;
        if (respJsonStr != null) {
            try {
                JSONObject json = JSON.parseObject(respJsonStr);
                if (json != null && json.containsKey("success") && json.getBoolean("success") && json.containsKey("fingerprint")) {
                    fingerprint = json.getString("fingerprint");
                }

            } catch (Exception e) {
                logger.warn("加载指纹出错", e);
                e.printStackTrace();
            }
        }
        fingerprintValue.setText(fingerprint);
    }

    String startBtnText = "开启自动转账";
    String stopBtnText = "停止自动转账";

    private void button1(ActionEvent e) {
        if (button1.getText().equals(startBtnText)) { //开启功能
            File path = null;
            try {
                path = ChiaUtils.getChiaCmdPathFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (path == null) {
                addLog("本地未检测到奇亚钱包客户端！");
                return;
            }

            // 检查地址长度
            String targetAddress = chiaWalletAddressTextField.getText();
            if (StrUtil.isBlank(targetAddress)) {
                JOptionPane.showMessageDialog(this, "先设置自动转账的目标地址才能启动！");
                return;
            }
            targetAddress = targetAddress.trim();
            if (StrUtil.length(targetAddress) != 62) {
                JOptionPane.showMessageDialog(this, "目标地址长度有误！");
                return;
            }

            String feeText = feeTextField.getText();
            long fee = Constant.DEFAULT_TRANSFER_FEE;
            if (StrUtil.isBlank(feeText) || !NumberUtil.isNumber(feeText)) {
                try {
                    int feeValue = Integer.parseInt(feeText);
                    if (feeValue <= 0) {
                        JOptionPane.showMessageDialog(this, "转账手续费的单位是mojo，不能填写负数或小数！");
                        return;
                    }
                } catch (Exception ex) {
                    logger.error("报错", ex);
                }
                addLog("转账手续费未设置，自动设置为默认:" + Constant.DEFAULT_TRANSFER_FEE);
                feeTextField.setText(Constant.DEFAULT_TRANSFER_FEE + "");
            } else {
                fee = NumberUtil.toBigInteger(feeText).intValue();
            }
            String fingerprint = fingerprintValue.getText();
            if (fingerprintDefaultValue.equals(fingerprint) || !NumberUtil.isNumber(fingerprint)) {
                JOptionPane.showMessageDialog(this, "未读取到指纹信息，请确保奇亚客户端已经启动！");
                return;
            }

            // 写入配置文件
            ConfigUtils.save(targetAddress, "" + fee);

            CheckWallet2Transfer.startMonitor(targetAddress, fingerprint, fee, autoClaimCheckBox.isSelected());
            setFormEnable(false);
            button1.setText(stopBtnText);
            statusValueLabel.setText("运行中");
            statusValueLabel.setForeground(Color.GREEN);
            addLog("开始监控钱包余额。");
        } else if (button1.getText().equals(stopBtnText)) { //关闭功能
            set2StopMonitor();
        }
    }

    public void set2StopMonitor() {
        CheckWallet2Transfer.stopMonitor();
        setFormEnable(true);
        statusValueLabel.setText("已停止");
        statusValueLabel.setForeground(Color.RED);
        addLog("停止监控。");
        button1.setText(startBtnText);
    }

    private void setFormEnable(boolean enable) {
        chiaWalletAddressTextField.setEnabled(enable);
        autoClaimCheckBox.setEnabled(enable);
        feeTextField.setEnabled(enable);
    }

    private void loadLastConfigContent() {
        String lastAddress = ConfigUtils.getReceivedAddress();
        Integer lastFee = ConfigUtils.getFee();
        chiaWalletAddressTextField.setText(lastAddress);

        feeTextField.setText((lastFee != null ? lastFee : Constant.DEFAULT_TRANSFER_FEE) + "");
    }


    public void addLog(String log) {
        logger.info(log);
        logTextArea.append(DateUtil.now() + " " + log + "\n");
    }

    private void showCursor(MouseEvent e) {
        JLabel label = (JLabel) e.getSource();
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void removeCursor(MouseEvent e) {
        JLabel label = (JLabel) e.getSource();
        label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void sourceUrlLabelMouseEntered(MouseEvent e) {
        showCursor(e);
    }

    private void sourceUrlLabelMouseExited(MouseEvent e) {
        removeCursor(e);
    }

    private void label6MouseEntered(MouseEvent e) {
        showCursor(e);
    }

    private void label6MouseExited(MouseEvent e) {
        removeCursor(e);
    }

    private void sourceUrlLabelMouseClicked(MouseEvent e) {
        SwingUtils.jump2Url("https://github.com/liyifeng/chiaHelper");
    }

    private void label6MouseClicked(MouseEvent e) {
        SwingUtils.jump2Url("http://www.easyfarmer.org");

    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
        label3 = new JLabel();
        statusValueLabel = new JLabel();
        topAdLabel = new JLabel();
        fingerPrintLabel = new JLabel();
        fingerprintValue = new JLabel();
        autoClaimCheckBox = new JCheckBox();
        targetChiaAddressLabel = new JLabel();
        chiaWalletAddressTextField = new JTextField();
        feeLabel = new JLabel();
        feeTextField = new JTextField();
        label2 = new JLabel();
        button1 = new JButton();
        separator1 = compFactory.createSeparator("\u64cd\u4f5c\u8bb0\u5f55", SwingConstants.CENTER);
        scrollPane1 = new JScrollPane();
        logTextArea = new JTextArea();
        label4 = new JLabel();
        label5 = new JLabel();
        sourceUrlLabel = new JLabel();
        label1 = new JLabel();
        websiteLabel = new JLabel();

        //======== this ========
        setTitle("\u5947\u4e9a\u94b1\u5305\u81ea\u52a8\u8f6c\u8d26\u5de5\u5177 v0.1.5      2023.1.18");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "hidemode 3",
            // columns
            "[fill]" +
            "[200:250:500,fill]" +
            "[250:250:500,fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[100:n:1500,grow,fill]" +
            "[]" +
            "[]" +
            "[]"));

        //---- label3 ----
        label3.setText("\u76d1\u63a7\u72b6\u6001\uff1a");
        contentPane.add(label3, "cell 0 1,alignx right,growx 0");

        //---- statusValueLabel ----
        statusValueLabel.setText("-");
        contentPane.add(statusValueLabel, "cell 1 1");

        //---- topAdLabel ----
        topAdLabel.setIcon(new ImageIcon(getClass().getResource("/img/topAdDefaultImg.jpg")));
        contentPane.add(topAdLabel, "hidemode 3,cell 0 0 3 1,alignx center,grow 0 100");

        //---- fingerPrintLabel ----
        fingerPrintLabel.setText("\u76d1\u63a7\u6307\u7eb9\uff1a");
        contentPane.add(fingerPrintLabel, "cell 0 2,alignx right,growx 0");

        //---- fingerprintValue ----
        fingerprintValue.setText("123123123");
        contentPane.add(fingerprintValue, "cell 1 2,alignx left,growx 0");

        //---- autoClaimCheckBox ----
        autoClaimCheckBox.setText("\u81ea\u52a8\u8ba4\u9886\u5956\u52b1");
        contentPane.add(autoClaimCheckBox, "cell 2 2");

        //---- targetChiaAddressLabel ----
        targetChiaAddressLabel.setText("\u8f6c\u5230\u76ee\u6807\u94b1\u5305\u5730\u5740\uff1a");
        contentPane.add(targetChiaAddressLabel, "cell 0 3,alignx right,growx 0");
        contentPane.add(chiaWalletAddressTextField, "cell 1 3 2 1,growx");

        //---- feeLabel ----
        feeLabel.setText("\u8f6c\u8d26\u624b\u7eed\u8d39\uff1a");
        contentPane.add(feeLabel, "cell 0 4,alignx right,growx 0");

        //---- feeTextField ----
        feeTextField.setToolTipText("\u5355\u4f4d\u662fmojo\uff0c\u7ed9\u4e9b\u624b\u7eed\u8d39\u53ef\u4ee5\u52a0\u5feb\u786e\u8ba4\u901f\u5ea6\uff0c\u4f8b\u5982\u586b\uff1a1\u8868\u793a0.000000000001xch\u3002");
        contentPane.add(feeTextField, "cell 1 4,grow");

        //---- label2 ----
        label2.setText("\u5355\u4f4d\uff1amojo");
        label2.setToolTipText("\u6ce8\u610f\u5355\u4f4d\u662fmojo");
        contentPane.add(label2, "cell 2 4");

        //---- button1 ----
        button1.setText("\u5f00\u542f\u81ea\u52a8\u8f6c\u8d26");
        button1.addActionListener(e -> button1(e));
        contentPane.add(button1, "cell 0 5 3 1,alignx center,growx 0");
        contentPane.add(separator1, "cell 0 6 3 1");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(logTextArea);
        }
        contentPane.add(scrollPane1, "cell 0 7 3 1,grow");

        //---- label4 ----
        label4.setText("\u672c\u5de5\u5177\u53ea\u9002\u7528Windows\u7cfb\u7edf\uff0c\u5df2\u5f00\u6e90\u3001\u6709\u5e7f\u544a\u3002");
        contentPane.add(label4, "cell 0 8 3 1");

        //---- label5 ----
        label5.setText("\u6e90\u7801\uff1a");
        contentPane.add(label5, "cell 0 9 3 1,alignx left,growx 0");

        //---- sourceUrlLabel ----
        sourceUrlLabel.setText("https://github.com/liyifeng/chiaHelper");
        sourceUrlLabel.setForeground(Color.blue);
        sourceUrlLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sourceUrlLabelMouseClicked(e);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                sourceUrlLabelMouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                sourceUrlLabelMouseExited(e);
            }
        });
        contentPane.add(sourceUrlLabel, "cell 0 9 3 1");

        //---- label1 ----
        label1.setText("\u5b98\u7f51\uff1a");
        contentPane.add(label1, "cell 0 10 2 1,alignx left,growx 0");

        //---- websiteLabel ----
        websiteLabel.setText("http://www.easyfarmer.org");
        websiteLabel.setForeground(Color.blue);
        websiteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                label6MouseClicked(e);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                label6MouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                label6MouseExited(e);
            }
        });
        contentPane.add(websiteLabel, "cell 0 10 2 1");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label3;
    private JLabel statusValueLabel;
    public JLabel topAdLabel;
    private JLabel fingerPrintLabel;
    private JLabel fingerprintValue;
    private JCheckBox autoClaimCheckBox;
    private JLabel targetChiaAddressLabel;
    private JTextField chiaWalletAddressTextField;
    private JLabel feeLabel;
    private JTextField feeTextField;
    private JLabel label2;
    private JButton button1;
    private JComponent separator1;
    private JScrollPane scrollPane1;
    private JTextArea logTextArea;
    private JLabel label4;
    private JLabel label5;
    private JLabel sourceUrlLabel;
    private JLabel label1;
    private JLabel websiteLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
