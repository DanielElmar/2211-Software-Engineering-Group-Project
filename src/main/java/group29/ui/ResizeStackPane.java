package group29.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResizeStackPane extends StackPane {

    private final double width;
    private final double height;
    private final double scale = 1;
    protected final Logger logger = LogManager.getLogger(this.getClass());

    public ResizeStackPane(int width, int height) {
        super();

        this.width = width;
        this.height = height;
        //setAlignment(Pos.TOP_LEFT);
    }

    @Override
    public void layoutChildren() {
        super.layoutChildren();

        // finds out the scale factor height and width
        double widthRatio = getWidth() / width;
        double heightRatio = getHeight() / height;

        //scale by width or height
        double minRatio = Math.min(widthRatio, heightRatio);

        var scale = new Scale(minRatio, minRatio);

        var offset = new Translate((getWidth() - (width * minRatio)) / 2.0, (getHeight() - (height * minRatio)) / 2.0);

        //doing the translate and scale
        scale.setPivotX(0);

        //todo figure out why this doesnt work
        getTransforms().setAll(offset, scale);
    }
}

