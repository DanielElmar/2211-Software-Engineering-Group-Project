package group29.data;

import group29.enums.data.Conversion;
import group29.enums.data.DataType;
import group29.model.CustomBounceModel;

import java.util.ArrayList;
import java.util.Date;

public class ServerData extends Data{

    private long longestStayInSeconds = -1;
    private long staySumInSeconds = -1;
    private int highestPagesViewed = -1;
    private double averagePagesViewed = -1;
    private int conversationYesCount = -1;

    /**
     * data attribute is set in the constructor, empty Data objects cant be created
     *
     * @param data The parsed data to store
     * @param type the type of Data to store
     */
    public ServerData(ArrayList data, DataType type) {
        super(data, type);
    }

    protected void clearCachedValues(){
        super.clearCachedValues();
        staySumInSeconds = -1;
        highestPagesViewed = -1;
        averagePagesViewed = -1;
        conversationYesCount = -1;
    }

    public long getTimeSpanInMillies() {
        if (timeSpanInMillies == -1){
            timeSpanInMillies = Math.abs(getLatestDate()- getEarliestDate()) * 1000;
        }
        return timeSpanInMillies;
    }

    public long getEarliestDate() {
        if (earliestDate == -1){
            earliestDate = ((ServerDataRow) getData().get(0)).timestamp;
            for (var row : (ArrayList<ServerDataRow>) getData()) {
                if (row.timestamp < earliestDate){ earliestDate = row.timestamp; }
            }
        }

        return earliestDate;
    }

    public long getLatestDate() {
        if (latestDate == -1){
            latestDate = ((ServerDataRow) getData().get(0)).exitDate;
            for ( var row : (ArrayList<ServerDataRow>) getData()) {
                if (row.exitDate > latestDate){ latestDate = row.exitDate; }
            }
        }

        if (latestDate == 0 || latestDate < getEarliestDate()) {
            latestDate = getEarliestDate();
        }
        
        return latestDate;
    }

    public long getLongestStayInSeconds() {
        if ( longestStayInSeconds == -1 ){
            for (var row : (ArrayList<ServerDataRow>) getData()) {
                long duration = row.exitDate - row.timestamp;
                if (row.exitDate > row.timestamp && duration > longestStayInSeconds) {
                    longestStayInSeconds = duration;
                }
            }
        }
        return longestStayInSeconds;
    }

    public long getStaySumInSeconds() {
        if ( staySumInSeconds == -1 ){
            long naExitData = 0;
            long newStaySum = 0;
            for ( Object row: getData()) {
                var serverRow = (ServerDataRow) row;
                if (serverRow.exitDate != naExitData) {
                    newStaySum += Math.abs(serverRow.exitDate - serverRow.timestamp);
                }
            }
            staySumInSeconds = newStaySum;
        }
        return staySumInSeconds;
    }

    public int getHighestPagesViewed() {
        if ( highestPagesViewed == -1 ){
            int currentHighestPagesViewed = 0;

            for ( Object row: getData()) {
                int pagesViewed = ((ServerDataRow) row).pagesViewed;
                if ( pagesViewed > currentHighestPagesViewed){ currentHighestPagesViewed = pagesViewed; }
            }
            highestPagesViewed = currentHighestPagesViewed;
        }
        return highestPagesViewed;
    }

    public double getAveragePagesViewed() {
        if ( averagePagesViewed == -1 ){

            double pagesViewedSum = 0;

            for ( Object row: getData()) {
                pagesViewedSum += ((ServerDataRow) row).pagesViewed;
            }
            averagePagesViewed = pagesViewedSum / (double) getNumOfRows();
        }
        return averagePagesViewed;
    }

    public int getConversionCount() {
        if ( conversationYesCount == -1 ){
            var newConversationYesCount = 0;
            for ( Object row : getData()) {
                if ( ((ServerDataRow) row).conversion == Conversion.YES ){ newConversationYesCount++; }
            }
            conversationYesCount = newConversationYesCount;
        }
        return conversationYesCount;
    }

    public double getConversionRate(){
        return getConversionCount() / (double) getNumOfRows();
    }

    public double getBounceRate() {
        // use default values
        return getBounceRate(new CustomBounceModel());
    }

    public double getBounceRate(CustomBounceModel bounceModel){
        var bounces = 0;

        for (ServerDataRow row : (ArrayList<ServerDataRow>) getData()) {
            if (bounceModel.isBounced(row)) {
                bounces += 1;
            }
        }

        return (double) bounces / getData().size();
    }
    
    @Override
    public void addRow(Object row) {
        super.addRow(row);
        clearCachedValues();
    }
}
