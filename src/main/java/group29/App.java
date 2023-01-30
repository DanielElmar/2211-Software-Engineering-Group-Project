package group29;

import group29.model.CampaignsModel;
import group29.model.ModelManager;
import group29.model.SettingsModel;
import group29.ui.Renderable;
import group29.ui.SceneManager;
import group29.ui.element.ADButton;
import group29.ui.element.ADComboBox;
import group29.ui.element.ADLabel;
import group29.ui.scene.CampaignSelectionScene;
import group29.ui.element.ADTextField;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JavaFX App
 */
public class App extends Application {

	static final Logger logger = LogManager.getLogger("App");

    // todo - add app icon
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        logger.info("Start function called!");

        ModelManager mm = new ModelManager(new CampaignsModel(), new SettingsModel());
        ADLabel.setSettingsModel(mm.getSettingsModel());
        ADButton.setSettingsModel(mm.getSettingsModel());
        ADTextField.setSettingsModel(mm.getSettingsModel());
        ADComboBox.setSettingsModel(mm.getSettingsModel());
        Renderable.setSettingsModel(mm.getSettingsModel());
        SceneManager sm = new SceneManager(stage, mm);
        sm.setScene(new CampaignSelectionScene(sm));
    }

    public int testReturn5() {
        return 5;
    }
}