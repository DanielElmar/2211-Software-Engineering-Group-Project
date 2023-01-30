package group29.ui.minimenu;

import group29.ui.SceneManager;
import group29.ui.element.ADButton;

public class NewGraphMiniMenu extends MiniMenu {

    public NewGraphMiniMenu(SceneManager sceneManager) {
        super(sceneManager);

        displayPane.setCenter(new ADButton("lol"));
    }
}
