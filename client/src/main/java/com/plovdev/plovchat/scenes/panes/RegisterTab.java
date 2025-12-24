package com.plovdev.plovchat.scenes.panes;

import com.plovdev.plovchat.PlovChatApp;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.scenes.ChatScene;
import com.plovdev.plovchat.utils.RestManager;
import com.plovdev.plovchat.utils.Utils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class RegisterTab extends StackPane {
    private static final Preferences prefs = Preferences.userNodeForPackage(PlovChatApp.class);

    private final VBox content = new VBox(20);
    private final TextField name = new TextField();
    private final TextField passw = new TextField();
    private final Button done = new Button("Зарегестрироваться");
    private final Stage root;

    public RegisterTab(Stage stage) {
        root = stage;
        init();
    }

    private void init() {
        name.setPromptText("Введите ник");
        passw.setPromptText("Введите пароль");

        name.getStyleClass().add("chat-field");
        passw.getStyleClass().add("chat-field");
        done.getStyleClass().add("chat-reg-button");

        done.setOnAction(a -> handleEnter());

        // Центрируем элементы внутри VBox
        content.setAlignment(Pos.CENTER);

        // Добавляем отступы вокруг VBox
        content.setPadding(new Insets(20));

        // Не даем VBox растягиваться на всю ширину
        content.setMaxWidth(Region.USE_PREF_SIZE);

        content.getChildren().addAll(name, passw, done);

        // StackPane автоматически центрирует content
        getChildren().add(content);
    }

    private void handleEnter() {
        String nameStr = name.getText().trim();
        String password = passw.getText().trim();

        if (!nameStr.isEmpty() && Utils.validatePassword(password)) {
            User created = RestManager.getInstance().createUser(nameStr, password);
            boolean isSuccess = created != null;
            if (isSuccess) {
                prefs.put("user-name", nameStr);
                prefs.put("user-password", password);
                prefs.put("user-id", created.getId());
                prefs.putBoolean("is-user-registred", true);

                root.setScene(new ChatScene(root));
            }
        }
    }
}