package group29.ui;

import group29.model.SettingsModel;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Renderable {
    
    protected final BorderPane displayPane;
    protected final SceneManager sceneManager;
    private static SettingsModel settingsModel;

	protected final Logger logger = LogManager.getLogger(this.getClass());

    public Renderable(SceneManager sceneManager) {
        this.displayPane = new BorderPane();
        this.sceneManager = sceneManager;
        
        try {
            displayPane.getStylesheets().add(getClass().getResource("/appearance.css").toExternalForm());
        } catch (Exception e) {
            // JAR fix
            displayPane.getStylesheets().add(getClass().getResource("appearance.css").toExternalForm());
        }
    }

    protected void makeScrollable(Region element) {
        makeScrollable(element, 0);
    }

    protected void makeScrollable(Region element, int offset) {
        element.setOnScroll(ev -> {
            if (ev.getDeltaY() != 0) {
                var newY = element.getTranslateY() + ev.getDeltaY();
                var maxY = element.getHeight() + offset - sceneManager.getHeight();

                if (maxY > 0) {
                    maxY += 32; // Some extra room to not feel so constrained in the scene
                }

                if (newY <= 0 && (newY >= -maxY || ev.getDeltaY() > 0)) {
                    element.setTranslateY(newY);
                }
            }
        });
    }

    public BorderPane getDisplayPane() {
        return displayPane;
    }

    public static void setSettingsModel(SettingsModel settingsModel) {
        Renderable.settingsModel = settingsModel;
    }

    public void deconstruct(Runnable finishDeconstruction) {
        finishDeconstruction.run();
    }
}