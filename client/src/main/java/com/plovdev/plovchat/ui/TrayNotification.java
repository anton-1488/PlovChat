package com.plovdev.plovchat.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class TrayNotification {
    private static final Logger log = LoggerFactory.getLogger(TrayNotification.class);
    private TrayIcon trayIcon;
    private SystemTray tray;

    public TrayNotification() {
        if (SystemTray.isSupported()) {
            setupTrayIcon();
        } else {
            System.err.println("System tray не поддерживается");
        }
    }

    private void setupTrayIcon() {
        try {
            tray = SystemTray.getSystemTray();
            Image image = createTrayImage();

            PopupMenu popup = new PopupMenu();

            MenuItem exitItem = new MenuItem("Выход");
            exitItem.addActionListener(e -> exitApplication());

            popup.add(exitItem);

            trayIcon = new TrayIcon(image, "PlovChat", popup);
            trayIcon.setImageAutoSize(true);

            tray.add(trayIcon);

        } catch (AWTException e) {
            log.error("Error to create tray notification: ", e);
        }
    }

    public void showNotification(String title, String message) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
    }

    public void showInfoNotification(String title, String message) {
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
    }

    public void showWarningNotification(String title, String message) {
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.WARNING);
    }

    public void showErrorNotification(String title, String message) {
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.ERROR);
    }

    private Image createTrayImage() {
        int width = 16;
        int height = 16;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.BLACK);
        g.fillOval(0, 0, width, height);
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 7));
        g.drawString("PC", 3, 11);
        g.dispose();

        return image;
    }

    private void exitApplication() {
        tray.remove(trayIcon);
        System.exit(0);
    }
}