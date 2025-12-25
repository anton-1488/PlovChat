package com.plovdev.plovchat.scenes.panes;

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

public class LoginTab extends StackPane {
    private final Stage root;

    private final VBox content = new VBox(20);
    private final TextField id = new TextField();
    private final TextField passw = new TextField();
    private final Button done = new Button("Войти");

    public LoginTab(Stage stage) {
        root = stage;
        init();
    }

    private void init() {
        id.setPromptText("Введите id");
        passw.setPromptText("Введите пароль");

        id.getStyleClass().add("chat-field");
        passw.getStyleClass().add("chat-field");
        done.getStyleClass().add("chat-button");

        done.setOnAction(a -> handleEnter());

        // Центрируем элементы внутри VBox
        content.setAlignment(Pos.CENTER);

        // Добавляем отступы вокруг VBox
        content.setPadding(new Insets(20));

        // Не даем VBox растягиваться на всю ширину
        content.setMaxWidth(Region.USE_PREF_SIZE);

        content.getChildren().addAll(id, passw, done);

        // StackPane автоматически центрирует content
        getChildren().add(content);
    }

    private void handleEnter() {
        String idStr = id.getText().trim();
        String password = passw.getText().trim();

        if (Utils.validatePassword(password)) {
            boolean isSuccess = RestManager.getInstance().checkUser(idStr, password);
            if (isSuccess) {
                Utils.putToPrefs("user-id", idStr);
                Utils.putToPrefs("user-password", password);
                Utils.putBoolean("is-user-registred", true);

                root.setScene(new ChatScene(root));
            }
        }
    }
}