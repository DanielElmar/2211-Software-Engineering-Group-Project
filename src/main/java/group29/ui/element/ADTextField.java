package group29.ui.element;

import group29.event.SettingsModelUpdateListener;
import group29.model.SettingsModel;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class ADTextField extends TextField implements SettingsModelUpdateListener {
    
    private static SettingsModel settingsModel;
    
    public ADTextField() {
        super();
        sceneProperty().addListener(ev -> {
            if (this.getScene() != null) {
                settingsModel.removeListener(this);
                settingsModel.addListener(this);
                settingsUpdate();
            }
        });
    }

    public void setNumeric() {
        // https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
        textProperty().addListener((obs, ov, nv) -> {
            if (!nv.matches("\\d*")) {
                setText(nv.replaceAll("[^\\d]", ""));
            }
        });
    }

    public static void setSettingsModel(SettingsModel settingsModel) {
        ADTextField.settingsModel = settingsModel;
    }

    public void settingsUpdate() {
        setFont(Font.font(settingsModel.getTxtSize()));
    }

    public boolean isAlive() {
        return this.getScene() != null;
    }
}
