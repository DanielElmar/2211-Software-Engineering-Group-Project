package group29.ui.element;

import group29.event.SettingsModelUpdateListener;
import group29.model.SettingsModel;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ADLabel extends Label implements SettingsModelUpdateListener {

    private static final Logger logger = LogManager.getLogger("ADLabel");
    private static SettingsModel settingsModel;
    private final int offset;
    private boolean isBold;

    private String fontFamily = "NunitoSans-Regular.ttf";

    public ADLabel(){
        this("", 0, "");
    }

    public ADLabel(String text){
        this(text, 0, "");
    }

    public ADLabel(String text, int offset){
        this(text, offset, "");
    }

    public ADLabel(String text, int offset, String className) {
        super( text );
        
        getStyleClass().add(className);
        this.offset = offset;
        setFont(new Font(settingsModel.getTxtSize() + offset));
        //settingsModel.addListener(this);

        sceneProperty().addListener(ev -> {
            if (this.getScene() != null) {
                settingsModel.removeListener(this);
                settingsModel.addListener(this);
                settingsUpdate();
            }
        });
    }

    public void setFont(String fontName) {
        fontFamily = fontName;
        settingsUpdate();
    }

    public void setIsBold(boolean bold) {
        isBold = bold;
        settingsUpdate();
    }

    public static void setSettingsModel(SettingsModel settingsModel) {
        ADLabel.settingsModel = settingsModel;
    }

    public void setStyleClass(String style) {
        getStyleClass().clear();
        getStyleClass().add(style);
    }

    @Override
    public void settingsUpdate() {
        var fontWeight = FontWeight.NORMAL;
        if (isBold) fontWeight = FontWeight.BOLD;

        if (!isBold) {
            // I don't know how to make this bold, it's probably not possible
            setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/" + fontFamily), settingsModel.getTxtSize() + offset));
        } else {
            setFont(Font.font("Verdana", fontWeight, settingsModel.getTxtSize() + offset));
        }
    }

    public boolean isAlive() {
        return this.getScene() != null;
    }
}
