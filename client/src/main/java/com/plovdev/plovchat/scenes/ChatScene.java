package com.plovdev.plovchat.scenes;

import com.plovdev.plovchat.models.Chat;
import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.ui.*;
import com.plovdev.plovchat.utils.MessageListener;
import com.plovdev.plovchat.utils.RestManager;
import com.plovdev.plovchat.utils.Utils;
import com.plovdev.plovchat.utils.WSManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ChatScene extends BaseScene {
    private static final Logger log = LoggerFactory.getLogger(ChatScene.class);
    private final BorderPane mainPane = new BorderPane();
    private final WSManager manager = WSManager.getInstance();
    private final ChatsList list = new ChatsList();
    private final VBox userCard = new VBox(10);

    public ChatScene(Stage stage) {
        super(new Pane(), 1000, 600, stage);
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        initListener(null);
        String id = Utils.getFromPrefs("user-id", "");
        String name = Utils.getFromPrefs("user-name", "");
        String passw = Utils.getFromPrefs("user-password", "");

        setRoot(mainPane);
        loadChats(list);

        userCard.setPadding(new Insets(5, 5, 5, 15));
        userCard.getStyleClass().add("user-card");
        Label userName = new Label("Name: " + name);
        Hyperlink userId = new Hyperlink("Id: " + id);
        userId.setOnAction(e -> Utils.copy(id));
        userName.getStyleClass().add("user-name-label");
        userId.getStyleClass().add("user-id-label");

        userCard.getChildren().addAll(userName, userId);

        userCard.prefWidthProperty().bind(list.widthProperty());

        manager.setOnReady(() -> manager.setOnline(true));

        list.getSelectionModel().selectedItemProperty().addListener((p1, p2, p3) -> {
            AnchorPane base = new AnchorPane();

            Chat chat = ((ChatCardView) p3).getChat();
            MessageList messageList = new MessageList();
            messageList.addAllMessages(loadViews(chat));
            messageList.prefHeightProperty().bind(heightProperty());
            initListener(messageList);

            SendField sendField = new SendField(chat, messageList);

            AnchorPane.setTopAnchor(messageList, 0.0);
            AnchorPane.setBottomAnchor(messageList, 50.0); // Оставляем место для SendField
            AnchorPane.setLeftAnchor(messageList, 0.0);
            AnchorPane.setRightAnchor(messageList, 0.0);

            AnchorPane.setBottomAnchor(sendField, 0.0);
            AnchorPane.setLeftAnchor(sendField, 0.0);
            AnchorPane.setRightAnchor(sendField, 0.0);

            base.getChildren().addAll(messageList, sendField);
            mainPane.setCenter(base);
        });

        mainPane.setLeft(list);


        mainPane.getStyleClass().add("chat-scene");

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        mainPane.setTop(new HBox(userCard, space));
    }

    private void loadChats(ChatsList list) {
        VBox.setVgrow(list, Priority.ALWAYS);
        String id = Utils.getFromPrefs("user-id", null);
        String password = Utils.getFromPrefs("user-password", null);

        if (id != null && password != null) {
            RestManager.getInstance().getChats(id, password).forEach(chat -> list.addChat(new ChatCardView(chat)));
        }

        Button createChat = new Button("Создать чат");
        createChat.setOnAction(a -> {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Введите ID");
            Optional<String> entered = dialog.showAndWait();
            entered.ifPresent(s -> {
                s = s.trim();
                if (s.isEmpty()) {
                    return;
                }
                Chat created = RestManager.getInstance().createChat(s, s);
                Platform.runLater(() -> list.addChat(new ChatCardView(created)));
            });
        });

        HBox createChatBox = new HBox(0, createChat);
        list.getItems().addFirst(createChatBox);
    }

    private List<MessageView> loadViews(Chat chat) {
        String id = Utils.getFromPrefs("user-id", null);
        String password = Utils.getFromPrefs("user-password", null);

        List<MessageView> views = new ArrayList<>();

        if (id != null && password != null) {
            RestManager.getInstance().loadMessages(chat.getId(), id, password).forEach(message -> views.add(new MessageView(message)));
        }
        return views;
    }

    private void initListener(MessageList messageList) {
        manager.setListener(new MessageListener() {
            @Override
            public void onMessageReceived(Message message) {
                Utils.notification("Сообщение от " + message.getFrom().getName(), message.getContent());
                if (messageList != null) {
                    Platform.runLater(() -> messageList.addMessage(message));
                }
            }

            @Override
            public void onAuntithicated() {
                log.info("Успешно аутенцифицировался");
            }

            @Override
            public void onStatusChanged(boolean status, User user) {
                // TODO
            }
        });
    }
}