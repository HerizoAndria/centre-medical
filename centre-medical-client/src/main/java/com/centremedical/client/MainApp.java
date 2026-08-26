package com.centremedical.client;

import com.centremedical.client.ui.AppTheme;
import com.centremedical.client.ui.MainFrame;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        AppTheme.install();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
