package group29.ui.element;

import group29.ui.Renderable;
import group29.ui.SceneManager;
import group29.ui.popup.HelpPopup;
import group29.ui.popup.SettingsPopup;
import group29.model.TableRow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class TopBar extends Renderable {

    private static final Logger logger = LogManager.getLogger("Top Bar");
    private final SceneManager sceneManager;

    private final HBox leftButtons;
    private final HBox centerButtons;
    private final HBox rightButtons;

    private ArrayList<TableRow> contextualShortcuts = new ArrayList<>();

    public TopBar(SceneManager sceneManager) {
        super(sceneManager);
        this.contextualShortcuts = contextualShortcuts;
        setTitle(" ");

        this.sceneManager = sceneManager;
        
        var btnHelp = new ADButton("", "btn-circle");
        btnHelp.setIcon("help.png", 24);
        btnHelp.setOnAction(ev -> sceneManager.setPopup(new HelpPopup(sceneManager, contextualShortcuts)));

        var btnSettings = new ADButton("", "btn-circle");
        btnSettings.setIcon("settings.png", 24);
        btnSettings.setOnAction(ev -> sceneManager.setPopup(new SettingsPopup(sceneManager)));

        // Scenes can add context-specific buttons on the left like the back or the print button
        leftButtons = new HBox();
        centerButtons = new HBox();
        rightButtons = new HBox(btnHelp, btnSettings);

        displayPane.setLeft(leftButtons);
        displayPane.setCenter(centerButtons);
        displayPane.setRight(rightButtons);

        // This is a very ugly hack to keep the right buttons in the same position when we enter the CampaignScene
        rightButtons.setPadding(new Insets(3, 0, 0, 0));
    }
    
    public void setShortcuts(ArrayList<TableRow> contextualShortcuts) {
        this.contextualShortcuts = contextualShortcuts;
    }

    public void setTitle(String title) {
        var labelTitle = new ADLabel(title, 32);
        labelTitle.setFont("NunitoSans-ExtraLight.ttf");
        labelTitle.setAlignment(Pos.CENTER);
        displayPane.setCenter(labelTitle);
    }

    public void addToLeftButtons(Node node) {
        leftButtons.getChildren().add(node);
    }
    
    public void addToRightButtons(Node node) {
        rightButtons.getChildren().add(0, node);
    }

    public void addToCenterButtons(Node node) {
        centerButtons.getChildren().add(0, node);
        rightButtons.setPadding(new Insets(0));
    }
}
