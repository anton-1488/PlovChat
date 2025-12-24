package com.plovdev.plovchat.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BaseScene extends Scene {
    protected final Stage rootStage;

    public BaseScene(Parent parent, double v, double v1, Stage stage) {
        super(parent, v, v1);
        rootStage = stage;
    }

    public Stage getRootStage() {
        return rootStage;
    }
}