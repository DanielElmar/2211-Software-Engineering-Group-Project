package group29.event;

/**
 * handles the event when another campaign has been added to the 'cache'
 */

public interface CampaignsModelUpdateListener {
    /**
     * handles campaign list being updated
     */
    void campaignsUpdate();
    boolean isAlive();
}
