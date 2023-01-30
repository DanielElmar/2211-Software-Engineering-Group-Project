package group29.model;

import group29.data.ClickData;
import group29.data.ImpressionData;
import group29.data.ServerData;
import group29.event.CampaignUpdateListener;

import java.util.ArrayList;

public class CampaignModel {

    private final ImpressionData impressionData;
    private final ClickData clickData;
    private final ServerData serverData;
    private final String title;

    private final ArrayList<GraphModel> graphs;
    private final ArrayList<CampaignUpdateListener> campaignUpdateListeners;

    public CampaignModel(ImpressionData impressionData, ClickData clickData, ServerData serverData, String title){
        this.clickData = clickData;
        this.impressionData = impressionData;
        this.serverData = serverData;
        this.title = title;

        this.graphs = new ArrayList<>();
        this.campaignUpdateListeners = new ArrayList<>();
    }

    public void addGraph(GraphModel graph) {
        graphs.add(graph);
        triggerListeners();
    }

    public void removeGraph(GraphModel graph) {
        graphs.remove(graph);
        triggerListeners();
    }

    public ArrayList<CampaignUpdateListener> getCampaignUpdateListeners() {
        return campaignUpdateListeners;
    }

    public ClickData getClickData() {
        return clickData;
    }

    public ImpressionData getImpressionData() {
        return impressionData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<GraphModel> getGraphs() {
        return graphs;
    }

    public void addListener(CampaignUpdateListener listener){
        campaignUpdateListeners.add(listener);
    }

    public void removeListener(CampaignUpdateListener listener){
        campaignUpdateListeners.remove(listener);
    }

    private void triggerListeners() {
        var listeners = new ArrayList<>(campaignUpdateListeners);
        for (var listener : listeners) {
            if (!listener.isAlive()) {
                removeListener(listener);
                continue;
            }
            
            listener.campaignUpdate();
        }
    }
}
