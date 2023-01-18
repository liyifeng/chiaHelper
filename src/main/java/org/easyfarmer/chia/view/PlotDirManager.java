/*
 * Created by JFormDesigner on Thu Dec 15 23:26:21 CST 2022
 */

package org.easyfarmer.chia.view;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * @author unknown
 */
public class PlotDirManager extends JFrame {
    public PlotDirManager() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        tabbedPane1 = new JTabbedPane();
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "hidemode 3",
            // columns
            "[fill]" +
            "[grow,fill]",
            // rows
            "[]" +
            "[]" +
            "[]"));

        //======== tabbedPane1 ========
        {

            //======== panel1 ========
            {
                panel1.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[grow,fill]" +
                    "[fill]" +
                    "[fill]",
                    // rows
                    "[]" +
                    "[]" +
                    "[]"));

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(list1);
                }
                panel1.add(scrollPane1, "cell 0 0 3 1");

                //---- button1 ----
                button1.setText("\u81ea\u52a8\u641c\u7d22");
                panel1.add(button1, "cell 0 1,alignx right,growx 0");

                //---- button2 ----
                button2.setText("\u6279\u91cf\u6dfb\u52a0");
                panel1.add(button2, "cell 1 1");

                //---- button3 ----
                button3.setText("\u81ea\u52a8\u6dfb\u52a0");
                panel1.add(button3, "cell 2 1");
            }
            tabbedPane1.addTab("\u56fe\u7ba1\u7406", panel1);
        }
        contentPane.add(tabbedPane1, "cell 0 0 2 2");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JList list1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
