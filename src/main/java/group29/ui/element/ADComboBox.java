package group29.ui.element;

import group29.event.SettingsModelUpdateListener;
import group29.model.SettingsModel;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class ADComboBox extends ComboBox implements SettingsModelUpdateListener {
    
    private static SettingsModel settingsModel;
    
    public ADComboBox() {
        super();

        sceneProperty().addListener(ev -> {
            if (this.getScene() != null) {
                settingsModel.removeListener(this);
                settingsModel.addListener(this);
                settingsUpdate();
            }
        });
    }

    public static void setSettingsModel(SettingsModel settingsModel) {
        ADComboBox.settingsModel = settingsModel;
    }

    public void settingsUpdate() {
        // Apparently there is no other way?
        setStyle("-fx-font-size: " + settingsModel.getTxtSize() + "px");
    }

    public boolean isAlive() {
        return this.getScene() != null;
    }
}
