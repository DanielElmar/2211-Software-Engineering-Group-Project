package group29.data;

import group29.enums.data.Age;
import group29.enums.data.Context;
import group29.enums.data.Gender;
import group29.enums.data.Income;

import java.util.Date;


public class ImpressionDataRow extends DataRow {
    
    public final Gender gender;
    public final Age age;
    public final Income income;
    public final Context context;
    public final float impressionCost;

    /**
     * Object used to store the data of a single row in the format of the Impression Logs
     *
     * @param date
     * @param id
     * @param gender
     * @param age
     * @param income
     * @param context
     * @param impressionCost
     */
    public ImpressionDataRow(long timestamp, long id, Gender gender, Age age, Income income, Context context, float impressionCost){
        this.timestamp = timestamp;

        this.age = age;
        this.context = context;
        this.gender = gender;
        this.id = id;
        this.impressionCost = impressionCost;
        this.income = income;
    }
}
