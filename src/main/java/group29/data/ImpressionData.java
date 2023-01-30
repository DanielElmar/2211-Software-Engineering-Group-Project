package group29.data;

import group29.enums.data.*;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImpressionData extends Data {

    static final Logger logger = LogManager.getLogger("Impression Data");

    private Pair<Double, Double> genderDistribution = null;
    private Map<Age,Double> ageDistribution = null;
    private Map<Income, Double> incomeDistribution = null;
    private Map<Context, Double> contextDistribution = null;
    private double highestImpressionCost = -1;
    private double averageImpressionCost = -1;

    private Map<Long, Long> idMap;

    /**
     * data attribute is set in the constructor, empty Data objects can't be created
     *
     * @param data The parsed data to store
     * @param type The type of Data being stored
     */
    public ImpressionData(ArrayList data, DataType type, Map<Long, Long> idMap) {
        super(data, type);
        this.idMap = idMap;
    }

    protected void clearCachedValues() {
        super.clearCachedValues();
        genderDistribution = null;
        ageDistribution = null;
        incomeDistribution = null;
        contextDistribution = null;
        highestImpressionCost = -1;
        averageImpressionCost = -1;
    }

    public Map<Long, Long> getIdMap() {
        return idMap;
    }

    public long getTimeSpanInMillies() {
        if (timeSpanInMillies == -1){
            timeSpanInMillies = Math.abs(getLatestDate() - getEarliestDate()) * 1000;
        }
        return timeSpanInMillies;
    }

    public Pair<Double, Double> getGenderDistribution() {
        if ( genderDistribution == null ){
            double maleCount = 0D;
            double femaleCount = 0D;

            for ( Object row : getData()) {
                if ( ((ImpressionDataRow) row).gender == Gender.MALE ){ maleCount++; }
                else { femaleCount++; }
            }
            genderDistribution = new Pair<>( (maleCount / getNumOfRows()), (femaleCount / getNumOfRows()));
        }
        return genderDistribution;
    }

    public Map<Age, Double> getAgeDistribution() {
        if ( ageDistribution == null){
            double under25Count = 0;
            double ages25to34Count = 0;
            double ages35to44Count = 0;
            double ages45to54Count = 0;
            double over54Count = 0;

            for ( Object row: getData()) {
                switch ( ((ImpressionDataRow) row).age ){
                    case UNDER_25 -> under25Count++;
                    case AGES_25_TO_34 -> ages25to34Count++;
                    case AGES_35_TO_44 -> ages35to44Count++;
                    case AGES_45_TO_54 -> ages45to54Count++;
                    case OVER_54 -> over54Count++;
                }
            }

            float total = getNumOfRows();
            ageDistribution = new HashMap<>();

            ageDistribution.put(Age.UNDER_25, (under25Count / total) );
            ageDistribution.put(Age.AGES_25_TO_34,(ages25to34Count / total) );
            ageDistribution.put(Age.AGES_35_TO_44, (ages35to44Count / total ) );
            ageDistribution.put(Age.AGES_45_TO_54, (ages45to54Count / total ) );
            ageDistribution.put(Age.OVER_54, (over54Count / total ) );
        }
        return ageDistribution;
    }

    public Map<Income, Double> getIncomeDistribution() {
        if ( incomeDistribution == null){
            double lowCount = 0;
            double mediumCount = 0;
            double highCount = 0;

            for ( Object row: getData()) {
                switch ( ((ImpressionDataRow) row).income ){
                    case LOW -> lowCount++;
                    case MEDIUM -> mediumCount++;
                    case HIGH -> highCount++;
                }
            }

            double total = getNumOfRows();
            incomeDistribution = new HashMap<>();

            incomeDistribution.put(Income.LOW, (lowCount / total) );
            incomeDistribution.put(Income.MEDIUM,(mediumCount / total) );
            incomeDistribution.put(Income.HIGH, (highCount / total ) );

        }
        return incomeDistribution;
    }

    public Map<Context, Double> getContextDistribution() {
        if ( contextDistribution == null){
            double blogCount = 0;
            double newsCount = 0;
            double shoppingCount = 0;
            double socialMediaCount = 0;
            double hobbiesMediaCount = 0;
            double travelMediaCount = 0;

            for ( Object row: getData()) {
                switch ( ((ImpressionDataRow) row).context ){
                    case BLOG -> blogCount++;
                    case NEWS -> newsCount++;
                    case SHOPPING -> shoppingCount++;
                    case SOCIAL_MEDIA -> socialMediaCount++;
                    case HOBBIES -> hobbiesMediaCount++;
                    case TRAVEL -> travelMediaCount++;
                }
            }

            double total = getNumOfRows();
            contextDistribution = new HashMap<>();

            contextDistribution.put(Context.BLOG, (blogCount / total) );
            contextDistribution.put(Context.NEWS,(newsCount / total) );
            contextDistribution.put(Context.SHOPPING, (shoppingCount / total ) );
            contextDistribution.put(Context.SOCIAL_MEDIA, (socialMediaCount / total ) );
            contextDistribution.put(Context.HOBBIES, (hobbiesMediaCount / total ) );
            contextDistribution.put(Context.TRAVEL, (travelMediaCount / total ) );
        }
        return contextDistribution;
    }

    public double getHighestImpressionCost() {
        if ( highestImpressionCost == -1 ){
            double currentHighestImpressionCost = 0;

            for ( Object row: getData()) {
                double impressionCost = ((ImpressionDataRow) row).impressionCost;
                if ( impressionCost > currentHighestImpressionCost){ currentHighestImpressionCost = impressionCost; }
            }
            highestImpressionCost = currentHighestImpressionCost;
        }
        return highestImpressionCost;
    }

    public double getAverageImpressionCost() {
        if ( averageImpressionCost == -1 ){
            double impressionCostSum = 0;

            for ( Object row: getData()) {
                impressionCostSum += ((ImpressionDataRow) row).impressionCost;
            }
            averageImpressionCost = (impressionCostSum / getNumOfRows());
        }
        return averageImpressionCost;
    }

    @Override
    public void addRow(Object row) {
        super.addRow(row);
        clearCachedValues();
    }
}
