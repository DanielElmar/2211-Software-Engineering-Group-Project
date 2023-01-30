package group29.data;

import group29.enums.data.DataType;

import java.util.ArrayList;
import java.util.Date;

/**
 * Data class stores parsed data  from csv files
 */
public abstract class Data<T>{

    protected long earliestDate = -1;
    protected long latestDate = -1;
    protected long timeSpanInMillies = -1;
    private final ArrayList<T> data;
    private final DataType type;

    protected void clearCachedValues() {
        earliestDate = -1;
        latestDate = -1;
        timeSpanInMillies = -1;
    }

    public int getNumOfRows(){
        return getData().size();
    }

    public long getEarliestDate() {
        if (earliestDate == -1){
            earliestDate = ((DataRow) getData().get(0)).timestamp;
            for (var row : (ArrayList<DataRow>) getData()) {
                if (row.timestamp < earliestDate){ earliestDate = row.timestamp; }
            }
        }

        return earliestDate;
    }

    public long getLatestDate() {
        if (latestDate == -1){
            latestDate = ((DataRow) getData().get(0)).timestamp;
            for (var row : (ArrayList<DataRow>) getData()) {
                if (row.timestamp > latestDate){ latestDate = row.timestamp; }
            }
        }

        return latestDate;
    }

    public long getTimeSpanInMillies() {
        if (timeSpanInMillies == -1){
            timeSpanInMillies = Math.abs(getLatestDate() - getEarliestDate()) * 1000;
        }
        return timeSpanInMillies;
    }

    public ArrayList<T> getData() {
        return data;
    }

    /**
     * Functionality to add a row to a Data Obj after it has been created
     *
     * @param row data to be added to the Obj, must be in the format as the rest of the data to enforce integrity
     */
    public void addRow(T row){
        data.add(row);
    }

    public DataType getType() {
        return type;
    }

    /**
     * data attribute is set in the constructor, empty Data objects cant be created
     * @param data The parsed data to store
     */
    public Data(ArrayList<T> data, DataType type){
        this.data = data;
        this.type = type;
    }
}
