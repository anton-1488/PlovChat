package com.plovdev.plovchat.ui;

import com.plovdev.plovchat.models.File;
import com.plovdev.plovchat.models.Message;
import com.plovdev.plovchat.models.User;
import com.plovdev.plovchat.utils.RestManager;
import com.plovdev.plovchat.utils.Utils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

import static com.plovdev.plovchat.utils.Utils.hReg;

public class ImageMessageRender extends MessageRender {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("HH:ss");
    private static final Logger log = LoggerFactory.getLogger(ImageMessageRender.class);
    private final StackPane mainContent = new StackPane();
    private final BorderPane render = new BorderPane();

    private Label fromName;
    private Text text;
    private Label time;

    public ImageMessageRender(Message message) {
        super(message);
    }

    @Override
    public Pane render() {
        try {
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
            }

            text = new Text(message.getContent());
            time = new Label(Utils.fromatDate(message.getTimesamp()));

            fromName.getStyleClass().add("system-message-label");
            time.getStyleClass().add("system-message-label");
            text.getStyleClass().add("message-text");

            text.wrappingWidthProperty().bind(mainContent.widthProperty().divide(2.1));

            File file = message.getInfo();

            RestManager.getInstance().download(file.getId(), file.getName());

            ImageView imageView = new ImageView("downloads/" + file.getName());
            imageView.setSmooth(true);
            imageView.setPreserveRatio(true);

            imageView.fitWidthProperty().bind(mainContent.widthProperty().divide(2.15));
            imageView.setFitHeight(400);

            render.setCenter(imageView);
            render.setTop(new HBox(fromName, hReg()));
            render.setBottom(new VBox(10, text, new HBox(hReg(), time)));

            render.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    showContextMenu(render, e.getScreenX(), e.getScreenY());
                }
            });

            render.maxWidthProperty().bind(mainContent.widthProperty().divide(2.1));
            mainContent.getChildren().add(render);
        } catch (Exception e) {
            log.error("Image render error: ", e);
        }

        return mainContent;
    }
}
