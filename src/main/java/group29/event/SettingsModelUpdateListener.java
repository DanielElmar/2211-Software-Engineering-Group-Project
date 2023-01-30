package group29.event;

/**
 * Used to handle the event in which the settings model Updates
 */
public interface SettingsModelUpdateListener {

    /**
     * handles a Settings update event
     */
    void settingsUpdate();
    boolean isAlive();
}
