package com.plovdev.plovchat;

import com.plovdev.plovchat.scenes.BaseScene;
import com.plovdev.plovchat.scenes.ChatScene;
import com.plovdev.plovchat.scenes.LoginScene;
import com.plovdev.plovchat.utils.RestManager;
import com.plovdev.plovchat.utils.Utils;
import com.plovdev.plovchat.utils.WSManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class PlovChatApp extends Application {
    @Override
    public void start(Stage stage) {
        BaseScene baseScene;

        //prefs.putBoolean("is-user-registred", false);
        if (!Utils.getBoolean("is-user-registred", false)) {
            baseScene = new LoginScene(stage);
        } else {
            String id = Utils.getFromPrefs("user-id", "");
            String name = Utils.getFromPrefs("user-name", "");
            String passw = Utils.getFromPrefs("user-password", "");

            if (RestManager.getInstance().checkUser(id, passw)) {
                baseScene = new ChatScene(stage);
            } else {
                baseScene = new LoginScene(stage);
            }
        }

        stage.setScene(baseScene);
        stage.setTitle("PlovChat");
        stage.show();
    }

    @Override
    public void stop() {
        WSManager manager = WSManager.getInstance();
        manager.setOnline(false);
        manager.close();
    }
}