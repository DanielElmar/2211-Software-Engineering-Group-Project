package group29.model;

import org.apache.logging.log4j.core.jmx.Server;

import group29.data.ClickDataRow;
import group29.data.DataRow;
import group29.data.ImpressionDataRow;
import group29.data.ServerDataRow;
import group29.enums.data.*;
import group29.enums.graph.FilterType;

public class FilterModel {
    
    private final boolean isNegated;
    private final FilterType filterType;

    // Awful!
    private Age age;
    private Context context;
    private Conversion conversion;
    private Gender gender;
    private Income income;

    public FilterModel(Age age, boolean isNegated) {
        this.filterType = FilterType.AGE;
        this.age = age;
        this.isNegated = isNegated;
    }

    public FilterModel(Context context, boolean isNegated) {
        this.filterType = FilterType.CONTEXT;
        this.context = context;
        this.isNegated = isNegated;
    }

    public FilterModel(Conversion conversion, boolean isNegated) {
        this.filterType = FilterType.CONVERSION;
        this.conversion = conversion;
        this.isNegated = isNegated;
    }

    public FilterModel(Gender gender, boolean isNegated) {
        this.filterType = FilterType.GENDER;
        this.gender = gender;
        this.isNegated = isNegated;
    }

    public FilterModel(Income income, boolean isNegated) {
        this.filterType = FilterType.INCOME;
        this.income = income;
        this.isNegated = isNegated;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public Age getAge() {
        return age;
    }

    public Context getContext() {
        return context;
    }

    public Conversion getConversion() {
        return conversion;
    }

    public Gender getGender() {
        return gender;
    }

    public Income getIncome() {
        return income;
    }

    public boolean isAccepted(DataRow row, ImpressionDataRow idRow) {
        boolean isAccepted = true;

        switch (filterType) {
            case AGE -> isAccepted = (idRow.age == age);
            case CONTEXT -> isAccepted = (idRow.context == context);
            case GENDER -> isAccepted = (idRow.gender == gender);
            case INCOME -> isAccepted = (idRow.income == income);
        }

        if (row instanceof ServerDataRow && filterType == FilterType.CONVERSION) {
            isAccepted = (((ServerDataRow) row).conversion == conversion);
        }

        if (isNegated) {
            isAccepted = !isAccepted;
        }

        return isAccepted;
    }
}
