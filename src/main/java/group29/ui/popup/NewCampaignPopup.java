package group29.ui.popup;

import group29.data.ClickData;
import group29.data.ImpressionData;
import group29.data.ServerData;
import group29.enums.data.DataType;
import group29.model.CampaignModel;
import group29.ui.SceneManager;
import group29.ui.element.ADButton;
import group29.ui.element.ADLabel;
import group29.ui.element.ADTextField;
import group29.ui.scene.CampaignSelectionScene;
import group29.utility.DataParser;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.InputStream;

public class NewCampaignPopup extends Popup {

    protected ImpressionData impressionData;
    protected ServerData serverData;
    protected ClickData clickData;
    protected final SimpleStringProperty title = new SimpleStringProperty();

    private final ADLabel labelNotification = new ADLabel("Please import the 3 campaign files needed");
    
    private final Image imgEmpty = new Image(getClass().getResourceAsStream("/images/icons/empty.png"));
    private final Image imgLoading = new Image(getClass().getResourceAsStream("/images/icons/loading.png"));
    private final Image imgError = new Image(getClass().getResourceAsStream("/images/icons/error.png"));
    private final Image imgSuccess = new Image(getClass().getResourceAsStream("/images/icons/success.png"));
    
    private final ImageView imgViewImpressions;
    private final ImageView imgViewClick;
    private final ImageView imgViewServer;
    private final ImageView imgViewSubmit;

    private final ADButton btnSubmit;
    public NewCampaignPopup(SceneManager sceneManager) {
        super(sceneManager, "New Campaign");
        
        var btnImpressionsLog = new ADButton("Add impression log");
        var btnClickLog = new ADButton("Add click log");
        var btnServerLog = new ADButton("Add server log");
        btnSubmit = new ADButton("OK", "btn-blue");
        btnSubmit.setDisable(true);

        btnImpressionsLog.setOnAction(ev -> genericAddLog(DataType.IMPRESSION_LOG));
        btnClickLog.setOnAction(ev -> genericAddLog(DataType.CLICK_LOG));
        btnServerLog.setOnAction(ev -> genericAddLog(DataType.SERVER_LOG));
        btnSubmit.setOnAction(ev -> submit());

        var titleBox = new ADTextField();
        title.bind(titleBox.textProperty());
        titleBox.textProperty().addListener((o, ov, nv) -> {
            if (nv.equals("")) {
                if (getImportsRemaining() == 0) {
                    labelNotification.setStyleClass("label-inactive");
                    labelNotification.setText("Please name your campaign.");
                }

                btnSubmit.setDisable(true);
            } else if (getImportsRemaining() == 0) {
                labelNotification.setText("");
                btnSubmit.setDisable(false);
            }
        });

        titleBox.setPromptText("New campaign's title");
        labelNotification.getStyleClass().add("label-inactive");
        labelNotification.setIsBold(true);
        
        imgViewImpressions = new ImageView(imgEmpty);
        imgViewClick = new ImageView(imgEmpty);
        imgViewServer = new ImageView(imgEmpty);
        imgViewSubmit = new ImageView(imgEmpty);

        imgViewImpressions.setPreserveRatio(true);
        imgViewClick.setPreserveRatio(true);
        imgViewServer.setPreserveRatio(true);
        imgViewSubmit.setPreserveRatio(true);

        imgViewImpressions.setFitWidth(32);
        imgViewClick.setFitWidth(32);
        imgViewServer.setFitWidth(32);
        imgViewSubmit.setFitWidth(32);

        var vBoxButtons = new VBox(
            new HBox(btnImpressionsLog, imgViewImpressions),
            new HBox(btnClickLog, imgViewClick),
            new HBox(btnServerLog, imgViewServer),
            new HBox(btnSubmit, imgViewSubmit)
        );

        vBoxButtons.setPadding(new Insets(0, 0, 0, 48));
        
        displayPane.setCenter(new VBox(
            new HBox(titleBox), 
            vBoxButtons,
            labelNotification
        ));
    }

