package group29.model;

public class ModelManager {
    private final CampaignsModel campaignsModel;
    private final SettingsModel settingsModel;

    public ModelManager(CampaignsModel campaignsModel, SettingsModel settingsModel) {
        this.campaignsModel = campaignsModel;
        this.settingsModel = settingsModel;
    }

    public CampaignsModel getCampaignsModel() {
        return campaignsModel;
    }

    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

}
