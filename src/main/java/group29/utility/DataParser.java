package group29.utility;

import group29.data.*;
import group29.enums.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * Class to read a csv file and create the corresponding Data obj
 */
public class DataParser {

    private static final Logger logger = LogManager.getLogger("DataParser");
    private static final String[] clickLogHeader = new String[]{"Date", "ID", "Click cost"};
    private static final String[] impressionLogHeader = new String[]{"Date", "ID", "Gender", "Age", "Income", "Context", "Impression Cost"};
    private static final String[] serverLogHeader = new String[]{"Entry Date", "ID", "Exit Date", "Pages Viewed", "Conversion"};

    /**
     * reads a given csv file parses it and creates the correct corresponding Data obj
     *
     * @param inputStream input stream of the csv file
     */

    public static long fastDateParser(String dateString) {
        return Timestamp.valueOf(dateString).getTime() / 1000;
    }

    public static Data readFile(InputStream inputStream) throws Exception {
        Data dataObj = null;
        DataType currentFileType;
        //SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        // read the header of the csv file
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String headerLine = br.readLine();

        if (headerLine == null) {
            throw new Exception("File is empty");
        }

        // check the header of the csv file to see if it corresponds to an expected file type
        
        if (headerIs(headerLine, clickLogHeader)) currentFileType = DataType.CLICK_LOG;
        else if (headerIs(headerLine, impressionLogHeader)) currentFileType = DataType.IMPRESSION_LOG;
        else if (headerIs(headerLine, serverLogHeader)) currentFileType = DataType.SERVER_LOG;
        else throw new Exception("Given File header is not in a recognized format");

        ArrayList<DataRow> parsedData = new ArrayList<>();
        String line = br.readLine();

        long lineIndex = 0;
        Map<Long, Long> idMap = new HashMap<>();

        String[] splitLine = null;
        while (line != null) {
            splitLine = line.split(",");
            
            try {
                switch (currentFileType) {
                    case CLICK_LOG -> {
                        long date = fastDateParser(splitLine[0]);
                        long id = Long.parseLong(splitLine[1]);
                        double clickCost = Double.parseDouble(splitLine[2]);
                        ClickDataRow row = new ClickDataRow(date, id, clickCost);
                        parsedData.add(row);
                    }

                    case SERVER_LOG -> {
                        long entryDate = fastDateParser(splitLine[0]);
                        long id = Long.parseLong(splitLine[1]);
                        long exitDate;
                        Conversion conversion;
                        int pagesViewed = Integer.parseInt(splitLine[3]);

                        try {
                            exitDate = fastDateParser(splitLine[2]);
                        } catch ( Exception e) {

                            if (splitLine[2].equals("n/a")) {
                                exitDate = 0;
                            } else {
                                throw new Exception("Unexpected Data in Server Log, Exit Date column: " + splitLine[2]);
                            }
                        }

                        switch (splitLine[4]) {
                            case "Yes" -> conversion = Conversion.YES;
                            case "No" -> conversion = Conversion.NO;
                            default -> throw new Exception("Unexpected Data in Server Log, Conversion column: " + splitLine[4]);
                        }

                        ServerDataRow row = new ServerDataRow(entryDate, id, exitDate, pagesViewed, conversion);
                        parsedData.add(row);
                    }

                    case IMPRESSION_LOG -> {
                        long date = fastDateParser(splitLine[0]);
                        long id = Long.parseLong(splitLine[1]);
                        float impressionCost = Float.valueOf(splitLine[6]);
                        Gender gender;
                        Age age;
                        Income income;
                        Context context;

                        switch (splitLine[2]) {
                            case "Male" -> gender = Gender.MALE;
                            case "Female" -> gender = Gender.FEMALE;
                            default -> throw new Exception("Unexpected Data in Impression Log, Gender column: " + splitLine[2]);
                        }
                        switch (splitLine[3]) {
                            case "<25" -> age = Age.UNDER_25;
                            case "25-34" -> age = Age.AGES_25_TO_34;
                            case "35-44" -> age = Age.AGES_35_TO_44;
                            case "45-54" -> age = Age.AGES_45_TO_54;
                            case ">54" -> age = Age.OVER_54;
                            default -> throw new Exception("Unexpected Data in Impression Log, Age column: " + splitLine[3]);
                        }
                        switch (splitLine[4]) {
                            case "Low" -> income = Income.LOW;
                            case "Medium" -> income = Income.MEDIUM;
                            case "High" -> income = Income.HIGH;
                            default -> throw new Exception("Unexpected Data in Impression Log, Income column: " + splitLine[4]);
                        }
                        switch (splitLine[5]) {
                            case "Blog" -> context = Context.BLOG;
                            case "News" -> context = Context.NEWS;
                            case "Shopping" -> context = Context.SHOPPING;
                            case "Social Media" -> context = Context.SOCIAL_MEDIA;
                            case "Travel" -> context = Context.TRAVEL;
                            case "Hobbies" -> context = Context.HOBBIES;
                            default -> throw new Exception("Unexpected Data in Impression Log, Context column: " + splitLine[5]);
                        }

                        idMap.put(id, lineIndex);
                        ImpressionDataRow row = new ImpressionDataRow(date, id, gender, age, income, context, impressionCost);
                        parsedData.add(row);
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected line '" + line + "' in file of type " + currentFileType);
            }

            line = br.readLine();
            lineIndex += 1;
        }

        switch (currentFileType) {
            case IMPRESSION_LOG -> dataObj = new ImpressionData(parsedData, DataType.IMPRESSION_LOG, idMap);
            case SERVER_LOG -> dataObj = new ServerData(parsedData, DataType.SERVER_LOG);
            case CLICK_LOG -> dataObj = new ClickData(parsedData, DataType.CLICK_LOG);
        }

        return dataObj;
    }

    private static boolean headerIs(String headerLine, String[] validHeader) {
        var splitHeader = headerLine.split(",");

        if (splitHeader.length != validHeader.length) {
            return false;
        }

        for (int i = 0; i < validHeader.length; i++) {
            if (!validHeader[i].toLowerCase().equals(splitHeader[i].toLowerCase().strip())) {
                return false;        
            }
        }

        return true;
    }
}