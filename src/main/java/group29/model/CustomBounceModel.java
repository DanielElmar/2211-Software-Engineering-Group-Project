package group29.model;

import group29.data.ServerDataRow;
import javafx.beans.property.SimpleIntegerProperty;

public class CustomBounceModel {
    
    private int pageCount = 5;
    private int timeSpent = 0;

    private boolean usingPC = true;
    private boolean usingTS = false;

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public boolean isUsingPageCount() {
        return usingPC;
    }

    public boolean isUsingTimeSpent() {
        return usingTS;
    }

    public void setUsingPageCount(boolean value) {
        usingPC = value;
    }

    public void setUsingTimeSpent(boolean value) {
        usingTS = value;
    }

    public boolean isBounced(ServerDataRow row) {
        if (isUsingPageCount() && row.pagesViewed < getPageCount()) {
            return true;
        }

        if (isUsingTimeSpent() && row.exitDate > 0) {
            if (row.exitDate - row.timestamp < getTimeSpent()) {
                return true;
            }
        }

        return false;
    }
}

