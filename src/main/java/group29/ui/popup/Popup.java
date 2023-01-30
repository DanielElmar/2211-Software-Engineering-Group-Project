package group29.ui.popup;

import group29.ui.Renderable;
import group29.ui.SceneManager;
import group29.ui.element.ADLabel;
import group29.ui.element.ADButton;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public abstract class Popup extends Renderable {

    private final ADLabel titleLabel;
    protected ADButton closeButton;
    
    private final StackPane fullPane;
    private final Rectangle bgRect;

    private final int animationSpeed = 200;
    public Popup(SceneManager sceneManager) {
        this(sceneManager, "");
    }

    public Popup(SceneManager sceneManager, String title) {
        super(sceneManager);
        displayPane.getStyleClass().add("popup");
        
        titleLabel = new ADLabel(title, 24);
        titleLabel.setAlignment(Pos.TOP_CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setFont("NunitoSans-ExtraLight.ttf");

        closeButton = new ADButton("", "btn-circle");
        closeButton.setIcon("close.png");
        closeButton.setOnAction(ev -> sceneManager.popStackpane());
        closeButton.setAlignment(Pos.TOP_RIGHT);

        var hBoxTopBar = new HBox(titleLabel, closeButton);
        hBoxTopBar.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        // Center the title, because we have a close button on the left
        hBoxTopBar.setPadding(new Insets(0, 0, 0, 48 + 16));
        
        displayPane.setTop(hBoxTopBar);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        BorderPane.setMargin(hBoxTopBar, new Insets(8));

        bgRect = new Rectangle();
        bgRect.setFill(new Color(0, 0, 0, 0.25));
        bgRect.setX(0);
        bgRect.setY(0);
        bgRect.setWidth(sceneManager.getWidth());
        bgRect.setHeight(sceneManager.getHeight());
        
        FadeTransition ft = new FadeTransition(Duration.millis(animationSpeed), displayPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        TranslateTransition tt = new TranslateTransition(Duration.millis(animationSpeed), displayPane);
        tt.setFromY(8);
        tt.setToY(0);
        tt.play();

        FillTransition tr = new FillTransition(Duration.millis(animationSpeed), bgRect);
        tr.setFromValue(new Color(0, 0, 0, 0));
        tr.setToValue(new Color(0, 0, 0, 0.25));
        tr.play();
        
        fullPane = new StackPane(bgRect, displayPane);
        fullPane.setAlignment(Pos.TOP_CENTER);
        StackPane.setMargin(displayPane, new Insets(64));
        fullPane.setMaxHeight(sceneManager.getHeight());

        makeScrollable(displayPane, 64);
    }

    @Override
    public void deconstruct(Runnable finishDeconstruction) {
        FadeTransition ft = new FadeTransition(Duration.millis(animationSpeed), displayPane);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();

        TranslateTransition tt = new TranslateTransition(Duration.millis(animationSpeed), displayPane);
        tt.setFromY(0);
        tt.setToY(4);
        tt.play();

        FillTransition tr = new FillTransition(Duration.millis(animationSpeed), bgRect);
        tr.setFromValue(new Color(0, 0, 0, 0.25));
        tr.setToValue(new Color(0, 0, 0, 0));
        tr.play();

        tr.setOnFinished(e -> finishDeconstruction.run());
    }

    public StackPane getFullPane() {
        return fullPane;
    }

    public void changeTitle(String title) {
        titleLabel.setText(title);
    }
}
