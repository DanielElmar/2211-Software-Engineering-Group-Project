package group29.data;

import group29.enums.data.Conversion;

import java.util.Date;

public class ServerDataRow extends DataRow{

    public final long exitDate;
    public final int pagesViewed;
    public final Conversion conversion;

    /**
     * Object used to store the data of a single row in the format of the Server Logs
     *
     * @param entryDate
     * @param id
     * @param exitDate
     * @param pagesViewed
     * @param conversion
     */
    public ServerDataRow(long timestamp, long id, long exitDate, int pagesViewed, Conversion conversion){
        this.timestamp = timestamp;
        this.conversion = conversion;
        this.exitDate = exitDate;
        this.pagesViewed = pagesViewed;
        this.id = id;
    }
}
