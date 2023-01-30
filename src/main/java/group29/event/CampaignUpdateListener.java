package group29.event;

/**
 * Used to handle the event in which a campaign is told to update, and passes the campaign that was told such
 */
public interface CampaignUpdateListener {
    /**
     * handles campaigns being updated
     */
    void campaignUpdate();
    boolean isAlive();

}
