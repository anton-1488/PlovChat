package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.File;
import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.utils.RestManager;
import com.plovdev.plovchat.utils.Utils;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.format.DateTimeFormatter;

import static com.plovdev.plovchat.utils.Utils.hReg;

public class FileMessageRender extends MessageRender {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("HH:ss");
    private static final Logger log = LoggerFactory.getLogger(FileMessageRender.class);
    private final BorderPane render = new BorderPane();

    private Label fromName;
    private Text text;
    private Label time;

    public FileMessageRender(Message message) {
        super(message);
    }

    @Override
    public Pane render() {
        StackPane mainContent = new StackPane();
        String myId = Utils.getFromPrefs("user-id", "");
        User from = message.getFrom();

        fromName = new Label();

        render.getStyleClass().add("message-view");

        if (from != null) {
            fromName.setText(from.getName());
            if (from.getId().equals(myId)) {
                render.getStyleClass().add("my-message-view");
                StackPane.setAlignment(render, Pos.CENTER_RIGHT);
            } else {
                render.getStyleClass().add("user-message-view");
                StackPane.setAlignment(render, Pos.CENTER_LEFT);
            }
        } else {
            render.getStyleClass().add("system-message-view");
            StackPane.setAlignment(render, Pos.CENTER);
        }

        text = new Text(message.getContent());
        time = new Label(Utils.fromatDate(message.getTimesamp()));

        fromName.getStyleClass().add("system-message-label");
        time.getStyleClass().add("system-message-label");
        text.getStyleClass().add("message-text");

        text.wrappingWidthProperty().bind(mainContent.widthProperty().divide(2.1));


        File file = message.getInfo();
        Hyperlink link = new Hyperlink(file.getName());
        String fileName = file.getName();
        link.setOnAction(a -> {
            RestManager.getInstance().download(file.getId(), file.getId() + fileName);
            openFile(file);
        });

        render.setCenter(new HBox(new VBox(20, link)));

        render.setTop(new HBox(fromName, hReg()));
        render.setBottom(new VBox(10, text, new HBox(hReg(), time)));

        render.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showContextMenu(render, e.getScreenX(), e.getScreenY());
            } else {
                openFile(file);
            }
        });

        render.maxWidthProperty().bind(mainContent.widthProperty().divide(2.1));
        mainContent.getChildren().add(render);

        return mainContent;
    }
    private void openFile(File file) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new java.io.File("downloads/" + file.getId() + file.getName()));
        } catch (Exception ex) {
            log.warn("Open error: ", ex);
        }
    }
}