    private InputStream getFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        try {
            return new FileInputStream(fileChooser.showOpenDialog(sceneManager.getJavaFxStage()));
        } catch (Exception e) {
            logger.error("File picker: file not found!");
            return null;
        }
    }

    private void genericAddLog(DataType dataType) {
        final ImageView imgViewStatus;
        switch (dataType) {
            case IMPRESSION_LOG -> {imgViewStatus = imgViewImpressions; impressionData = null;}
            case CLICK_LOG -> {imgViewStatus = imgViewClick; clickData = null;}
            case SERVER_LOG -> {imgViewStatus = imgViewServer; serverData = null;}
            
            // This has to be here in order to make java happy for some reason?
            // But this default will never be entered
            default -> imgViewStatus = imgViewImpressions;
        }

        var dataTypeName = dataType.toString().replace("_", " ");
        var loadedFile = getFile();

        Task<Void> loadFileInBackground = new Task<Void>() {
            protected Void call() {
                RotateTransition rt = getRotateTransitionForNode(imgViewStatus);
                Platform.runLater(() -> {
                    imgViewStatus.setImage(imgLoading);
                    rt.play();
                });

                try {
                    if (loadedFile != null) {
                        var loadedData = DataParser.readFile(loadedFile);
                        
                        if (loadedData.getType() == dataType) {
                            Platform.runLater(() -> {
                                logger.info(dataTypeName + " added");

                                switch (dataType) {
                                    case IMPRESSION_LOG -> impressionData = (ImpressionData) loadedData;
                                    case CLICK_LOG -> clickData = (ClickData) loadedData;
                                    case SERVER_LOG -> serverData = (ServerData) loadedData;
                                }
                                
                                imgViewStatus.setImage(imgSuccess);
                                rt.stop();
                                imgViewStatus.setRotate(0);
                                setSuccessfulImportNotification();
                            });
                        } else throw new Exception("Invalid " + dataTypeName + " file");
                    } else throw new Exception("Couldn't read file");
                } catch (Exception e) {
                    logger.info(e);
                    Platform.runLater(() -> {
                        imgViewStatus.setImage(imgError);
                        rt.stop();
                        imgViewStatus.setRotate(0);
                        labelNotification.setStyleClass("label-error");
                        labelNotification.setText("ERROR: " + e.getMessage());
                    });
                }

                return null; // Don't ask me why, but apparently Task needs this
            }
        };

        new Thread(loadFileInBackground).start();
    }

    private RotateTransition getRotateTransitionForNode(Node node) {
        RotateTransition rt = new RotateTransition(Duration.millis(2000), node);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setFromAngle(0);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        
        return rt;
    }

    private void submit(){
        //notificationMessage.setText("Adding campaign...");

        if(impressionData != null && serverData != null && clickData != null && !title.getValue().equals("")) {
            CampaignModel campaignModel = new CampaignModel(impressionData, clickData, serverData, title.getValue());
            sceneManager.getModelManager().getCampaignsModel().addCampaign(campaignModel);
            logger.info("added campaign, title: " + title);
            sceneManager.setScene(new CampaignSelectionScene(sceneManager));
        } else {
            logger.info("cannot submit yet!");
            labelNotification.setStyleClass("label-error");
            labelNotification.setText("ERROR: ");
        }
    }

    private void setSuccessfulImportNotification() {
        labelNotification.setStyleClass("label-ok");
        btnSubmit.setDisable(true);

        if (getImportsRemaining() > 0) {
            labelNotification.setText("OK: Please import the other " + getImportsRemaining() + " files needed");
        } else {
            if (title.getValue().equals("")) {
                labelNotification.setText("OK: Please name your campaign.");
            } else {
                labelNotification.setText("");
                btnSubmit.setDisable(false);
            }
        }
    }

    private int getImportsRemaining() {
        int remainingImports = 0;
        if (impressionData == null) remainingImports += 1;
        if (clickData == null) remainingImports += 1;
        if (serverData == null) remainingImports += 1;

        return remainingImports;
    }
}
