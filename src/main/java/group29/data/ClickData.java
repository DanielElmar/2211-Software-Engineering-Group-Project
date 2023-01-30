package group29.data;

import group29.enums.data.DataType;

import java.util.ArrayList;
import java.util.Date;

public class ClickData extends Data {

    private double lowestClickCost = -1;
    private double highestClickCost = -1;
    private double averageClickCost = -1;


    /**
     * data attribute is set in the constructor, empty Data objects cant be created
     *
     * @param data The parsed data to store
     * @param type The type of Data to store
     */
    public ClickData(ArrayList data, DataType type) {
        super(data, type);
    }

    protected void clearCachedValues(){
        super.clearCachedValues();
        lowestClickCost = -1;
        highestClickCost = -1;
        averageClickCost = -1;
    }

    public double getLowestClickCost() {
        if ( lowestClickCost == -1 ){
            double newLowestClickCost = ((ClickDataRow) getData().get(0)).clickCost;
            for ( Object row :  getData()) {
                if (newLowestClickCost > (((ClickDataRow) row).clickCost)){ newLowestClickCost = ((ClickDataRow) row).clickCost; }
            }
            lowestClickCost = newLowestClickCost;
        }
        return lowestClickCost;
    }

    public double getHighestClickCost() {
        if (highestClickCost == -1){
            double newHighestClickCost = ((ClickDataRow) getData().get(0)).clickCost;
            for ( Object row : getData()) {
                if (newHighestClickCost < (((ClickDataRow) row).clickCost)){ newHighestClickCost = ((ClickDataRow) row).clickCost; }
            }
            highestClickCost = newHighestClickCost;
        }
        return highestClickCost;
    }

    public double getAverageClickCost() {
        if ( averageClickCost == -1 ){
            double clickCostSum = 0;
            for ( Object row : getData()) {
                clickCostSum += ((ClickDataRow) row).clickCost;
            }
            averageClickCost = clickCostSum / getNumOfRows();
        }
        return averageClickCost;
    }

    @Override
    public void addRow(Object row) {
        super.addRow(row);
        clearCachedValues();
    }
}
