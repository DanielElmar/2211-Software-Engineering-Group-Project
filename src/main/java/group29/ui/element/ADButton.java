package group29.ui.element;

import group29.event.SettingsModelUpdateListener;
import group29.model.SettingsModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ADButton extends StackPane implements SettingsModelUpdateListener {

    private static SettingsModel settingsModel;
    private final Button btn;
    private final Button btnBackground;
    private String style = "-fx-background-repeat: no-repeat;" + "-fx-background-position: center;";
    private boolean isBold = false;

    public ADButton(){
        this("");
    }

    public ADButton(String text){
        this(text, "");
    }

    public ADButton(String text, String className){
        super();
        btn = new Button(text);
        btnBackground = new Button(text);
        setClass(className);
        getChildren().addAll(btnBackground, btn);

        sceneProperty().addListener(ev -> {
            if (this.getScene() != null) {
                settingsModel.removeListener(this);
                settingsModel.addListener(this);
                settingsUpdate();
            }
        });
        
        // Hacky method to be able to have 2 buttons pressed at the same time
        btn.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            getStyleClass().add("ad-button-pressed");
        });

        btn.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            getStyleClass().clear();
        });
    }

    public void removeBackButton() {
        getChildren().clear();
        getChildren().add(btn);
    }

    public void setOnAction(EventHandler<ActionEvent> e) {
        btn.setOnAction(ev -> {
            e.handle(ev);            
        });
    }

    public void setIcon(String iconName) {
        setIcon(iconName, 16);
    }

    public void setIcon(String iconName, int iconSize) {
        // It's all here instead of in the CSS because setting the background image on its own
        // resets the size, repeat and position for some reason
        addStyle("-fx-background-image: url('/images/icons/" + iconName + "')");
        addStyle("-fx-background-size: " + iconSize + "px;");
    }
    
    public void addStyle(String style) {
        this.style += style + ";";
        btn.setStyle(this.style);
        btnBackground.setStyle(this.style);
    }
    
    public void setSize(int width, int height) {
        btn.setMinWidth(width);
        btn.setMinHeight(height);
        btnBackground.setMinWidth(width);
        btnBackground.setMinHeight(height);
    }

    public void setClass(String className) {
        btn.getStyleClass().clear();
        btnBackground.getStyleClass().clear();

        btn.getStyleClass().addAll("btn-large", className);
        btnBackground.getStyleClass().addAll("btn-large", className, "ad-button-light-shadow");
        isBold = className.equals("btn-blue");

        settingsUpdate();
    }

    public static void setSettingsModel(SettingsModel settingsModel) {
        ADButton.settingsModel = settingsModel;
    }

    @Override
    public void settingsUpdate() {
        // Apparently setting this in the CSS leads to weird font sizing when hovering a button
        var fontWeight = FontWeight.NORMAL;
        if (isBold) fontWeight = FontWeight.BOLD;

        var font = Font.font("Verdana", fontWeight, settingsModel.getTxtSize());
        btn.setFont(font);
        btnBackground.setFont(font);
    }

    public boolean isAlive() {
        return this.getScene() != null;
    }
}
