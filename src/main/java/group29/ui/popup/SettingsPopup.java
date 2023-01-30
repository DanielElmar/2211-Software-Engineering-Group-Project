package group29.ui.popup;

import group29.event.SettingsModelUpdateListener;
import group29.model.SettingsModel;
import group29.ui.SceneManager;
import group29.ui.element.ADButton;
import group29.ui.element.ADComboBox;
import group29.ui.element.ADLabel;
import group29.ui.element.ADTextField;
import group29.enums.ui.Theme;
import group29.event.SettingsModelUpdateListener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SettingsPopup extends Popup implements SettingsModelUpdateListener {

    private final SettingsModel settingsModel;

    public SettingsPopup(SceneManager sceneManager) {
        super(sceneManager, "Settings");

        settingsModel = sceneManager.getModelManager().getSettingsModel();

        displayPane.sceneProperty().addListener(ev -> {
            if (displayPane.getScene() != null) {
                settingsModel.removeListener(this);
                settingsModel.addListener(this);
            }
        });

        settingsUpdate();
    }

    public boolean isAlive() {
        return this.displayPane.getScene() != null;
    }

    public void settingsUpdate() {
        logger.info("refresh " + this);

        ComboBox colourThemesComboBox = new ADComboBox();

        var index = 0;
        var indexOfSelection = 0;

        for (Theme theme : Theme.values()) {
            colourThemesComboBox.getItems().add(theme);

            if (theme == settingsModel.getTheme()) {
                indexOfSelection = index;
            }

            index += 1;
        }

        colourThemesComboBox.getSelectionModel().select(indexOfSelection);

        colourThemesComboBox.valueProperty().addListener(ev -> {
            logger.info("set " + colourThemesComboBox.getValue());
            settingsModel.setTheme((Theme) colourThemesComboBox.getValue());
        });

        // Custom text size
        var currentTextSize = settingsModel.getTxtSize();
        var smallTextSize = 10;
        var mediumTextSize = 16;
        var largeTextSize = 24;

        var smallTextButton = new ADButton("Small");
        var mediumTextButton = new ADButton("Medium");
        var largeTextButton = new ADButton("Large");

        if (currentTextSize == smallTextSize) smallTextButton.setClass("btn-blue");
        if (currentTextSize == mediumTextSize) mediumTextButton.setClass("btn-blue");
        if (currentTextSize == largeTextSize) largeTextButton.setClass("btn-blue");

        smallTextButton.setOnAction(e -> settingsModel.setTxtSize(smallTextSize));
        mediumTextButton.setOnAction(e -> settingsModel.setTxtSize(mediumTextSize));
        largeTextButton.setOnAction(e -> settingsModel.setTxtSize(largeTextSize));

        var textSizeField = new ADTextField();
        textSizeField.setPromptText("Text size in pixels");
        textSizeField.setMaxWidth(100);
        textSizeField.textProperty().setValue(currentTextSize + "");
        textSizeField.setNumeric();

        var okBtnTextSize = new ADButton("Set");
        okBtnTextSize.setOnAction(ev -> {
            var textSize = currentTextSize;

            try {
                var value = textSizeField.textProperty().getValue();
                textSize = Integer.valueOf(value);
            } catch (Exception e) {
                textSize = currentTextSize;
            }

            if (textSize < settingsModel.getMinTextSize()) {
                textSize = settingsModel.getMinTextSize();
            }

            if (textSize > settingsModel.getMaxTextSize()) {
                textSize = settingsModel.getMaxTextSize();
            }

            settingsModel.setTxtSize(textSize);
        });

        //checkboxes
        // saveButton.setOnAction( ev -> {
        //     int checkBoxTextSize = checkBoxTextSizesController.getTextSize();
        //     int customTextSize = -1;
        //     try {
        //         customTextSize = Integer.parseInt(textSizeField.getText());
        //     }catch (Exception ignored){}

        //     // check error case
        //     if ( (settingsModel.getCustomTextSizeActive() && (customTextSize < settingsModel.getMinTextSize() || customTextSize > settingsModel.getMaxTextSize())) ){

        //         textFieldErrorDisplay.setText("Please enter a Number between " + settingsModel.getMinTextSize() + " and " + settingsModel.getMaxTextSize());
        //         textSizeField.setText("");

        //     } else{
        //         // Is Custom text field valid
        //         if ( (settingsModel.getCustomTextSizeActive() && (customTextSize >= settingsModel.getMinTextSize()) && (customTextSize <= settingsModel.getMaxTextSize())) ){
        //             settingsModel.setTxtSize(customTextSize);

        //         }else if ( (checkBoxTextSize != -1 )  ){
        //             settingsModel.setTxtSize(checkBoxTextSize);
        //         }

        //         // update Colours
        //         var grey  = new Color(0.85, 0.85, 0.85, 1);
        //         var lightBlue = new Color(0.72, 0.81, 0.81, 1);
        //         var slate = new Color(0.44, 0.5, 0.56, 1);
        //         var obsidian = new Color(0.44, 0.38, 0.48, 1);

        //         ArrayList<Color> colours = new ArrayList<>(List.of(Color.BLACK, Color.WHITE, lightBlue , grey, slate));

        //         //settingsModel.setBgColour(colours.get(backgroundColoursComboBox.getSelectionModel().getSelectedIndex()));
        //         //settingsModel.setTxtColour(colours.get(textColoursComboBox.getSelectionModel().getSelectedIndex()));

        //         switch ( colourThemesComboBox.getSelectionModel().getSelectedIndex() ){
        //             case 0 -> {
        //                 //classic

        //                 settingsModel.setBgColour(Color.WHITE);
        //                 settingsModel.setTxtColour(Color.BLACK);
        //             }
        //             case 1 -> {
        //                 //Sky Blue
        //                 settingsModel.setBgColour(lightBlue);
        //                 settingsModel.setTxtColour(Color.BLACK);
        //             }
        //             case 2 -> {
        //                 //Obsidian
        //                 settingsModel.setBgColour(obsidian);
        //                 settingsModel.setTxtColour(Color.BLACK);
        //             }
        //             case 3 -> {
        //                 //Slate
        //                 settingsModel.setBgColour(slate);
        //                 settingsModel.setTxtColour(Color.BLACK);
        //             }
        //         }

        //         settingsModel.setTxtSize(checkBoxTextSize);

        //         textFieldErrorDisplay.setText("");
        //         successLabel.setText("Success");

        //         Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
        //             sceneManager.popStackpane();
        //             successLabel.setText("");
        //         }));

        //         timeline.play();
        //     }
        // });

        ADLabel labelColours = new ADLabel("Colours", 8);
        labelColours.setFont("NunitoSans-ExtraLight.ttf");

        ADLabel labelTextSize = new ADLabel("Text Size", 8);
        labelTextSize.setFont("NunitoSans-ExtraLight.ttf");

        var mainWrapper = new VBox(
            labelColours,
            new HBox(new ADLabel("Theme"), colourThemesComboBox),
            labelTextSize,
            new HBox(smallTextButton, mediumTextButton, largeTextButton),
            new HBox(new ADLabel("In pixels (" + settingsModel.getMinTextSize() + " - " + settingsModel.getMaxTextSize() + ")"),
                                textSizeField, okBtnTextSize
            )
        );
        
        displayPane.setCenter(mainWrapper);
    }
}
