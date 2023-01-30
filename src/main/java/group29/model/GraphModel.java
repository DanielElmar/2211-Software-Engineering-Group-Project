package group29.model;

import group29.data.ClickData;
import group29.data.DataRow;
import group29.data.ImpressionData;
import group29.data.ServerData;
import group29.enums.graph.LineType;
import group29.event.GraphUpdateListener;
import group29.ui.GraphRenderer;
import javafx.scene.chart.LineChart;
import javafx.util.Pair;

import java.util.ArrayList;

public class GraphModel {

    private final CampaignModel campaignModel;

    private final ImpressionData impressionData;
    private final ClickData clickData;
    private final ServerData serverData;

    private long timeGranularity = 400;
    private Pair<Double, Double> timeInterval;
    private final ArrayList<LineType> activeLines;
    private final ArrayList<FilterModel> filters;
    private final ArrayList<GraphUpdateListener> graphUpdateListeners;
    
    private final Pair<Long, Long> timeBoundsOfData;

    private final CustomBounceModel bounceModel;

    private boolean hasCustomMaxY = false;
    private double customMaxY = 0;

    public boolean showingAdvancedSettings = false;

    // Needed for exporting
    private GraphRenderer graphRenderer = null;
    
    public GraphModel(CampaignModel campaign) {
        this.campaignModel = campaign;

        this.bounceModel = new CustomBounceModel();
        this.impressionData = campaign.getImpressionData();
        this.clickData = campaign.getClickData();
        this.serverData = campaign.getServerData();

        long minTime = impressionData.getEarliestDate();
        long maxTime = impressionData.getLatestDate();

        if (clickData.getEarliestDate() < minTime) minTime = clickData.getEarliestDate();
        if (serverData.getEarliestDate() < minTime) minTime = serverData.getEarliestDate();
        if (clickData.getLatestDate() > maxTime) maxTime = clickData.getLatestDate();
        if (serverData.getLatestDate() > maxTime) maxTime = serverData.getLatestDate();

        timeBoundsOfData = new Pair<>(minTime, maxTime);

        setTimeInterval(new Pair<>(Double.valueOf(timeBoundsOfData.getKey()), Double.valueOf(timeBoundsOfData.getValue())));
        
        activeLines = new ArrayList<>();
        filters = new ArrayList<>();
        graphUpdateListeners = new ArrayList<>();
    }
    
    public boolean getHasCustomMaxY() {
        return hasCustomMaxY;
    }

    public void setHasCustomMaxY(boolean value) {
        hasCustomMaxY = value;
    }
    
    public double getCustomMaxY() {
        return customMaxY;
    }

    public void setCustomMaxY(double value) {
        customMaxY = value;
    }

    public CustomBounceModel getBounceModel() {
        return bounceModel;
    }

    public CampaignModel getCampaignModel() {
        return campaignModel;
    }

    public ImpressionData getImpressionData() {
        return impressionData;
    }

    public ClickData getClickData() {
        return clickData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public long getTimeGranularity() {
        return timeGranularity;
    }

    public void setTimeGranularity(long timeGranulation) {
        setTimeGranularity(timeGranulation, true);
    }

    public void setTimeGranularity(long timeGranulation, boolean shouldTriggerListeners) {
        this.timeGranularity = timeGranulation;

        if (shouldTriggerListeners)
            triggerListeners();
    }

    public Pair<Double, Double> getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Pair<Double, Double> timeInterval) {
        setTimeInterval(timeInterval, true);
    }

    public void setTimeInterval(Pair<Double, Double> timeInterval, boolean shouldTriggerListeners) {
        this.timeInterval = timeInterval;
    }

    public void removeActiveLine(LineType lineType) {
        activeLines.remove(lineType);
    }

    public void addActiveLine(LineType lineType) {
        addActiveLine(lineType, true);
    }

    // Dirty solution...
    public void addActiveLine(LineType lineType, Boolean shouldTriggerListeners) {
        activeLines.add(lineType);

        if (shouldTriggerListeners)
            triggerListeners();
    }

    public ArrayList<LineType> getActiveLines() {
        return activeLines;
    }

    public ArrayList<FilterModel> getFilters() {
        return filters;
    }

    public void addFilter(FilterModel filterModel) {
        filters.add(filterModel);
    }
    
    public void addListener(GraphUpdateListener graphUpdateListener){
        graphUpdateListeners.add(graphUpdateListener);
    }

    public void removeListener(GraphUpdateListener graphUpdateListener){
        graphUpdateListeners.remove(graphUpdateListener);
    }

    private void triggerListeners(){
        var listeners = new ArrayList<>(graphUpdateListeners);
        for (var listener : listeners) {
            if (!listener.isAlive()) {
                removeListener(listener);
                continue;
            }
            
            listener.graphUpdate();
        }
    }

    public void setGraphRenderer(GraphRenderer graphRenderer) {
        this.graphRenderer = graphRenderer;
    }

    public GraphRenderer getGraphRenderer() {
        return graphRenderer;
    }

    public Pair<Long, Long> getTimeBoundsOfData() {
        return timeBoundsOfData;
    }
}
