package group29.model;
import group29.event.CampaignsModelUpdateListener;

import java.util.ArrayList;

public class CampaignsModel {
    private final ArrayList<CampaignModel> campaigns;
    private final ArrayList<CampaignsModelUpdateListener> campaignsModelUpdateListeners;


    public CampaignsModel(){
        this.campaigns = new ArrayList<>();
        this.campaignsModelUpdateListeners = new ArrayList<>();
    }

    public void addCampaign(CampaignModel campaign){
        campaigns.add(campaign);
        triggerListeners();
    }

    public ArrayList<CampaignModel> getCampaigns() {
        return campaigns;
    }

    public void addListener(CampaignsModelUpdateListener listener){
        campaignsModelUpdateListeners.add(listener);
    }

    public void removeListener(CampaignsModelUpdateListener listener){
        campaignsModelUpdateListeners.remove(listener);
    }

    public ArrayList<CampaignsModelUpdateListener> getCampaignsModelUpdateListeners() {
        return campaignsModelUpdateListeners;
    }

    private void triggerListeners() {
        var listeners = new ArrayList<>(campaignsModelUpdateListeners);
        for (var listener : listeners) {
            if (!listener.isAlive()) {
                removeListener(listener);
                continue;
            }
            
            listener.campaignsUpdate();
        }
    }
}
