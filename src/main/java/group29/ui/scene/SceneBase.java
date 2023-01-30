package group29.ui.scene;

import group29.ui.Renderable;
import group29.ui.SceneManager;
import group29.ui.element.TopBar;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public abstract class SceneBase extends Renderable {
    
    private final StackPane fullPane;
    protected TopBar topBar;
    private final boolean animationPopIn;
    private final int animationSpeed = 150;

    public SceneBase(SceneManager sceneManager, boolean animationPopIn) {
        super(sceneManager);

        this.animationPopIn = animationPopIn;
        
        topBar = new TopBar(sceneManager);
        var topBarPane = topBar.getDisplayPane();
        topBarPane.setMaxHeight(48);
        topBarPane.setPadding(new Insets(21, 24, 8, 24));
        
        displayPane.setPadding(new Insets(80, 24, 24, 24));

        fullPane = new StackPane(displayPane, topBarPane);
        StackPane.setAlignment(topBarPane, Pos.TOP_CENTER);

        fullPane.getStyleClass().add("scene-bgcolor");
        displayPane.getStyleClass().add("scene-bgcolor");
        topBarPane.getStyleClass().add("scene-bgcolor");
    }

    public void animateFadeIn(Runnable finishFadeIn) {
        if (animationPopIn) {
            ScaleTransition st = new ScaleTransition(Duration.millis(animationSpeed), displayPane);
            st.setFromX(0.95);
            st.setFromY(0.95);
            st.setToX(1);
            st.setToY(1);
            st.play();
        } else {
            ScaleTransition st = new ScaleTransition(Duration.millis(animationSpeed), displayPane);
            st.setFromX(1.05);
            st.setFromY(1.05);
            st.setToX(1);
            st.setToY(1);
            st.play();
        }

        FadeTransition ft = new FadeTransition(Duration.millis(animationSpeed), fullPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        
        ft.setOnFinished(e -> finishFadeIn.run());
    }

    public StackPane getFullPane() {
        return fullPane;
    }
}
