package group29.model;

import group29.enums.ui.Theme;
import group29.event.SettingsModelUpdateListener;
import group29.exceptions.InvalidParameter;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.Iterator;

public class SettingsModel {

    static final Logger logger = LogManager.getLogger("SettingsModel");
    private int txtSize;
    private int minTextSize;
    private int maxTextSize;

    private final ArrayList<SettingsModelUpdateListener> settingsUpdateListeners;
    private Theme currentTheme = Theme.WHITE;
    private String theme;

    public SettingsModel(){
        this.txtSize = 16;
        this.minTextSize = 9;
        this.maxTextSize = 26;

        this.settingsUpdateListeners = new ArrayList<>();
        setTheme(currentTheme);
    }

    public Theme getTheme() {
        return currentTheme;
    }

    public void setTheme(Theme themeName) {
        currentTheme = themeName;
        switch (themeName) {
            case WHITE -> theme = "bg: #F0F1F3;"
                                + "shadow: #E0E2E7;"
                                + "shadow1: #E0E2E6;"
                                + "light: #ffffff;"
                                + "light1: #fffffe;"
                                + "bg-accent: #D0D4DB;"
                                + "hover: #F8F8F9;"
                                + "content-color: #222222;"
                                + "content-color2: #ffffff;"
                                + "btn-inactive: #E8EAED";

            case DARK -> theme = "bg: #222222;"
                                + "shadow: #1E1E1E;"
                                + "shadow1: #1E1E1D;"
                                + "light: #28292A;"
                                + "light1: #28292B;"
                                + "bg-accent: #2E2F32;"
                                + "hover: #34363A;"
                                + "content-color: #ffffff;"
                                + "content-color2: #222222;"
                                + "btn-inactive: #28292A";
        }

        triggerListeners();
    }

    public String getThemeStyle() {
        return theme;
    }

    public int getTxtSize() {
        return txtSize;
    }

    public void setTxtSize(int txtSize) {
        if ( txtSize >= minTextSize && txtSize <= maxTextSize ) {
            this.txtSize = txtSize;
            triggerListeners();
        }
    }

    public void setMaxTextSize(int maxTextSize) throws InvalidParameter {
        if (minTextSize > maxTextSize){
            throw new InvalidParameter();
        } else if ( txtSize > maxTextSize){
            txtSize = minTextSize;
        }
        this.maxTextSize = maxTextSize;
        triggerListeners();
    }

    public void setMinTextSize(int minTextSize) throws InvalidParameter {
        if (minTextSize > maxTextSize || minTextSize < 1){
            throw new InvalidParameter();
        } else if ( txtSize < minTextSize){
            txtSize = minTextSize;
        }
        this.minTextSize = minTextSize;
        triggerListeners();
    }

    public int getMaxTextSize() {
        return maxTextSize;
    }

    public int getMinTextSize() {
        return minTextSize;
    }

    public void addListener(SettingsModelUpdateListener listener){
        settingsUpdateListeners.add(listener);
    }

    public void removeListener(SettingsModelUpdateListener listener){
        settingsUpdateListeners.remove(listener);
    }

    private void triggerListeners() {
        // We are making a copy to avoid a concurrent modification exception which happens for some reason
        var listeners = new ArrayList<>(settingsUpdateListeners);
        for (var listener : listeners) {
            if (!listener.isAlive()) {
                removeListener(listener);
                continue;
            }
            
            listener.settingsUpdate();
        }
    }
}
