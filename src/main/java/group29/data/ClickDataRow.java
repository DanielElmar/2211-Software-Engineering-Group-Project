package group29.data;

import java.util.Date;

public class ClickDataRow extends DataRow{
    
    public final double clickCost;

    /**
     * Object used to store the data of a single row in the format of the Click Logs
     *
     * @param date
     * @param id
     * @param clickCost
     */
    public ClickDataRow(long timestamp, long id, double clickCost){
        this.timestamp = timestamp;
        this.id = id;
        this.clickCost = clickCost;
    }
}
