package com.plovdev.plovchat.utils;

import com.plovdev.plovchat.PlovChatApp;
import com.plovdev.plovchat.ui.TrayNotification;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

public class Utils {
    private static final Preferences prefs = Preferences.userNodeForPackage(PlovChatApp.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final TrayNotification notification = new TrayNotification();
    private static final String local = "localhost";
    private static final String global = "217.26.27.252";

    public static boolean validatePassword(String text) {
        boolean space = text.contains(" ");
        boolean length = text.length() >= 5;

        return !space && length;
    }

    public static String getFromPrefs(String key, String def) {
        return prefs.get(key, def);
    }
    public static void putToPrefs(String key, String val) {
        prefs.put(key, val);
    }
    public static void putBoolean(String key, boolean val) {
        prefs.putBoolean(key, val);
    }
    public static boolean getBoolean(String key, boolean def) {
        return prefs.getBoolean(key, def);
    }

    public static String fromatDate(long time) {
        LocalDateTime dateTime = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return dateTime.format(formatter);
    }

    public static Region hReg() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }
    public static Region vReg() {
        Region region = new Region();
        VBox.setVgrow(region, Priority.ALWAYS);
        return region;
    }

    public static void copy(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(text);
        clipboard.setContents(selection, null);
    }

    public static void notification(String title, String text) {
        notification.showInfoNotification(title, text);
    }

    public static String getServer() {
        return global;
    }
}