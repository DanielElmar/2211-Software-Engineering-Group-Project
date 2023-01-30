package group29.ui.scene;

import group29.data.ClickData;
import group29.data.ImpressionData;
import group29.data.ServerData;
import group29.enums.data.Age;
import group29.enums.data.Context;
import group29.enums.data.Income;
import group29.enums.graph.LineType;
import group29.enums.ui.CampaignDisplay;
import group29.event.CampaignUpdateListener;
import group29.model.CampaignModel;
import group29.model.GraphModel;
import group29.ui.GraphRenderer;
import group29.ui.SceneManager;
import group29.ui.element.ADButton;
import group29.ui.element.ADLabel;
import group29.ui.popup.GraphPopup;
import group29.ui.popup.HelpPopup;
import group29.ui.popup.PrintPopup;
import group29.ui.popup.SettingsPopup;
import group29.model.TableRow;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CampaignScene extends SceneBase implements CampaignUpdateListener {

    private final int DATA_POINTS_IN_VIEW_INIT = 400;

    // todo show filename, change files maybe?
    private final ServerData serverData;
    private final ImpressionData impressionData;
    private final ClickData clickData;
    private final CampaignModel campaignModel;

    private final DecimalFormat df = new DecimalFormat("###.####");
    private final DecimalFormat dfl = new DecimalFormat("###.#######");

    private VBox basicSummaryContainer;
    private VBox detailedSummaryContainer;
    private VBox graphsContainer;

    private VBox previousDisplayContainer;
    private CampaignDisplay currentDisplay;

    private final ADButton btnBasicSummary;
    private final ADButton btnDetailedSummary;
    private final ADButton btnGraphs;
    
    public CampaignScene(SceneManager sceneManager, CampaignModel campaignModel) {
        super(sceneManager, true);

        this.campaignModel = campaignModel;
        this.serverData = campaignModel.getServerData();
        this.clickData = campaignModel.getClickData();
        this.impressionData = campaignModel.getImpressionData();

        displayPane.sceneProperty().addListener(ev -> {
            if (displayPane.getScene() != null) {
                campaignModel.removeListener(this);
                campaignModel.addListener(this);
            }
        });

        // Add shortcuts to Help PopUp
        ArrayList<TableRow> shortcuts = new ArrayList<>();
        shortcuts.add(new TableRow("Esc", "Navigates back to the Campaign Selection Page, or closes any open popups"));
        shortcuts.add(new TableRow("H", "Opens the help popup"));
        shortcuts.add(new TableRow("S", "Opens the settings popup"));
        shortcuts.add(new TableRow("B", "Navigates to Basic Summary page"));
        shortcuts.add(new TableRow("D", "Navigates to Detailed Summary Page"));
        shortcuts.add(new TableRow("N", "Opens the new Graph creation popup"));

        topBar.setShortcuts(shortcuts);

        btnBasicSummary = new ADButton("Basic Summary", "btn-blue");
        btnDetailedSummary = new ADButton("Detailed Summary");
        btnGraphs = new ADButton("Graphs");

        btnBasicSummary.setOnAction(e -> setDisplay(CampaignDisplay.BASIC_SUMMARY));
        btnDetailedSummary.setOnAction(e -> setDisplay(CampaignDisplay.DETAILED_SUMMARY));
        btnGraphs.setOnAction(e -> setDisplay(CampaignDisplay.GRAPHS));

        topBar.addToCenterButtons(btnGraphs);
        topBar.addToCenterButtons(btnDetailedSummary);
        topBar.addToCenterButtons(btnBasicSummary);

        ADButton backButton = new ADButton("", "btn-circle");
        backButton.setIcon("back.png", 20);
        backButton.setOnAction(e -> sceneManager.popStackpane());

        ADButton printButton = new ADButton("", "btn-circle");
        printButton.setIcon("print.png", 24);
        printButton.setOnAction(ev -> {
            sceneManager.setPopup(new PrintPopup(sceneManager));
        });

        topBar.addToLeftButtons(backButton);
        topBar.addToLeftButtons(printButton);

        sceneManager.getRootStackPane().setOnKeyPressed(t -> {
            switch (t.getCode()) {
                case S -> sceneManager.setPopup(new SettingsPopup(sceneManager));
                case H -> sceneManager.setPopup(new HelpPopup(sceneManager, shortcuts));
                case B -> setDisplay(CampaignDisplay.BASIC_SUMMARY);
                case D -> setDisplay(CampaignDisplay.DETAILED_SUMMARY);
                case N -> createNewGraph();
            }
        });

        buildBasicSummary();
        buildDetailedSummary();
        buildGraphList();

        setDisplay(CampaignDisplay.BASIC_SUMMARY);
    }

    private void createNewGraph() {
        if (sceneManager.getIsShowingPopup()) {
            return;
        }

        setDisplay(CampaignDisplay.GRAPHS);

        var graph = new GraphModel(campaignModel);
        campaignModel.addGraph(graph);

        var popup = new GraphPopup(sceneManager, graph);
        popup.changeTitle("New graph");

        sceneManager.setPopup(popup);
    }

    private void setDisplay(CampaignDisplay newDisplay) {
        setDisplay(newDisplay, false);
    }

    private void setDisplay(CampaignDisplay newDisplay, boolean forceSet) {
        if (currentDisplay == newDisplay && !forceSet) {
            return;
        }

        boolean appearFromRight = true;
        if ((currentDisplay == CampaignDisplay.DETAILED_SUMMARY && newDisplay == CampaignDisplay.BASIC_SUMMARY)
            || (currentDisplay == CampaignDisplay.GRAPHS && newDisplay == CampaignDisplay.DETAILED_SUMMARY)
            || (currentDisplay == CampaignDisplay.GRAPHS && newDisplay == CampaignDisplay.BASIC_SUMMARY)
         ) {
            appearFromRight = false;
        }

        currentDisplay = newDisplay;

        btnBasicSummary.setClass("");
        btnDetailedSummary.setClass("");
        btnGraphs.setClass("");
        
        VBox currentDisplayContainer;

        switch (currentDisplay) {
            case BASIC_SUMMARY -> {btnBasicSummary.setClass("btn-blue"); currentDisplayContainer = basicSummaryContainer;}
            case DETAILED_SUMMARY -> {btnDetailedSummary.setClass("btn-blue"); currentDisplayContainer = detailedSummaryContainer;}
            case GRAPHS -> {btnGraphs.setClass("btn-blue"); currentDisplayContainer = graphsContainer;}
            default -> currentDisplayContainer = null; // This will never happen (currentDisplay can only have 3 possible values)
        }

        if (previousDisplayContainer != null && !forceSet) {
            int animationSpeed = 200;
            int xOffset = 12;

            var previousTitle = previousDisplayContainer.getChildren().get(0);
            var previousContent = previousDisplayContainer.getChildren().get(1);
            var currentTitle = currentDisplayContainer.getChildren().get(0);
            var currentContent = currentDisplayContainer.getChildren().get(1);

            // todo The first child of a display container is the campaign title
            // the second child is the contents; this is very odd and can cause
            // problems later on, if code is changed
            FadeTransition ft = new FadeTransition(Duration.millis(animationSpeed), previousContent);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.play();

            TranslateTransition tt = new TranslateTransition(Duration.millis(animationSpeed), previousContent);
            tt.setFromX(0);
            tt.setToX(appearFromRight ? -xOffset : xOffset);
            tt.play();

            FadeTransition ft2 = new FadeTransition(Duration.millis(animationSpeed), currentContent);
            ft2.setFromValue(0);
            ft2.setToValue(1);
            ft2.play();

            TranslateTransition tt2 = new TranslateTransition(Duration.millis(animationSpeed), currentContent);
            tt2.setFromX(appearFromRight ? xOffset : -xOffset);
            tt2.setToX(0);
            tt2.play();

            previousTitle.setOpacity(0);
            currentTitle.setOpacity(1);
            
            // This is needed, because otherwise displays will have the large height of another display
            // and that messes up scrolling
            var previousContainer = new VBox(previousDisplayContainer);
            var currentContainer = new VBox(currentDisplayContainer);
            previousContainer.setStyle("-fx-alignment: top-center");
            currentContainer.setStyle("-fx-alignment: top-center");
            
            var stackPane = new StackPane(previousContainer, currentContainer);
            stackPane.setStyle("-fx-alignment: top-center");
            logger.info("setting stack pane");
            displayPane.setCenter(stackPane);
            logger.info("finished");

            //tt2.setOnFinished(e -> displayPane.setCenter(currentDisplayContainer));
        } else {
            displayPane.setCenter(currentDisplayContainer);
        }

        previousDisplayContainer = currentDisplayContainer;
    }

    private void buildBasicSummary() {
        var labelClickData = new ADLabel("Click data", 16);
        labelClickData.setFont("NunitoSans-ExtraLight.ttf");

        var clickDataContainer = new VBox(labelClickData,
            createValueLine("Number Of Rows", String.valueOf(clickData.getNumOfRows())),
            createValueLine("Earliest Date", new Date(clickData.getEarliestDate() * 1000).toString()),
            createValueLine("Latest Date", new Date(clickData.getLatestDate() * 1000).toString()),
            createValueLine("Timespan", TimeUnit.DAYS.convert(clickData.getTimeSpanInMillies(), TimeUnit.MILLISECONDS) + " Days")
        );

        var labelServerData = new ADLabel("Server data", 16);
        labelServerData.setFont("NunitoSans-ExtraLight.ttf");

        var serverDataContainer = new VBox(labelServerData,
            createValueLine("Number of Rows", String.valueOf(serverData.getNumOfRows())),
            createValueLine("Longest Stay", TimeUnit.MINUTES.convert(serverData.getLongestStayInSeconds(), TimeUnit.SECONDS) + " minutes"),
            createValueLine("Average Stay", TimeUnit.MINUTES.convert((serverData.getStaySumInSeconds() / serverData.getNumOfRows()), TimeUnit.SECONDS) + " minutes"),
            createValueLine("Bounce Rate", df.format(serverData.getBounceRate() * 100) + "%")
        );
        
        var labelImpressionData = new ADLabel("Impression data", 16);
        labelImpressionData.setFont("NunitoSans-ExtraLight.ttf");

        var impressionDataContainer = new VBox(labelImpressionData,
            createValueLine("Number of Rows", String.valueOf(impressionData.getNumOfRows())),
            createValueLine("Earliest Date", new Date(impressionData.getEarliestDate() * 1000).toString()),
            createValueLine("Latest Date", new Date(impressionData.getLatestDate() * 1000).toString()),
            createValueLine("Time Span", TimeUnit.DAYS.convert(impressionData.getTimeSpanInMillies(), TimeUnit.MILLISECONDS) + " Days")
        );

        clickDataContainer.setStyle("-fx-alignment: center-left; -fx-spacing: 4px");
        serverDataContainer.setStyle("-fx-alignment: center-left; -fx-spacing: 4px");
        impressionDataContainer.setStyle("-fx-alignment: center-left; -fx-spacing: 4px");
        
        basicSummaryContainer = new VBox(createCampaignTitleLabel(), new VBox(clickDataContainer, serverDataContainer, impressionDataContainer));
        basicSummaryContainer.setStyle("-fx-alignment: top-center");
        makeScrollable(basicSummaryContainer, 96);
    }

    private void buildDetailedSummary() {
        // Click data
        var labelClickData = new ADLabel("Click data", 16);
        labelClickData.setFont("NunitoSans-ExtraLight.ttf");

        var clickDataLines = new VBox(
            createValueLine("Number Of Rows", String.valueOf(clickData.getNumOfRows())),
            createValueLine("Earliest Date", new Date(clickData.getEarliestDate() * 1000).toString()),
            createValueLine("Latest Date", new Date(clickData.getLatestDate() * 1000).toString()),
            createValueLine("Timespan", TimeUnit.DAYS.convert(clickData.getTimeSpanInMillies(), TimeUnit.MILLISECONDS) + " Days"),
            createValueLine("Lowest Click Cost", String.valueOf(clickData.getLowestClickCost())),
            createValueLine("Highest Click Cost", String.valueOf(clickData.getHighestClickCost())),
            createValueLine("Average Click Cost", String.valueOf(df.format(clickData.getAverageClickCost())))
        );
        
        // todo variance?
        var clickDataGraphModel = new GraphModel(campaignModel);
        clickDataGraphModel.addActiveLine(LineType.NUM_OF_CLICKS);
        clickDataGraphModel.addActiveLine(LineType.CPA);
        clickDataGraphModel.addActiveLine(LineType.CPC);
        clickDataGraphModel.setTimeGranularity( (int) ((campaignModel.getClickData().getTimeSpanInMillies() / 1000) / DATA_POINTS_IN_VIEW_INIT));

        var clickDataGraphRender = new GraphRenderer(sceneManager, clickDataGraphModel, false);

        var clickDataLinesAndGraph = new TilePane(clickDataLines, clickDataGraphRender.getDisplayPane()); //
        VBox clickDataContainer = new VBox(labelClickData, clickDataLinesAndGraph);

        // Server data
        var labelServerData = new ADLabel("Server data", 16);
        labelServerData.setFont("NunitoSans-ExtraLight.ttf");

        var serverDataLines = new VBox(
            createValueLine("Number of Rows", String.valueOf(serverData.getNumOfRows())),
            createValueLine("Longest Stay", TimeUnit.MINUTES.convert(serverData.getLongestStayInSeconds(), TimeUnit.SECONDS) + " minutes"),
            createValueLine("Average Stay", TimeUnit.MINUTES.convert((serverData.getStaySumInSeconds() / serverData.getNumOfRows()), TimeUnit.SECONDS) + " minutes"),
            createValueLine("Highest Pages Viewed", df.format(serverData.getHighestPagesViewed())),
            // todo add an explain in the help (this is set such that only one page viewed = bounce)
            createValueLine("Bounce Rate (default)", df.format(serverData.getBounceRate() * 100) + "%"),
            createValueLine("Average Pages Viewed", String.valueOf(dfl.format(serverData.getAveragePagesViewed()))),
            createValueLine("Conversion Rate", df.format(serverData.getConversionRate() * 100) + "%")
        );

        var serverDataGraphModel = new GraphModel(campaignModel);
        serverDataGraphModel.addActiveLine(LineType.BOUNCE_RATE);
        serverDataGraphModel.addActiveLine(LineType.NUM_OF_BOUNCES);
        serverDataGraphModel.setTimeGranularity( (int) ((campaignModel.getServerData().getTimeSpanInMillies() / 1000) / DATA_POINTS_IN_VIEW_INIT));

        var serverDataGraphRender = new GraphRenderer(sceneManager, serverDataGraphModel, false);

        var serverDataLinesAndGraph = new TilePane(serverDataLines, serverDataGraphRender.getDisplayPane()); //
        VBox serverDataContainer = new VBox(labelServerData, serverDataLinesAndGraph);

        // Impression Summary
        var labelImpressionData = new ADLabel("Impression data", 16);
        labelImpressionData.setFont("NunitoSans-ExtraLight.ttf");

        var ageDistribution = impressionData.getAgeDistribution();
        var incomeDistribution = impressionData.getIncomeDistribution();
        var contextDistribution = impressionData.getContextDistribution();

        var impressionDataLines = new VBox(
            // Number of Rows
                createValueLine("Number of Rows", String.valueOf(impressionData.getNumOfRows())),
                createValueLine("Earliest Date", new Date(impressionData.getEarliestDate() * 1000).toString()),
                createValueLine("Latest Date", new Date(impressionData.getLatestDate() * 1000).toString()),
                createValueLine("Timespan", TimeUnit.DAYS.convert(impressionData.getTimeSpanInMillies(), TimeUnit.MILLISECONDS) + " Days"),
                createValueLine("Highest Impression Cost", String.valueOf(dfl.format(impressionData.getHighestImpressionCost()))),
                createValueLine("Average Impression Cost", String.valueOf(dfl.format(impressionData.getAverageImpressionCost()))),
                createValueMultiline("Gender distribution",
                                df.format(impressionData.getGenderDistribution().getKey() * 100) + "% Male",
                                df.format(impressionData.getGenderDistribution().getValue() * 100) + "% Female"),
                createValueMultiline("Age distribution",
                                df.format(ageDistribution.get(Age.UNDER_25) * 100) + "% Under 25; ",
                                df.format(ageDistribution.get(Age.AGES_25_TO_34) * 100) + "% Ages 25-34; ",
                                df.format(ageDistribution.get(Age.AGES_35_TO_44) * 100) + "% Ages 35-44; ",
                                df.format(ageDistribution.get(Age.AGES_45_TO_54) * 100) + "% Ages 45-54; ",
                                df.format(ageDistribution.get(Age.OVER_54) * 100) + "% Over 54"),
                createValueMultiline("Income distibution", 
                                df.format(incomeDistribution.get(Income.LOW) * 100) + "% Low; ",
                                df.format(incomeDistribution.get(Income.MEDIUM) * 100) + "% Medium; ",
                                df.format(incomeDistribution.get(Income.HIGH) * 100) + "% High"),
                createValueMultiline("Context distribution",
                                df.format((contextDistribution.get(Context.BLOG) * 100)) + "% Blog",
                                df.format((contextDistribution.get(Context.NEWS) * 100)) + "% News",
                                df.format((contextDistribution.get(Context.SHOPPING) * 100)) + "% Shopping",
                                df.format((contextDistribution.get(Context.SOCIAL_MEDIA) * 100)) + "% Social Media",
                                df.format((contextDistribution.get(Context.TRAVEL) * 100)) + "% Travel",
                                df.format((contextDistribution.get(Context.HOBBIES) * 100)) + "% Hobbies"
                )
        );
                
        // Context Distribution
        var impressionDataGraphModel = new GraphModel(campaignModel);
        impressionDataGraphModel.addActiveLine(LineType.NUM_OF_IMPRESSIONS);
        impressionDataGraphModel.addActiveLine(LineType.CPM);
        impressionDataGraphModel.setTimeGranularity( (int) ((campaignModel.getImpressionData().getTimeSpanInMillies() / 1000) / DATA_POINTS_IN_VIEW_INIT));

        var impressionDataGraphRender = new GraphRenderer(sceneManager, impressionDataGraphModel, false);

        var impressionDataLinesAndGraph = new TilePane(impressionDataLines, impressionDataGraphRender.getDisplayPane());
        VBox impressionDataContainer = new VBox(labelImpressionData, impressionDataLinesAndGraph);

        var leftStyle = "-fx-alignment: center-left; -fx-spacing: 4px";

        clickDataLines.setStyle(leftStyle);
        serverDataLines.setStyle(leftStyle);
        impressionDataLines.setStyle(leftStyle);

        clickDataLinesAndGraph.setStyle(leftStyle);
        serverDataLinesAndGraph.setStyle(leftStyle);
        impressionDataLinesAndGraph.setStyle(leftStyle);

        clickDataContainer.setStyle(leftStyle);
        serverDataContainer.setStyle(leftStyle);
        impressionDataContainer.setStyle(leftStyle);

        detailedSummaryContainer = new VBox(createCampaignTitleLabel(), new VBox(clickDataContainer, serverDataContainer, impressionDataContainer));
        detailedSummaryContainer.setStyle("-fx-alignment: top-center");
        makeScrollable(detailedSummaryContainer, 96);
    }

    public void buildGraphList() {
        GridPane graphTiles = new GridPane();
        
        int maxRows = 2;
        int column = 0;
        int row = 0;

        for (GraphModel graph : campaignModel.getGraphs()) {
            var g = graph.getGraphRenderer();

            logger.info("assigning new graph");
            if (g == null) {
                g = new GraphRenderer(sceneManager, graph);
            }

            graphTiles.add(g.getDisplayPane(), row, column);

            row += 1;
            if (row >= maxRows) {
                column += 1;
                row = 0;
            }
        }

        var newGraphButton = new ADButton("", "btn-new-graph");
        //newGraphButton.setMinSize(450, 360);
        newGraphButton.setSize(450 + 16, 360 + 16);
        newGraphButton.removeBackButton();
        newGraphButton.setIcon("plus.png", 128);
        //newGraphButton.setSize(256, 128);

        newGraphButton.setOnAction(ev -> {
            var graph = new GraphModel(campaignModel);
            campaignModel.addGraph(graph);

            var popup = new GraphPopup(sceneManager, graph);
            popup.changeTitle("New graph");

            sceneManager.setPopup(popup);
        });

        graphTiles.add(newGraphButton, row, column);

        graphsContainer = new VBox(createCampaignTitleLabel(), graphTiles);
        graphsContainer.setStyle("-fx-alignment: top-center");
        graphTiles.setStyle("-fx-alignment: top-center");

        makeScrollable(graphsContainer, 96);
    }

    private HBox createValueLine(String valueName, String value) {
        var labelName = new ADLabel(valueName + ":");
        labelName.setIsBold(true);
        var labelValue = new ADLabel(value);

        var returnHBox = new HBox(labelName, labelValue);
        returnHBox.setStyle("-fx-alignment: center-left; -fx-spacing: 4px;");

        return returnHBox;
    }

    private VBox createValueMultiline(String valueName, String... values) {
        var labelName = new ADLabel(valueName);
        labelName.setIsBold(true);
        var valuesVBox = new VBox();

        for (String value : values) {
            valuesVBox.getChildren().add(new ADLabel(value));
        }

        valuesVBox.setPadding(new Insets(0, 0, 0, 32));
        valuesVBox.setStyle("-fx-alignment: center-left; -fx-spacing: 4px;");
        
        var returnVBox = new VBox(labelName, valuesVBox);
        returnVBox.setStyle("-fx-alignment: center-left; -fx-spacing: 4px;");

        return returnVBox;
    }

    private ADLabel createCampaignTitleLabel() {
        var labelCampaignTitle = new ADLabel(campaignModel.getTitle(), 32);
        labelCampaignTitle.setFont("NunitoSans-ExtraLight.ttf");
        labelCampaignTitle.setAlignment(Pos.TOP_CENTER);

        return labelCampaignTitle;
    }

    public void campaignUpdate() {
        // TODO - This might cause bugs! these 2 sections never change, but if they will do in the future
        // these lines will have to be uncommented
        // buildBasicSummary();
        // buildDetailedSummary();
        buildGraphList();

        setDisplay(currentDisplay, true);
    }

    public boolean isAlive() {
        return this.displayPane.getScene() != null;
    }
}
