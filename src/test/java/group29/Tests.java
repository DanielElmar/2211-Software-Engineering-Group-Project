package group29;
 
import group29.data.*;
import group29.enums.data.*;
import group29.enums.graph.LineType;
import group29.event.CampaignUpdateListener;
import group29.event.CampaignsModelUpdateListener;
import group29.model.*;
import group29.utility.DataParser;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import group29.exceptions.InvalidParameter;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    static final Logger logger = LogManager.getLogger("Tests");
    static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    static final DecimalFormat df = new DecimalFormat("#.####");
    
    

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Before everything");
        df.setRoundingMode(RoundingMode.FLOOR);
    }

    @Test
    public void testReturn5() {
        var app = new App();
        assertEquals(app.testReturn5(), 5, "testReturn5 does not return 5");
    }

    @Test
    @Disabled
    public void supposedToFail() {
        var app = new App();
        assertEquals(app.testReturn5(), 6, "this returned 5 instead of 6! It's supposed to fail.");
    }

    @Test
    public void clickLogFileParseObjCreated() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/click_log.csv");
        assertNotNull(DataParser.readFile(resource), "Data obj failed to be created from ClickLog");
    }

    @Test
    public void clickLogFileParseRowClickCostCorrect()throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/click_log.csv");
        assertEquals( 11.794442, ((ClickDataRow) DataParser.readFile(resource).getData().get(0)).clickCost, "Expected Data in 3rd col is incorrect");

    }

    @Test
    public void clickLogFileParseRowIdCorrect()throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/click_log.csv");
        long id = Long.parseLong("8895519749317550080");

        assertEquals( id, ( (ClickDataRow) DataParser.readFile(resource).getData().get(0)).id, "Expected Data in 2nd col is incorrect");
    }

    @Test
    public void clickLogFileParseRowDateCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/click_log.csv");
        assertEquals( Timestamp.valueOf("2015-01-01 12:01:21").getTime() / 1000, ( (ClickDataRow) DataParser.readFile(resource).getData().get(0)).timestamp, "Expected Data in 1st col is incorrect");
    }

    @Test
    public void clickLogFileParseInvalidFilePath()  {
        // TODO - DataParser no longer handles resolving filename to a file
        //assertNull(DataParser.readFile(new FileInputStream("Downloads/Amongus")));
    }

    @Test
    public void serverLogFileParseObjCreated() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/server_log.csv");
        assertNotNull(DataParser.readFile(resource), "Data obj failed to be created from ClickLog");
    }

    @Test
    public void serverLogFileParseRowEntryDateCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/server_log.csv");
        assertEquals(Timestamp.valueOf("2015-01-01 12:01:21").getTime() / 1000, ((ServerDataRow) DataParser.readFile(resource).getData().get(0)).timestamp, "Expected Data in 1st col is incorrect");

    }

    @Test
    public void serverLogFileParseRowIdCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/server_log.csv");
        long id = Long.parseLong("8895519749317550080");

        assertEquals( id, ( (ServerDataRow) DataParser.readFile(resource).getData().get(0)).id, "Expected Data in 2nd col is incorrect");
    }

    @Test
    public void serverLogFileParseRowExitDateCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/server_log.csv");
        assertEquals(formatter.parse("2015-01-01 12:05:13").getTime() / 1000, ( (ServerDataRow) DataParser.readFile(resource).getData().get(0)).exitDate, "Expected Data in 3rd col is incorrect");
    }

    @Test
    public void serverLogFileParseRowPagesViewedCorrect() throws Exception{

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/server_log.csv");
        assertEquals( 7, ( (ServerDataRow) DataParser.readFile(resource).getData().get(0)).pagesViewed, "Expected Data in 4th col is incorrect");
    }

    @Test
    public void serverLogFileParseRowConversionCorrect() throws Exception{

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/server_log.csv");
        assertEquals( Conversion.NO, ( (ServerDataRow) DataParser.readFile(resource).getData().get(0)).conversion, "Expected Data in 5th col is incorrect");
    }

    @Test
    public void serverLogFileParseInvalidFilePath()  {
        //assertNull(DataParser.readFile("Downloads/Amongus"));
    }

    @Test
    public void impressionLogFileParseObjCreated() throws Exception{

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/impression_log.csv");
        assertNotNull(DataParser.readFile(resource), "Data obj failed to be created from ClickLog");
    }

    @Test
    public void impressionLogFileParseRowDateCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/impression_log.csv");
        assertEquals(Timestamp.valueOf("2015-01-01 12:00:02").getTime() / 1000, ((ImpressionDataRow) DataParser.readFile(resource).getData().get(0)).timestamp, "Expected Data in 1st col is incorrect");

    }

    @Test
    public void impressionLogFileParseRowIdCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/impression_log.csv");
        long id = Long.parseLong("4620864431353617408");

        assertEquals( id, ( (ImpressionDataRow) DataParser.readFile(resource).getData().get(0)).id, "Expected Data in 2nd col is incorrect");
    }

    @Test
    public void impressionLogFileParseRowGenderCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/impression_log.csv");
        assertEquals( Gender.MALE , ( (ImpressionDataRow) DataParser.readFile(resource).getData().get(0)).gender, "Expected Data in 3rd col is incorrect");
    }

    @Test
    public void impressionLogFileParseRowAgeCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/impression_log.csv");
        assertEquals(Age.AGES_25_TO_34, ( (ImpressionDataRow) DataParser.readFile(resource).getData().get(0)).age, "Expected Data in 4th col is incorrect");
    }

    @Test
    public void impressionLogFileParseRowIncomeCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/impression_log.csv");
        assertEquals(Income.HIGH, ( (ImpressionDataRow) DataParser.readFile(resource).getData().get(0)).income, "Expected Data in 5th col is incorrect");
    }

    @Test
    public void impressionLogFileParseRowContextCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/impression_log.csv");
        assertEquals(Context.BLOG, ( (ImpressionDataRow) DataParser.readFile(resource).getData().get(0)).context, "Expected Data in 6th col is incorrect");
    }

    @Test
    public void impressionLogFileParseRowImpressionCostCorrect() throws Exception {

        var resource = Tests.class.getResourceAsStream("/2_week_campaign_2/impression_log.csv");
        assertEquals( 0.001713, (double) Math.round( ((ImpressionDataRow) DataParser.readFile(resource).getData().get(0)).impressionCost * 1000000) / 1000000, "Expected Data in 7th col is incorrect");
    }

    @Test
    public void impressionLogFileParseInvalidFilePath()  {
        //assertNull(DataParser.readFile("Downloads/Amongus"));
    }

    @Test
    public void expectExceptionExample() {

        Exception exception = assertThrows(Exception.class, () -> Integer.parseInt("1a"));

        String expectedMessage = "For input string";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    // TODO add tests for themes
    @Test
    public void settingsModelDefaultValues(){

        SettingsModel sm = new SettingsModel();

        assertEquals(sm.getTxtSize(), 16, "Settings Model Has Wrong text size");
        assertEquals(sm.getMaxTextSize(), 26, "Settings Model Has Wrong default max text size");
        assertEquals(sm.getMinTextSize(), 9, "Settings Model Has Wrong default min text size");
    }

    @Test
    public void settingsModelSetterNormalValues(){

        SettingsModel sm = new SettingsModel();

        sm.setTxtSize( 26 );
        try {
            sm.setMaxTextSize( 100 );
            sm.setMinTextSize( 5 );
        } catch (InvalidParameter ignored) { }

        assertEquals(sm.getTxtSize(), 26, "Settings Model Setter for text size wrong");
        assertEquals(sm.getMaxTextSize(), 100, "Settings Model Setter for max text size wrong");
        assertEquals(sm.getMinTextSize(), 5, "Settings Model Setter for default min text size ");
    }

    @Test
    public void settingsModelSetterErroneousValues(){

        SettingsModel sm = new SettingsModel();

        sm.setTxtSize( 15 );
        try {
            sm.setMaxTextSize( 20);
            sm.setMinTextSize( 10 );
        } catch (InvalidParameter ignored) {}


        assertThrows( InvalidParameter.class ,  () -> sm.setMinTextSize( 25 ), "Settings Model Setter min text size error");
        assertEquals(sm.getMinTextSize(), 10, "Settings Model Setter min text size error");

        assertThrows( InvalidParameter.class ,  () -> sm.setMaxTextSize( 5 ), "Settings Model Setter max text size error");
        assertEquals(sm.getMaxTextSize(), 20, "Settings Model Setter max text size error");

        sm.setTxtSize(50);
        assertEquals(sm.getTxtSize(), 15, "Settings Model Setter text size error");

        sm.setTxtSize(0);
        assertEquals(sm.getTxtSize(), 15, "Settings Model Setter text size error");


        assertThrows( InvalidParameter.class ,  () -> sm.setMinTextSize( 0 ), "Settings Model Setter text size error");
    }

    @Test
    public void campaignsModelSetterMethods() throws Exception {
        var cd = (ClickData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/click_log_500.csv"));
        var id = (ImpressionData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/impression_log_500.csv"));
        var sd = (ServerData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/server_log_500.csv"));
        var c1 = new CampaignModel(id, cd, sd,"TestCampaign1");
        var c2 = new CampaignModel(id, cd, sd,"TestCampaign2");
        var c3 = new CampaignModel(id, cd, sd,"TestCampaign3");

        CampaignsModel cm = new CampaignsModel();

        assertEquals(cm.getCampaigns().size(), 0 , "CampaignsModel default Campaigns Array error");

        cm.addCampaign(c1);
        cm.addCampaign(c2);
        cm.addCampaign(c3);

        ArrayList<CampaignModel> cmList = new ArrayList<>( Arrays.asList(c1, c2, c3) );

        for (int i = 0; i < cmList.size(); i++) {
            assertEquals(cm.getCampaigns().get(i), cmList.get(i) , "CampaignsModel get Campaigns error");
        }

        var listener = new CampaignsModelUpdateListener(){
            @Override
            public void campaignsUpdate() {

            }

            public boolean isAlive() {
                return true;
            }
        };

        assertEquals(cm.getCampaignsModelUpdateListeners().size() , 0, "CampaignsModel get Campaigns error");
        cm.addListener( listener );
        assertEquals(cm.getCampaignsModelUpdateListeners().get(0) , listener, "CampaignsModel get Campaigns error");
        cm.removeListener( listener );
        assertEquals(cm.getCampaignsModelUpdateListeners().size() , 0, "CampaignsModel get Campaigns error");
    }

    @Test
    public void campaignModelGetters() throws Exception {
        var cd = (ClickData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/click_log_500.csv"));
        var id = (ImpressionData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/impression_log_500.csv"));
        var sd = (ServerData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/server_log_500.csv"));
        var c1 = new CampaignModel(id, cd, sd,"TestCampaign1");

        assertEquals(c1.getTitle(), "TestCampaign1", "CampaignModel getter error");
        assertEquals(c1.getClickData(), cd, "CampaignModel getter error");
        assertEquals(c1.getImpressionData(), id, "CampaignModel getter error");
        assertEquals(c1.getServerData(), sd, "CampaignModel getter error");
        assertEquals(c1.getGraphs().size(), 0, "CampaignModel getter error");
        assertEquals(c1.getCampaignUpdateListeners().size(), 0, "CampaignModel getter error");
    }

    @Test
    public void campaignModelSetters() throws Exception {
        var cd = (ClickData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/click_log_500.csv"));
        var id = (ImpressionData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/impression_log_500.csv"));
        var sd = (ServerData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/server_log_500.csv"));
        var c1 = new CampaignModel(id, cd, sd,"TestCampaign1");

        var listener = new CampaignUpdateListener() {
            @Override
            public void campaignUpdate() {

            }

            public boolean isAlive() {
                return true;
            }
        };

        c1.addListener(listener);

        assertEquals(c1.getCampaignUpdateListeners().get(0), listener, "CampaignModel setter error");
        c1.removeListener(listener);
        assertEquals(c1.getCampaignUpdateListeners().size(), 0, "CampaignModel getter error");


        var graph = new GraphModel(c1);

        assertEquals(c1.getGraphs().size(), 0, "CampaignModel setter error");
        c1.addGraph( graph );
        assertEquals(c1.getGraphs().get(0), graph, "CampaignModel setter error");
        c1.removeGraph( graph );
        assertEquals(c1.getGraphs().size(), 0, "CampaignModel setter error");

    }

    // split me up
    @Test
    public void graphModel() throws Exception {
        var cd = (ClickData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/click_log_500.csv"));
        var id = (ImpressionData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/impression_log_500.csv"));
        var sd = (ServerData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/server_log_500.csv"));
        var cm = new CampaignModel(id, cd, sd,"TestCampaign");

        GraphModel gm = new GraphModel(cm);


        assertEquals(gm.getCampaignModel(), cm, "Graph Model setter error");
        assertEquals(gm.getClickData(), cd , "Graph Model setter error");
        assertEquals(gm.getImpressionData(), id , "Graph Model setter error");
        assertEquals(gm.getServerData(), sd, "Graph Model setter error");


        assertEquals(gm.getActiveLines().size(),0 , "Graph Model setter error");
        gm.addActiveLine(LineType.CPC);
        assertEquals(gm.getActiveLines().get(0), LineType.CPC, "Graph Model setter error");

        gm.setTimeGranularity( 100 );
        assertEquals(gm.getTimeGranularity(), 100, "Graph Model setter error");
        gm.setTimeGranularity( -1 );// Fails on purpose
        // assertTrue(false, "Graph Model setter error");


        Date d1 = null;
        Date d2 = null;
        try {
            d1 = formatter.parse("2015-01-01 12:12:12");

            d2 = formatter.parse("2015-01-01 12:12:13");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        var p1 = new Pair<>(d1, d2);
        var p2 = new Pair<>(d2,d1);


        //assertEquals(gm.getTimeInterval(), 0 , "Graph Model setter error");

        /*gm.setTimeInterval(p1);
        assertEquals(gm.getTimeInterval(), p1 , "Graph Model setter error");
        gm.setTimeInterval(p2);//// ERROR CASE SHOULD FAIL
        assertEquals(gm.getTimeInterval(), p1 , "Graph Model setter error");*/

    }

    @Test
    public void modelManagerGetterMethods(){
        CampaignsModel cm = new CampaignsModel();
        SettingsModel sm = new SettingsModel();
        ModelManager mm = new ModelManager(cm, sm);

        assertEquals(mm.getCampaignsModel(), cm, "Model manager getter error");
        assertEquals(mm.getSettingsModel(), sm, "Model manager getter error");
    }

    @Test
    public void clickDataTestGetterTests() throws Exception {
        var cd = (ClickData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/click_log_500.csv"));

        assertEquals(cd.getData().size(), cd.getNumOfRows(), "Click Data return has incorrect amount of rows in data");
        assertEquals(cd.getAverageClickCost(), 4.931059957915831, "Click Data returns wrong Average Cost");
        try {
            assertEquals(formatter.parse("2015-01-01 12:01:21").getTime() / 1000, cd.getEarliestDate(), "Click Data returns wrong Earliest Date");
            assertEquals(formatter.parse("2015-01-01 16:52:34").getTime() / 1000, cd.getLatestDate(), "Click Data returns wrong Earliest Date");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertEquals(cd.getHighestClickCost(), 15.021847, "Click Data return has incorrect Highest Click Cost");
        assertEquals(cd.getLowestClickCost(), 0.0, "Click Data return has incorrect Lowest Click Cost");
        assertEquals(cd.getTimeSpanInMillies(), 17473000 , "Click Data return has incorrect Time Span");

    }

    @Test
    public void clickDataTestUpdateValues() throws Exception {
        var cd = (ClickData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/click_log_500.csv"));
        ClickDataRow newClickRow1 = null;
        ClickDataRow newClickRow2 = null;

        newClickRow1 = new ClickDataRow(Timestamp.valueOf("2011-01-01 12:01:21").getTime() / 1000,1,20);
        newClickRow2 = new ClickDataRow(Timestamp.valueOf("2025-01-01 12:01:21").getTime() / 1000,2,-1);

        /*2011-01-01 12:01:21,1,20
2025-01-01 12:01:21,2,-1
2015-01-01 12:01:21,8895519749317550080,11.794442
2015-01-01 12:01:33,6487139546184780800,11.718663
2015-01-01 12:02:25,1277480883056123904,0.000000
2015-01-01 12:02:57,3777890599251549184,0.000000
2015-01-01 12:04:02,6202006224593186816,9.340521
2015-01-01 12:04:15,7375754838836782080,9.827456
2015-01-01 12:04:12,1162345557141671936,0.000000
2015-01-01 12:04:11,8370837523317244928,0.000000
2015-01-01 12:04:28,5217170615204436992,0.000000
2015-01-01 12:04:35,9205559084602150912,0.000000
2015-01-01 12:05:51,4033231570324092928,14.570611*/

        for (int i = 0; i < 11; i++) {
            cd.addRow(cd.getData().get(i));
        }
        cd.addRow(newClickRow1);
        cd.addRow(newClickRow2);

        assertEquals(cd.getData().size(), cd.getNumOfRows(), "Click Data return has incorrect amount of rows in data after adding rows to data");
        assertEquals("" + cd.getAverageClickCost(), "4.9547863515625", "Click Data returns wrong Average Cost after adding rows to data");
        try {
            assertEquals(formatter.parse("2011-01-01 12:01:21").getTime() / 1000, cd.getEarliestDate(), "Click Data returns wrong Earliest Date");
            assertEquals(formatter.parse("2025-01-01 12:01:21").getTime() / 1000, cd.getLatestDate(), "Click Data returns wrong Latest Date");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertEquals(cd.getHighestClickCost(), 20.0, "Click Data return has incorrect Highest Click Cost after adding rows to data");
        assertEquals(cd.getLowestClickCost(), (-1.0), "Click Data return has incorrect Lowest Click Cost after adding rows to data");
        assertEquals(cd.getTimeSpanInMillies(), 441849600000L, "Click Data return has incorrect Time Span after adding rows to data");
    }

    @Test
    public void impressionDataTestGetterTests() throws Exception {
        var id = (ImpressionData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/impression_log_500.csv"));

        assertEquals(id.getData().size(), id.getNumOfRows(), "Impressions Data return has incorrect amount of rows in data");
        assertEquals((double) Math.round(id.getAverageImpressionCost() * 1000000) / 1000000, (double) Math.round(0.0010036152304609214d * 1000000) / 1000000, "Impression Data returns wrong Average Impression Cost");
        assertEquals((double) Math.round(id.getHighestImpressionCost() * 1000000) / 1000000, 0.00299, "Impression Data returns wrong Highest Impression Cost");
        try {
            assertEquals(formatter.parse("2015-01-01 12:00:02").getTime() / 1000, id.getEarliestDate(), "Impression Data returns wrong Earliest Date");
            assertEquals(formatter.parse("2015-01-01 12:14:05").getTime() / 1000, id.getLatestDate(), "Impression Data returns wrong Latest Date");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertEquals(id.getTimeSpanInMillies(), 843000 , "Click Data return has incorrect Time Span");

        ArrayList<Age> ages = new ArrayList<>( List.of( Age.UNDER_25, Age.AGES_25_TO_34, Age.AGES_35_TO_44, Age.AGES_45_TO_54, Age.OVER_54 ) );
        ArrayList<Double> agesExpectedValues = new ArrayList<>(List.of(0.19238476, 0.250501, 0.23647295, 0.19839678, 0.12224449));

        ArrayList<Context> contexts = new ArrayList<>( List.of( Context.BLOG, Context.NEWS, Context.SHOPPING, Context.SOCIAL_MEDIA ) );
        ArrayList<Double> contextsExpectedValues = new ArrayList<>(List.of(0.16633266, 0.28256512, 0.28056112, 0.27054108));

        ArrayList<Income> incomes = new ArrayList<>( List.of( Income.LOW, Income.MEDIUM, Income.HIGH ) );
        ArrayList<Double> incomesExpectedValues = new ArrayList<>(List.of(0.3046092, 0.51302605, 0.18236473));

        for (int i = 0; i < 4; i++) {
            assertEquals(df.format(id.getAgeDistribution().get(ages.get(i))), df.format(agesExpectedValues.get(i)), "Impression Data return has incorrect Age Distribution");

            try {
                assertEquals(df.format(id.getIncomeDistribution().get(incomes.get(i))), df.format(incomesExpectedValues.get(i)), "Impression Data return has incorrect Income Distribution");
            }catch (Exception ignored) {}

            try {
                assertEquals(df.format(id.getContextDistribution().get(contexts.get(i))), df.format(contextsExpectedValues.get(i)), "Impression Data return has incorrect Context Distribution");
            }catch (Exception ignored) {}
        }

        assertEquals(df.format(id.getGenderDistribution().getKey()), df.format(0.32064128) , "Impression Data return has incorrect Male Gender Distribution");
        assertEquals(df.format(id.getGenderDistribution().getValue()), df.format(0.6793587) , "Impression Data return has incorrect Female Gender Distribution");

    }

    @Test
    public void impressionDataTestUpdateValues() throws Exception {
        var id = (ImpressionData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/impression_log_500.csv"));

        ImpressionDataRow newImpressionRow1 = null;
        ImpressionDataRow newImpressionRow2 = null;

        newImpressionRow1 = new ImpressionDataRow(Timestamp.valueOf("2011-01-01 12:01:21").getTime() / 1000,1,Gender.FEMALE, Age.AGES_25_TO_34, Income.HIGH, Context.SOCIAL_MEDIA, -1);
        newImpressionRow2 = new ImpressionDataRow(Timestamp.valueOf("2025-01-01 12:01:21").getTime() / 1000,2,Gender.FEMALE, Age.AGES_25_TO_34, Income.HIGH, Context.SOCIAL_MEDIA, 10);

        for (int i = 0; i < 11; i++) {
            id.addRow(id.getData().get(i));
        }
        id.addRow(newImpressionRow1);
        id.addRow(newImpressionRow2);

        /*
2011-01-01 12:01:21,1,Female,25-34,High,Social Media,-1
2025-01-01 12:01:21,2,Female,25-34,High,Social Media,10
2015-01-01 12:00:02,4620864431353617408,Male,25-34,High,Blog,0.001713
2015-01-01 12:00:04,3365479180556158976,Female,35-44,Medium,News,0.002762
2015-01-01 12:00:05,5239785226806161408,Female,>54,Medium,Shopping,0.001632
2015-01-01 12:00:06,8530398223564990464,Female,<25,Medium,Social Media,0.000000
2015-01-01 12:00:10,399593948382193664,Male,<25,Low,Social Media,0.002064
2015-01-01 12:00:11,5694894591373382656,Female,<25,Medium,Shopping,0.000000
2015-01-01 12:00:13,1804784971213607936,Female,>54,Medium,News,0.001954
2015-01-01 12:00:13,4350042328721430528,Female,>54,Low,News,0.002172
2015-01-01 12:00:14,4029628267337302016,Female,25-34,Medium,News,0.000000
2015-01-01 12:00:18,4042695101021739008,Female,45-54,Low,Social Media,0.002265
2015-01-01 12:00:20,526798779215907840,Female,>54,Medium,Shopping,0.002635*/



        assertEquals(id.getData().size(), id.getNumOfRows(), "Impressions Data return has incorrect amount of rows in data");
        assertEquals((double) Math.round(id.getAverageImpressionCost() * 1000000) / 1000000, 0.018590, "Impression Data returns wrong Average Impression Cost");
        assertEquals(id.getHighestImpressionCost(), 10.0, "Impression Data returns wrong Highest Impression Cost");
        try {
            assertEquals(formatter.parse("2011-01-01 12:01:21").getTime() / 1000, id.getEarliestDate(), "Impression Data returns wrong Earliest Date");
            assertEquals(formatter.parse("2025-01-01 12:01:21").getTime() / 1000, id.getLatestDate(), "Impression Data returns wrong Latest Date");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertEquals(id.getTimeSpanInMillies(), 441849600000L , "Impression Data return has incorrect Time Span");

        ArrayList<Age> ages = new ArrayList<>( List.of( Age.UNDER_25, Age.AGES_25_TO_34, Age.AGES_35_TO_44, Age.AGES_45_TO_54, Age.OVER_54 ) );
        ArrayList<Double> agesExpectedValues = new ArrayList<>(List.of(0.19335938, 0.25195312, 0.23242188, 0.1953125, 0.126953125));

        ArrayList<Context> contexts = new ArrayList<>( List.of( Context.BLOG, Context.NEWS, Context.SHOPPING, Context.SOCIAL_MEDIA ) );
        ArrayList<Double> contextsExpectedValues = new ArrayList<>(List.of(0.1640625, 0.28320312, 0.27929688, 0.2734375));

        ArrayList<Income> incomes = new ArrayList<>( List.of( Income.LOW, Income.MEDIUM, Income.HIGH ) );
        ArrayList<Double> incomesExpectedValues = new ArrayList<>(List.of(0.30273438, 0.51367188, 0.18359375));

        for (int i = 0; i < 4; i++) {
            assertEquals(df.format(id.getAgeDistribution().get(ages.get(i))), df.format(agesExpectedValues.get(i)), "Impression Data return has incorrect Age Distribution");

            try {
                assertEquals(df.format(id.getIncomeDistribution().get(incomes.get(i))), df.format(incomesExpectedValues.get(i)), "Impression Data return has incorrect Income Distribution");
            }catch (Exception ignored) {}

            try {
                assertEquals(df.format(id.getContextDistribution().get(contexts.get(i))), df.format(contextsExpectedValues.get(i)), "Impression Data return has incorrect Context Distribution");
            }catch (Exception ignored) {}
        }

        assertEquals(id.getGenderDistribution().getKey(), 0.31640625 , "Impression Data return has incorrect Male Gender Distribution");
        assertEquals(id.getGenderDistribution().getValue(), 0.68359375 , "Impression Data return has incorrect Female Gender Distribution");

    }

    @Test
    public void serverDataTestGetterTests() throws Exception {
        var sd = (ServerData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/server_log_500.csv"));

        assertEquals(sd.getData().size(), sd.getNumOfRows(), "Server Data return has incorrect amount of rows in data");

        assertEquals(sd.getHighestPagesViewed(), 20, "Server Data returns wrong Highest Pages Viewed");
        assertEquals(df.format(sd.getAveragePagesViewed()), df.format(5.0861726), "Server Data returns wrong Average Pages Viewed");

        assertEquals((double) Math.round(sd.getBounceRate() * 100000) / 100000, 0.51503, "Server Data returns wrong Bounce Rate");

        assertEquals(sd.getLongestStayInSeconds(), 1439, "Server Data returns wrong Longest Stay");
        logger.info(sd.getStaySumInSeconds() / (double) sd.getNumOfRows());
        assertEquals(Math.round(sd.getStaySumInSeconds() / (double) sd.getNumOfRows()), 137 , "Server Data return has incorrect Average Stay");

        assertEquals(df.format(sd.getConversionRate()), df.format(0.09819639278557114) , "Server Data return has incorrect Conversation Yes Percentage");
    }

    @Test
    public void serverDataTestUpdateValues() throws Exception {
        var sd = (ServerData) DataParser.readFile(getClass().getResourceAsStream("/tiny_campaign/server_log_500.csv"));


        ServerDataRow newServerRow1 = null;
        ServerDataRow newServerRow2 = null;

        newServerRow1 = new ServerDataRow(Timestamp.valueOf("2011-01-01 12:01:21").getTime(),1,Timestamp.valueOf("2011-11-11 12:01:21").getTime(), 30 , Conversion.YES);
        newServerRow2 = new ServerDataRow(Timestamp.valueOf("2011-01-01 12:01:21").getTime(),2,Timestamp.valueOf("2011-01-01 12:51:21").getTime(), 6 , Conversion.YES);

        for (int i = 0; i < 11; i++) {
            sd.addRow(sd.getData().get(i));
        }
        sd.addRow(newServerRow1);
        sd.addRow(newServerRow2);


        /*
2011-01-01 12:01:21,1,2011-11-11 12:01:21,30,Yes
2011-01-01 12:01:21,2,2011-01-01 12:51:21,6,Yes
2015-01-01 12:01:21,8895519749317550080,2015-01-01 12:05:13,7,No
2015-01-01 12:01:34,6487139546184780800,2015-01-01 12:02:01,1,No
2015-01-01 12:02:26,1277480883056123904,2015-01-01 12:05:19,10,No
2015-01-01 12:02:58,3777890599251549184,2015-01-01 12:06:30,3,No
2015-01-01 12:04:02,6202006224593186816,2015-01-01 12:04:03,1,No
2015-01-01 12:04:16,7375754838836782080,2015-01-01 12:05:48,4,No
2015-01-01 12:04:13,1162345557141671936,2015-01-01 12:05:20,4,Yes
2015-01-01 12:04:13,8370837523317244928,2015-01-01 12:09:50,10,No
2015-01-01 12:04:29,5217170615204436992,2015-01-01 12:07:31,7,No
2015-01-01 12:04:36,9205559084602150912,2015-01-01 12:06:13,15,Yes
2015-01-01 12:05:51,4033231570324092928,n/a,10,No*/


        assertEquals(sd.getData().size(), sd.getNumOfRows(), "Server Data return has incorrect amount of rows in data");

        assertEquals(sd.getHighestPagesViewed(), 30, "Server Data returns wrong Highest Pages Viewed");
        assertEquals(df.format(sd.getAveragePagesViewed()), df.format(5.1679688), "Server Data returns wrong Average Pages Viewed");

        assertEquals(df.format(sd.getBounceRate()), df.format(0.5117), "Server Data returns wrong Bounce Rate");

        assertEquals(sd.getLongestStayInSeconds(), 27129600000L, "Server Data returns wrong Longest Stay");
        logger.info(sd.getStaySumInSeconds() / (double) sd.getNumOfRows());
        assertEquals(Math.round(sd.getStaySumInSeconds() / (double) sd.getNumOfRows()), 52993496 , "Server Data return has incorrect Average Stay");

        assertEquals(df.format(sd.getConversionRate()), df.format(0.103515625) , "Server Data return has incorrect Conversation Yes Percentage");
    }


    @AfterAll
    public static void afterClass() {
        System.out.println("After everything");
    }
}