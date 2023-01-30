package group29.ui.scene;

import group29.data.ClickData;
import group29.data.ImpressionData;
import group29.data.ServerData;
import group29.enums.graph.LineType;
import group29.event.CampaignsModelUpdateListener;
import group29.model.CampaignModel;
import group29.model.GraphModel;
import group29.ui.SceneManager;
import group29.ui.element.ADButton;
import group29.ui.element.ADLabel;
import group29.ui.popup.HelpPopup;
import group29.ui.popup.NewCampaignPopup;
import group29.ui.popup.SettingsPopup;
import group29.model.TableRow;
import group29.utility.DataParser;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class CampaignSelectionScene extends SceneBase implements CampaignsModelUpdateListener {

    private ArrayList<TableRow> keyboardShortcuts = new ArrayList<>();

    public CampaignSelectionScene(SceneManager sceneManager) {
        super(sceneManager, false);

        displayPane.sceneProperty().addListener(ev -> {
            if (displayPane.getScene() != null) {
                sceneManager.getModelManager().getCampaignsModel().removeListener(this);
                sceneManager.getModelManager().getCampaignsModel().addListener(this);
            }
        });

        keyboardShortcuts = new ArrayList<>();
        keyboardShortcuts.add( new TableRow( "Esc", "Exits the application, or any open popups" ));
        keyboardShortcuts.add( new TableRow( "H", "Opens the help popup" ));
        keyboardShortcuts.add( new TableRow( "S", "Opens the settings popup" ));
        keyboardShortcuts.add( new TableRow( "N", "Opens the new campaign creation popup" ));

        sceneManager.getRootStackPane().setOnKeyPressed(t -> {
            switch (t.getCode()) {
                case S -> sceneManager.setPopup(new SettingsPopup(sceneManager));
                case H -> sceneManager.setPopup(new HelpPopup(sceneManager, keyboardShortcuts));
                case N -> sceneManager.setPopup(new NewCampaignPopup(sceneManager));
            }
        });

        updateCampaignList();
    }

    void updateCampaignList() {
        TilePane campaignList = new TilePane();
        ArrayList<CampaignModel> campaigns = sceneManager.getModelManager().getCampaignsModel().getCampaigns();

        for (var campaign : campaigns) {
            logger.info(campaign.getTitle());
            var editCampaignBtn = new ADButton(campaign.getTitle());
            editCampaignBtn.setOnAction(ev -> sceneManager.setScene(new CampaignScene(sceneManager, campaign)));
            campaignList.getChildren().add(editCampaignBtn);
        }

        var newCampaignBtn = new ADButton("New campaign", "btn-blue");
        newCampaignBtn.setOnAction(ev -> sceneManager.setPopup(new NewCampaignPopup(sceneManager)));
        campaignList.getChildren().add(newCampaignBtn);

        ADButton addTestCampaign = new ADButton("Add tiny test campaign");

        addTestCampaign.setOnAction(ev -> {
            try {
                var cd = (ClickData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/click_log_500.csv"));
                var id = (ImpressionData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/impression_log_500.csv"));
                var sd = (ServerData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/server_log_500.csv"));

                var campaign = new CampaignModel(id, cd, sd, "Facebook Campaign");

                var graph1 = new GraphModel(campaign);
                graph1.addActiveLine(LineType.NUM_OF_IMPRESSIONS);
                graph1.addActiveLine(LineType.NUM_OF_CLICKS);
                graph1.addActiveLine(LineType.NUM_OF_BOUNCES);

                campaign.addGraph(graph1);

                sceneManager.getModelManager().getCampaignsModel().addCampaign(campaign);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        ADButton addTestCampaign2 = new ADButton("Add 2 week campaign");

        addTestCampaign2.setOnAction(ev -> {
            try {
                var cd = (ClickData) DataParser.readFile(getClass().getResourceAsStream("/2_week_campaign_2/click_log.csv"));
                var id = (ImpressionData) DataParser.readFile(getClass().getResourceAsStream("/2_week_campaign_2/impression_log.csv"));
                var sd = (ServerData) DataParser.readFile(getClass().getResourceAsStream("/2_week_campaign_2/server_log.csv"));

                var campaign = new CampaignModel(id, cd, sd, "Big cloudflare campaign");

                var graph1 = new GraphModel(campaign);
                graph1.addActiveLine(LineType.NUM_OF_CLICKS);
                graph1.addActiveLine(LineType.NUM_OF_UNIQUES);
                graph1.setTimeGranularity(3600);
                
                campaign.addGraph(graph1);

                sceneManager.getModelManager().getCampaignsModel().addCampaign(campaign);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        ADButton addTestCampaign3 = new ADButton("Add 2 month campaign");

        addTestCampaign3.setOnAction(ev -> {
            try {
                var cd = (ClickData) DataParser.readFile(getClass().getResourceAsStream("/2_month_campaign/click_log.csv"));
                var id = (ImpressionData) DataParser.readFile(getClass().getResourceAsStream("/2_month_campaign/impression_log.csv"));
                var sd = (ServerData) DataParser.readFile(getClass().getResourceAsStream("/2_month_campaign/server_log.csv"));

                var campaign = new CampaignModel(id, cd, sd, "Huge 2 month campaign");

                var graph1 = new GraphModel(campaign);
                graph1.addActiveLine(LineType.NUM_OF_CLICKS);
                graph1.addActiveLine(LineType.NUM_OF_UNIQUES);
                graph1.setTimeGranularity(3600);
                
                campaign.addGraph(graph1);

                sceneManager.getModelManager().getCampaignsModel().addCampaign(campaign);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        topBar.setShortcuts(keyboardShortcuts);

        var labelTitle = new ADLabel("AD Auction Dashboard", 48);
        labelTitle.setFont("NunitoSans-ExtraLight.ttf");

        var centerVBox = new VBox(labelTitle, campaignList);
        displayPane.setCenter(centerVBox);
        makeScrollable(centerVBox, 128);

        var debugButtons = new HBox(addTestCampaign, addTestCampaign2, addTestCampaign3);

        addTestCampaign.setAlignment(Pos.BOTTOM_RIGHT);
        HBox.setHgrow(addTestCampaign, Priority.ALWAYS);
        //displayPane.setBottom(debugButtons);
    }

    public void campaignsUpdate() {
        updateCampaignList(); // no need to relink campaignsModel, since we already have a reference to it
    }

    public boolean isAlive() {
        return this.displayPane.getScene() != null;
    }
}
