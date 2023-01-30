package group29.ui;

import group29.ui.scene.CampaignScene;

import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Stack;
import javafx.scene.shape.Rectangle;
import group29.event.SettingsModelUpdateListener;
import group29.model.ModelManager;
import group29.model.SettingsModel;
import group29.ui.minimenu.MiniMenu;
import group29.ui.popup.Popup;
import group29.ui.scene.CampaignSelectionScene;
import group29.ui.scene.SceneBase;

public class SceneManager implements SettingsModelUpdateListener {
    
	final Logger logger = LogManager.getLogger(this.getClass());

    private final StackPane rootStackPane;

    private final Stage javaFxStage;
    private final Scene javaFxScene;
    private final ModelManager modelManager;

    private final int width = 1024;
    private final int height = 720;

    private int mouseX;
    private int mouseY;

    private Renderable currentScene;
    private final Stack<Renderable> renderableStack = new Stack<>();

    private boolean isShowingPopup = false;
    public SceneManager(Stage javaFxStage, ModelManager modelManager) {
        this.javaFxStage = javaFxStage;
        this.modelManager = modelManager;
        
        javaFxStage.setTitle("Ad auction");
        javaFxStage.setMinWidth(width);
        javaFxStage.setWidth(width);
        javaFxStage.setMinHeight(height);
        javaFxStage.setHeight(height);
        javaFxStage.setOnCloseRequest(ev -> System.exit(0));
        
        var resizeStackPane = new ResizeStackPane(width, height);
        rootStackPane = new StackPane();
        rootStackPane.setMinWidth(width);
        rootStackPane.setMinHeight(height);
        rootStackPane.setMaxWidth(width);
        rootStackPane.setMaxHeight(height);

        resizeStackPane.getChildren().add(rootStackPane);
        StackPane.setAlignment(rootStackPane, Pos.TOP_LEFT);
        javaFxScene = new Scene(resizeStackPane);
        javaFxScene.setFill(Color.web("#F0F1F3"));
        javaFxStage.setScene(javaFxScene);
        javaFxStage.show();

        logger.info(resizeStackPane.getStyleClass());

        javaFxScene.addEventFilter(MouseEvent.MOUSE_MOVED, ev -> {
            mouseX = (int) ((ev.getX() / javaFxScene.getWidth()) * width);
            mouseY = (int) ((ev.getY() / javaFxScene.getHeight()) * height);
        });

        javaFxScene.addEventHandler(
            KeyEvent.KEY_PRESSED,
            t -> {
                switch (t.getCode()) {
                    case ESCAPE -> popStackpane();
                }
            }
        );

        modelManager.getSettingsModel().addListener(this);
        settingsUpdate();
    }

    public void settingsUpdate() {
        rootStackPane.setStyle(modelManager.getSettingsModel().getThemeStyle());
    }

    public boolean isAlive() {
        return rootStackPane.getScene() != null;
    }

    public void setScene(SceneBase scene) {
        isShowingPopup = false;
        var fullPane = scene.getFullPane();
        // temporarily render the scene on top of the current scene, to get a nice fade-in effect 
        rootStackPane.getChildren().add(fullPane);
        StackPane.setAlignment(fullPane, Pos.TOP_CENTER);
        
        scene.animateFadeIn(() -> {
            currentScene = scene;
            rootStackPane.getChildren().clear();
            renderableStack.clear();
        
            rootStackPane.getChildren().add(fullPane);
            StackPane.setAlignment(fullPane, Pos.TOP_CENTER);
            renderableStack.add(scene);
        });
    }

    public void setPopup(Popup popup) {
        if (isShowingPopup) {
            return;
        }

        isShowingPopup = true;
        
        var popupPane = popup.getFullPane();

        rootStackPane.getChildren().add(popupPane);
        StackPane.setAlignment(popupPane, Pos.TOP_CENTER);
        renderableStack.add(popup);
    }

    public boolean getIsShowingPopup() {
        return isShowingPopup;
    }
    
    public void spawnMinimenu(MiniMenu minimenu) {
        var bg = new Pane();
        bg.addEventFilter(MouseEvent.MOUSE_CLICKED, ev -> {if (ev.getTarget() == bg) popStackpane();});

        var minimenuPane = minimenu.getDisplayPane();
        bg.getChildren().add(minimenuPane);
        minimenuPane.setTranslateX(mouseX + 8);
        minimenuPane.setTranslateY(mouseY);

        rootStackPane.getChildren().add(bg);
        renderableStack.add(minimenu);
    }
    
    public ImageView capturePrint() {
        Pane pane = currentScene.getDisplayPane();
        WritableImage writeImage = pane.snapshot(null, null);

        ImageView image = new ImageView(writeImage);
        image.getStyleClass().add("screenshot");
        return image;
    }

    public void popStackpane() {
        isShowingPopup = false;
        var children = rootStackPane.getChildren();

        // If there is a closable element (like a popup or a minimenu)
        if (renderableStack.size() >= 2) {
            Renderable renderable = renderableStack.get(renderableStack.size() - 1);
            renderableStack.pop();
            renderable.deconstruct(() -> {
                children.remove(children.size() - 1);
            });
        } else if ( currentScene instanceof CampaignSelectionScene ){
            // TODO - implement exit prompt
            //System.exit( 0 );
        } else {
            setScene(new CampaignSelectionScene(this));
        }
    }

    public Stage getJavaFxStage() {
        return javaFxStage;
    }

    public Scene getJavaFxScene() {
        return javaFxScene;
    }

    public StackPane getRootStackPane() {
        return rootStackPane;
    }

    public Renderable getRenderableScene(){
        return currentScene;
    }

    public ModelManager getModelManager() {
        return modelManager;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
}
