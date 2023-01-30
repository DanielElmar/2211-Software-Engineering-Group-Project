package group29.ui;

import group29.data.ClickDataRow;
import group29.data.DataRow;
import group29.data.ImpressionDataRow;
import group29.data.ServerDataRow;
import group29.enums.data.Conversion;
import group29.enums.graph.FilterAcceptance;
import group29.enums.graph.FilterType;
import group29.event.GraphUpdateListener;
import group29.model.GraphModel;
import group29.model.SettingsModel;
import group29.ui.element.ADButton;
import group29.ui.element.ADLabel;
import group29.ui.popup.GraphPopup;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.StringConverter;
import group29.event.GraphUpdateListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import group29.enums.graph.FilterAcceptance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.*;

public class GraphRenderer extends Renderable implements GraphUpdateListener {
    
    // todo - graph description, follow graph bounce definition
	final Logger logger = LogManager.getLogger(this.getClass());
    private static SettingsModel settingsModel;

    private final GraphModel graphModel;

    private int graphDragOriginalX = 0;
    private int graphDragOriginalY = 0;

    private final HBox graphControlHBox = new HBox();
    
    private StackPane centerPane;
    private boolean isShowingSymbols;

    private final DecimalFormat df = new DecimalFormat("###.######");
    private boolean displayTopButtons = true;
    
    private LineChart<Number, Number> lineChart;

    private long graphDataMinTime = 0;
    private long graphDataMaxTime = 0;

    private int[] filterAcceptance;

    public GraphRenderer(SceneManager sceneManager, GraphModel graphModel) {
        this(sceneManager, graphModel, true);
    }

    public GraphRenderer(SceneManager sceneManager, GraphModel graphModel, boolean displayTopButtons) {
        super(sceneManager);
        displayPane.getStyleClass().add("graph-container");
        logger.info("setting graph renderer");
        graphModel.setGraphRenderer(this);

        this.displayTopButtons = displayTopButtons;
        this.graphModel = graphModel;
        this.filterAcceptance = new int[FilterType.values().length];

        graphDataMinTime = graphModel.getTimeBoundsOfData().getKey();
        graphDataMaxTime = graphModel.getTimeBoundsOfData().getValue();

        renderGraph();
        displayPane.sceneProperty().addListener(ev -> {
            if (displayPane.getScene() != null) {
                graphModel.removeListener(this);
                graphModel.addListener(this);
            }
        });
    }
    
    @Override
    public void graphUpdate() {
        logger.info("update graph " + this);
        renderGraph();
    }

    private StringConverter<Number> getConverterForInterval(Double minTime, Double maxTime) {
        StringConverter<Number> converter = null;

        //String daysOfWeek[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        if (maxTime - minTime <= 60) {
            converter = new StringConverter<>() {
                public Number fromString(String s) { return 0; }
                public String toString(Number x) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date(x.longValue() * 1000));
    
                    return getCalSection(cal, Calendar.SECOND) + "s";
                }
            };
        } else if (maxTime - minTime <= 60 * 60) {
            converter = new StringConverter<>() {
                public Number fromString(String s) { return 0; }
                public String toString(Number x) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date(x.longValue() * 1000));
    
                    return getCalSection(cal, Calendar.MINUTE) + "m "
                            + getCalSection(cal, Calendar.SECOND) + "s";
                }
            };
        } else if (maxTime - minTime <= 60 * 60 * 24) {
            converter = new StringConverter<>() {
                public Number fromString(String s) { return 0; }
                public String toString(Number x) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date(x.longValue() * 1000));
    
                    return getCalSection(cal, Calendar.HOUR_OF_DAY) + "h "
                            + getCalSection(cal, Calendar.MINUTE) + "m";
                }
            };
        } else if (maxTime - minTime <= 60 * 60 * 24 * 5) {
            converter = new StringConverter<>() {
                public Number fromString(String s) { return 0; }
                public String toString(Number x) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date(x.longValue() * 1000));
    
                    //return daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1] + " "
                    return "Day " + getCalSection(cal, Calendar.DAY_OF_MONTH) + ", "
                            + getCalSection(cal, Calendar.HOUR_OF_DAY) + "h";
                }
            };
        } else {
            converter = new StringConverter<>() {
                public Number fromString(String s) { return 0; }
                public String toString(Number x) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date(x.longValue() * 1000));
    
                    return getCalSection(cal, Calendar.DAY_OF_MONTH) + "/"
                            + getCalSection(cal, Calendar.MONTH) + "/"
                            + getCalSection(cal, Calendar.YEAR);
                }
            };
        }

        return converter;
    }

    private String getFullDate(int timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(((long) timestamp) * 1000));

        return getCalSection(cal, Calendar.DAY_OF_MONTH) + "/"
            + getCalSection(cal, Calendar.MONTH) + "/"
            + getCalSection(cal, Calendar.YEAR) + ", "
            + getCalSection(cal, Calendar.HOUR_OF_DAY) + ":"
            + getCalSection(cal, Calendar.MINUTE) + ":"
            + getCalSection(cal, Calendar.SECOND);
    }

    private String getCalSection(Calendar cal, int section) {
        int value = cal.get(section);

        if (section == Calendar.MONTH) {
            value += 1;
        }

        return String.format("%02d", value);
    }

    private String getTimeAxisName(Double minTime, Double maxTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(minTime.longValue() * 1000));

        if (maxTime - minTime <= 60) {
            return "Time (Starting "
                    + getCalSection(cal, Calendar.DAY_OF_MONTH) + "/"
                    + getCalSection(cal, Calendar.MONTH) + "/"
                    + getCalSection(cal, Calendar.YEAR) + " "
                    + getCalSection(cal, Calendar.HOUR_OF_DAY) + "h "
                    + getCalSection(cal, Calendar.MINUTE) + "m"
                    + ")";
        } else if (maxTime - minTime <= 60 * 60) {
            return "Time (Starting "
                    + getCalSection(cal, Calendar.DAY_OF_MONTH) + "/"
                    + getCalSection(cal, Calendar.MONTH) + "/"
                    + getCalSection(cal, Calendar.YEAR) + " "
                    + getCalSection(cal, Calendar.HOUR_OF_DAY) + "h"
                    + ")";
        } else if (maxTime - minTime <= 60 * 60 * 24) {
            return "Time (Starting "
                    + getCalSection(cal, Calendar.DAY_OF_MONTH) + "/"
                    + getCalSection(cal, Calendar.MONTH) + "/"
                    + getCalSection(cal, Calendar.YEAR)
                    + ")";
        } else if (maxTime - minTime <= 60 * 60 * 24 * 5) {
            return "Time (Starting "
                + getCalSection(cal, Calendar.MONTH) + "/"
                + getCalSection(cal, Calendar.YEAR)
                + ")";
        } else {
            return "Time";
        }
    }

    private void dynamicUpdateChart(LineChart lineChart, Double minTime, Double maxTime) {
        dynamicUpdateChart(lineChart, minTime, maxTime, false);
    }

    // Todo buttons for selecting a set interval
    private void dynamicUpdateChart(LineChart lineChart, Double minTime, Double maxTime, boolean round) {
        if (round) {
            int roundTo = 1;
            if (maxTime - minTime <= 60) roundTo = 1;
            else if (maxTime - minTime <= 60 * 60) roundTo = 10;
            else if (maxTime - minTime <= 60 * 60 * 24) roundTo = 60 * 5;
            else if (maxTime - minTime <= 60 * 60 * 24 * 5) roundTo = 3600;
            else roundTo = 3600 * 6;

            minTime = Double.valueOf(Math.floor(minTime - (minTime % roundTo)));
            maxTime = Double.valueOf(Math.floor(maxTime - (maxTime % roundTo)));
        }

        var xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setTickLabelFormatter(getConverterForInterval(minTime, maxTime));
        xAxis.setLabel(getTimeAxisName(minTime, maxTime));
        var timeGranularity = graphModel.getTimeGranularity();

        var chartSeries = lineChart.getData();
        isShowingSymbols = maxTime - minTime <= timeGranularity * 100;

        //lineChart.setCreateSymbols(isShowingSymbols);

        int emptyCenterPaneSize = centerPane.getChildren().size();

        // for (XYChart.Series series : (ObservableList<XYChart.Series>) chartSeries) {
        //     for (XYChart.Data dataPoint : (ObservableList<XYChart.Data>) series.getData()) {
        //         dataPoint.nodeProperty().addListener(ev -> {
        //             int timestamp = (int) (dataPoint.getXValue());
        //             var value = dataPoint.getYValue();
        //             var node = dataPoint.getNode();

        //             if (node != null) {
        //                 node.setOnMouseEntered(e -> {
        //                     if (isShowingSymbols) {
        //                         var labelValue = new ADLabel(series.getName() + ": " + df.format(value), -2);
        //                         var labelDate = new ADLabel("From: " + getFullDate((int) dataPoint.getExtraValue()), -4);
        //                         var labelDateUntil = new ADLabel("Until: " + getFullDate(timestamp), -4);

        //                         labelValue.setPadding(new Insets(0, 0, 4, 0));
        //                         labelDate.setPadding(new Insets(0, 0, 4, 0));

        //                         labelValue.getStyleClass().add("tooltip-value");
        //                         labelValue.setIsBold(true);
        //                         labelDate.getStyleClass().add("tooltip-date");
        //                         labelDateUntil.getStyleClass().add("tooltip-date");

        //                         var tooltip = new VBox(labelValue, labelDate, labelDateUntil);
        //                         var tooltipContainer = new Pane(tooltip);
        //                         tooltipContainer.setMouseTransparent(true);

        //                         tooltip.setStyle("-fx-spacing: 0px;");
        //                         tooltip.getStyleClass().add("tooltip");

        //                         centerPane.getChildren().add(tooltipContainer);

        //                         var translateX = node.getBoundsInParent().getMinX() - 42;
        //                         var translateY = node.getBoundsInParent().getMinY() - 64;

        //                         if (translateX < 8) translateX = 8;
        //                         if (translateX >= lineChart.getWidth() - 180) translateX = lineChart.getWidth() - 180;
        //                         if (translateY < 8) translateY = 8;

        //                         tooltip.setTranslateX(translateX);
        //                         tooltip.setTranslateY(translateY);

        //                         FadeTransition ft = new FadeTransition(Duration.millis(100), tooltip);
        //                         ft.setFromValue(0);
        //                         ft.setToValue(0.75);
        //                         ft.play();
        //                     }
        //                 });

        //                 node.setOnMouseExited(e -> {
        //                     centerPane.getChildren().remove(emptyCenterPaneSize, centerPane.getChildren().size());
        //                 });
        //             }
        //         });
        //     }
        // }

        xAxis.setTickUnit((maxTime - minTime) / 4);
        xAxis.setLowerBound(minTime);
        xAxis.setUpperBound(maxTime);
    }

    private void renderGraph() {
        isShowingSymbols = true;
        
        // Initialize X time axis
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(false); // important for zooming in and out
        xAxis.setMinorTickVisible(false);
        xAxis.setAnimated(false);

        // Initialize Y value axis
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setMinorTickVisible(false);
        if (graphModel.getHasCustomMaxY()) {
            yAxis.setAutoRanging(false);
            yAxis.setUpperBound(graphModel.getCustomMaxY());
            yAxis.setLowerBound(0);
        } else {
            yAxis.setAutoRanging(true);
        }
        
        // Initialize chart
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);

        lineChart.setMinSize(450, 360);
        lineChart.setMaxSize(450, 360);

        centerPane = new StackPane(lineChart);
        centerPane.setMaxSize(450, 360);

        // Create tooltips efficiently
        // var chartContent = (Parent) lineChart.getChildrenUnmodifiable().get(1);

        // chartContent.setOnMouseMoved(ev -> {
        //     if (ev.getTarget().getClass() == StackPane.class) {

        //     }
        // });

        displayPane.setCenter(centerPane);
        
        if (displayTopButtons) {
            var editButton = new ADButton("", "btn-graph-edit");
            editButton.setIcon("edit.png");
            editButton.addStyle("-fx-padding: 4px 12px 4px 12px;");
            editButton.setOnAction(ev -> sceneManager.setPopup(new GraphPopup(sceneManager, graphModel)));

            var topLeftButtons = new HBox();
            topLeftButtons.setMaxHeight(32);
            topLeftButtons.setMaxWidth(64);
            topLeftButtons.setStyle("-fx-alignment: top-left;");
            topLeftButtons.setPadding(new Insets(24, 0, 0, 52));

            var topRightButtons = new HBox(editButton);
            topRightButtons.setMaxHeight(32);
            topRightButtons.setMaxWidth(64);
            topRightButtons.setStyle("-fx-alignment: top-right;");
            topRightButtons.setPadding(new Insets(24, 24, 0, 0));
    
            centerPane.getChildren().addAll(topLeftButtons, topRightButtons);
            StackPane.setAlignment(topLeftButtons, Pos.TOP_LEFT);
            StackPane.setAlignment(topRightButtons, Pos.TOP_RIGHT);

            var opacity = 0.4;
            topLeftButtons.setOpacity(opacity);
            topRightButtons.setOpacity(opacity);

            topLeftButtons.setOnMouseEntered(ev -> {
                FadeTransition ft = new FadeTransition(Duration.millis(100), topLeftButtons);
                ft.setFromValue(0.4);
                ft.setToValue(1);
                ft.play();
            });

            topLeftButtons.setOnMouseExited(ev -> {
                FadeTransition ft = new FadeTransition(Duration.millis(100), topLeftButtons);
                ft.setFromValue(1);
                ft.setToValue(0.4);
                ft.play();
            });

            topRightButtons.setOnMouseEntered(ev -> {
                FadeTransition ft = new FadeTransition(Duration.millis(100), topRightButtons);
                ft.setFromValue(0.4);
                ft.setToValue(1);
                ft.play();
            });

            topRightButtons.setOnMouseExited(ev -> {
                FadeTransition ft = new FadeTransition(Duration.millis(100), topRightButtons);
                ft.setFromValue(1);
                ft.setToValue(0.4);
                ft.play();
            });
        }
        
        // Manage chart zooming / scrolling
        lineChart.setOnScroll(ev -> {
            var targetClass = ev.getTarget().getClass();

            if (ev.getDeltaY() != 0
                && (targetClass.equals(Region.class) || targetClass.equals(Path.class) || targetClass.equals(StackPane.class))
            ) {
                var graphXAxisRange = graphModel.getTimeInterval();
                
                var minTime = graphXAxisRange.getKey();
                var maxTime = graphXAxisRange.getValue();

                var scrollDistance = (maxTime - minTime) * ev.getDeltaY() * 0.002;
                minTime += scrollDistance;
                maxTime -= scrollDistance;

                var graphXValuesRange = graphModel.getTimeBoundsOfData();
                var maxTimeSize = (graphXValuesRange.getValue() - graphXValuesRange.getKey() + 1) * 2;

                if (maxTime - minTime >= 5 && (maxTime - minTime <= maxTimeSize || ev.getDeltaY() > 0)) {
                    graphModel.setTimeInterval(new Pair<>(minTime, maxTime));
                    dynamicUpdateChart(lineChart, minTime, maxTime, true);
                }

                ev.consume();
            }
        });

        // Manage chart panning
        lineChart.addEventFilter(MouseEvent.MOUSE_PRESSED, ev -> {
            graphDragOriginalX = (int) ev.getScreenX();
            graphDragOriginalY = (int) ev.getScreenY();
        });

        lineChart.addEventFilter(MouseEvent.MOUSE_DRAGGED, ev -> {
            var deltaX = (int) ev.getScreenX() - graphDragOriginalX;
            var deltaY = (int) ev.getScreenY() - graphDragOriginalY;

            if (deltaX != 0) {
                graphDragOriginalX = (int) ev.getScreenX();
                graphDragOriginalY = (int) ev.getScreenY();

                var graphXAxisRange = graphModel.getTimeInterval();

                var minTime = graphXAxisRange.getKey();
                var maxTime = graphXAxisRange.getValue();
                
                var originalMinTime = minTime;
                var originalMaxTime = maxTime;

                var dragDistance = (maxTime - minTime) * deltaX * 0.002;
                minTime -= dragDistance;
                maxTime -= dragDistance;

                var graphXValuesRange = graphModel.getTimeBoundsOfData();
                var graphTimeDuration = graphXValuesRange.getValue() - graphXValuesRange.getKey() + 1;
                var timeBounds = new Pair<>(graphXValuesRange.getKey() - graphTimeDuration / 2,
                                            graphXValuesRange.getValue() + graphTimeDuration / 2);
                
                boolean ok = false;
                if ((minTime >= timeBounds.getKey() || deltaX < 0)
                    && (maxTime <= timeBounds.getValue() || deltaX > 0)) {
                    ok = true;
                } else {
                    if (originalMinTime >= timeBounds.getKey() && minTime < timeBounds.getKey()) {
                        ok = true;
                        minTime = Double.valueOf(timeBounds.getKey());
                        maxTime = originalMaxTime;
                    }
                    
                    if (originalMaxTime <= timeBounds.getValue() && maxTime > timeBounds.getValue()) {
                        ok = true;
                        minTime = originalMinTime;
                        maxTime = Double.valueOf(timeBounds.getValue());
                    }
                }
                
                if (ok) {
                    graphModel.setTimeInterval(new Pair<>(minTime, maxTime));
                    dynamicUpdateChart(lineChart, minTime, maxTime, true);
                }
            }
        });

        var activeLines = graphModel.getActiveLines();
        var maxNumberOfDataPoints = ((graphDataMaxTime - graphDataMinTime) / graphModel.getTimeGranularity()) + 5;

        for (var lineType : activeLines) {
            logger.info("Computing line... " + lineType);
            XYChart.Series series = new XYChart.Series();

            XYChart.Data[] dataPoints = new XYChart.Data[(int) maxNumberOfDataPoints];
            int actualDataPointsSize = 0;

            switch (lineType) {
                case NUM_OF_IMPRESSIONS -> {
                    series.setName("Number of impressions");

                    ArrayList<ImpressionDataRow> data = graphModel.getImpressionData().getData();

                    var lastTimestamp = data.get(0).timestamp;
                    var impressionCount = 0;

                    for (var row : data) {
                        if (!isRowAccepted(row)) continue;
                        impressionCount += 1;

                        if (row.timestamp - lastTimestamp >= graphModel.getTimeGranularity() || row == data.get(data.size() - 1)) {
                            var dataPoint = new XYChart.Data(row.timestamp, impressionCount, lastTimestamp);
                            dataPoints[actualDataPointsSize++] = dataPoint;

                            lastTimestamp = row.timestamp;
                            impressionCount = 0;
                        }
                    }
                }
                case NUM_OF_CLICKS -> {
                    series.setName("Number of clicks");

                    ArrayList<ClickDataRow> data = graphModel.getClickData().getData();

                    var lastTimestamp = data.get(0).timestamp;
                    var clickCount = 0;

                    for (var row : data) {
                        if (!isRowAccepted(row)) continue;
                        clickCount += 1;

                        if (row.timestamp - lastTimestamp >= graphModel.getTimeGranularity() || row == data.get(data.size() - 1)) {
                            var dataPoint = new XYChart.Data(row.timestamp, clickCount, lastTimestamp);
                            dataPoints[actualDataPointsSize++] = dataPoint;

                            lastTimestamp = row.timestamp;
                            clickCount = 0;
                        }
                    }
                }
                case NUM_OF_UNIQUES -> {
                    series.setName("Number of uniques");

                    ArrayList<ClickDataRow> data = graphModel.getClickData().getData();
                    HashMap<Long, Boolean> idSeen = new HashMap<>();

                    var lastTimestamp = data.get(0).timestamp;
                    var new_ids_seen = 0;

                    for (var row : data) {
                        if (!isRowAccepted(row)) continue;
                        if (!idSeen.containsKey(row.id)) {
                            idSeen.put(row.id, true);
                            new_ids_seen += 1;
                        }

                        if (row.timestamp - lastTimestamp >= graphModel.getTimeGranularity() || row == data.get(data.size() - 1)) {
                            var dataPoint = new XYChart.Data(row.timestamp, new_ids_seen, lastTimestamp);
                            dataPoints[actualDataPointsSize++] = dataPoint;

                            lastTimestamp = row.timestamp;
                            new_ids_seen = 0;
                        }
                    }
                }
                case NUM_OF_BOUNCES -> {
                    //todo custom bounce!
                    series.setName("Number of bounces");

                    ArrayList<ServerDataRow> data = graphModel.getServerData().getData();

                    var lastTimestamp = data.get(0).timestamp;
                    var bounceCount = 0;

                    for (var row : data) {
                        if (!isRowAccepted(row)) continue;
                        if (graphModel.getBounceModel().isBounced(row)) {
                            bounceCount += 1;
                        }

                        if (row.timestamp - lastTimestamp >= graphModel.getTimeGranularity() || row == data.get(data.size() - 1)) {
                            var dataPoint = new XYChart.Data(row.timestamp, bounceCount, lastTimestamp);
                            dataPoints[actualDataPointsSize++] = dataPoint;

                            lastTimestamp = row.timestamp;
                            bounceCount = 0;
                        }

                    }
                }
                case NUM_OF_CONVERSIONS -> {
                    series.setName("Number of conversions");

                    ArrayList<ServerDataRow> data = graphModel.getServerData().getData();

                    var lastTimestamp = data.get(0).timestamp;
                    var conversionCount = 0;

                    for (var row : data) {
                        if (!isRowAccepted(row)) continue;
                        if (row.conversion == Conversion.YES) {
                            conversionCount += 1;
                        }

                        if (row.timestamp - lastTimestamp >= graphModel.getTimeGranularity() || row == data.get(data.size() - 1)) {
                            var dataPoint = new XYChart.Data(row.timestamp, conversionCount, lastTimestamp);
                            dataPoints[actualDataPointsSize++] = dataPoint;

                            lastTimestamp = row.timestamp;
                            conversionCount = 0;
                        }
                    }
                }

                case TOTAL_COST -> {
                    series.setName("Total cost");

                    ArrayList<ClickDataRow> clickData = graphModel.getClickData().getData();
                    ArrayList<ImpressionDataRow> impressionData = graphModel.getImpressionData().getData();

                    var startTime = Math.min(clickData.get(0).timestamp, impressionData.get(0).timestamp);
                    var endTime = Math.max(clickData.get(clickData.size() - 1).timestamp, impressionData.get(impressionData.size() - 1).timestamp);
                    startTime += graphModel.getTimeGranularity();
                    endTime += graphModel.getTimeGranularity();

                    int cdIndex = 0;
                    int idIndex = 0;
                    long lastTimestamp = startTime - graphModel.getTimeGranularity();

                    for (long i = startTime; i <= endTime; i += graphModel.getTimeGranularity()) {
                        double cost = 0.0;

                        while (cdIndex < clickData.size() && clickData.get(cdIndex).timestamp <= i) {
                            if (isRowAccepted(clickData.get(cdIndex))) {
                                cost += clickData.get(cdIndex).clickCost;
                            }
                            cdIndex += 1;
                        }

                        while (idIndex < impressionData.size() && impressionData.get(idIndex).timestamp <= i) {
                            if (isRowAccepted(impressionData.get(idIndex))) {
                                cost += impressionData.get(idIndex).impressionCost;
                            }
                            
                            idIndex += 1;
                        }

                        var dataPoint = new XYChart.Data(i, cost, lastTimestamp);
                        dataPoints[actualDataPointsSize++] = dataPoint;
                        
                        lastTimestamp = i;
                    }
                }

                case CTR -> {
                    series.setName("Click-through rate");
                    ArrayList<ClickDataRow> clickData = graphModel.getClickData().getData();
                    ArrayList<ImpressionDataRow> impressionData = graphModel.getImpressionData().getData();

                    var startTime = Math.min(clickData.get(0).timestamp, impressionData.get(0).timestamp);
                    var endTime = Math.max(clickData.get(clickData.size() - 1).timestamp, impressionData.get(impressionData.size() - 1).timestamp);
                    startTime += graphModel.getTimeGranularity();
                    endTime += graphModel.getTimeGranularity();

                    int cdIndex = 0;
                    int idIndex = 0;
                    long lastTimestamp = startTime - graphModel.getTimeGranularity();

                    for (long i = startTime; i <= endTime; i += graphModel.getTimeGranularity()) {
                        int clickCount = 0;
                        int impCount = 0;

                        while (cdIndex < clickData.size() && clickData.get(cdIndex).timestamp <= i) {
                            if (isRowAccepted(clickData.get(cdIndex))) {
                                clickCount++;
                            }

                            cdIndex += 1;
                        }

                        while (idIndex < impressionData.size() && impressionData.get(idIndex).timestamp <= i) {
                            if (isRowAccepted(impressionData.get(idIndex))) {
                                impCount++;
                            }

                            idIndex += 1;
                        }

                        if (impCount != 0) {
                            var dataPoint = new XYChart.Data(i, (double) clickCount / impCount, lastTimestamp);
                            dataPoints[actualDataPointsSize++] = dataPoint;
                        }

                        lastTimestamp = i;
                    }
                }
                case CPA -> {
                    series.setName("Cost per conversion");

                    ArrayList<ClickDataRow> clickData = graphModel.getClickData().getData();
                    ArrayList<ImpressionDataRow> impressionData = graphModel.getImpressionData().getData();
                    ArrayList<ServerDataRow> serverData = graphModel.getServerData().getData();

                    var startTime = Math.min(Math.min(clickData.get(0).timestamp, impressionData.get(0).timestamp), serverData.get(0).timestamp);
                    var endTime = Math.max(Math.max(clickData.get(clickData.size() - 1).timestamp, impressionData.get(impressionData.size() - 1).timestamp), serverData.get(serverData.size() - 1).timestamp);
                    startTime += graphModel.getTimeGranularity();
                    endTime += graphModel.getTimeGranularity();

                    int cdIndex = 0;
                    int idIndex = 0;
                    int svIndex = 0;
                    long lastTimestamp = startTime - graphModel.getTimeGranularity();

                    for (long i = startTime; i <= endTime; i += graphModel.getTimeGranularity()) {
                        double cost = 0.0;
                        int accCount = 0;

                        while (cdIndex < clickData.size() && clickData.get(cdIndex).timestamp <= i) {
                            if (isRowAccepted(clickData.get(cdIndex))) {
                                cost += clickData.get(cdIndex).clickCost;
                            }

                            cdIndex += 1;
                        }

                        while (idIndex < impressionData.size() && impressionData.get(idIndex).timestamp <= i) {
                            if (isRowAccepted(impressionData.get(idIndex))) {
                                cost += impressionData.get(idIndex).impressionCost;
                            }

                            idIndex += 1;
                        }

                        while (svIndex < serverData.size() && serverData.get(svIndex).timestamp <= i) {
                            if (isRowAccepted(serverData.get(svIndex))) {
                                if (serverData.get(svIndex).conversion.equals(Conversion.YES))
                                    accCount++;
                            }

                            svIndex += 1;
                        }

                        if (cost != 0) {
                            var dataPoint = new XYChart.Data(i, (double) accCount / cost, lastTimestamp);
                            dataPoints[actualDataPointsSize++] = dataPoint;
                        }

                        lastTimestamp = i;
                    }

                }
                case CPC -> {
                    series.setName("Cost per Click");
                    ArrayList<ClickDataRow> clickData = graphModel.getClickData().getData();
                    ArrayList<ImpressionDataRow> impressionData = graphModel.getImpressionData().getData();

                    var startTime = Math.min(clickData.get(0).timestamp, impressionData.get(0).timestamp);
                    var endTime = Math.max(clickData.get(clickData.size() - 1).timestamp, impressionData.get(impressionData.size() - 1).timestamp);
                    startTime += graphModel.getTimeGranularity();
                    endTime += graphModel.getTimeGranularity();

                    int cdIndex = 0;
                    int idIndex = 0;
                    long lastTimestamp = startTime - graphModel.getTimeGranularity();

                    for (long i = startTime; i <= endTime; i += graphModel.getTimeGranularity()) {
                        double cost = 0.0;
                        int clickCount = 0;

                        while (cdIndex < clickData.size() && clickData.get(cdIndex).timestamp <= i) {
                            if (isRowAccepted(clickData.get(cdIndex))) {
                                cost += clickData.get(cdIndex).clickCost;
                                clickCount++;
                            }

                            cdIndex += 1;
                        }

                        while (idIndex < impressionData.size() && impressionData.get(idIndex).timestamp <= i) {
                            if (isRowAccepted(impressionData.get(idIndex))) {
                                cost += impressionData.get(idIndex).impressionCost;
                            }

                            idIndex += 1;
                        }

                        if (clickCount != 0) {
                            var dataPoint = new XYChart.Data(i, cost / clickCount, lastTimestamp);
                            dataPoints[actualDataPointsSize++] = dataPoint;
                        }

                        lastTimestamp = i;
                    }
                }
                case CPM -> {
                    series.setName("Cost per 1000 impressions");
                    ArrayList<ClickDataRow> clickData = graphModel.getClickData().getData();
                    ArrayList<ImpressionDataRow> impressionData = graphModel.getImpressionData().getData();

                    var startTime = clickData.get(0).timestamp;
                    var endTime = clickData.get(clickData.size() - 1).timestamp;

                    if (impressionData.get(0).timestamp < startTime) {
                        startTime = impressionData.get(0).timestamp;
                    }

                    if (impressionData.get(impressionData.size() - 1).timestamp > endTime) {
                        endTime = impressionData.get(impressionData.size() - 1).timestamp;
                    }

                    startTime += graphModel.getTimeGranularity();
                    endTime += graphModel.getTimeGranularity();

                    int cdIndex = 0;
                    int idIndex = 0;
                    long lastTimestamp = startTime - graphModel.getTimeGranularity();

                    for (long i = startTime; i <= endTime; i += graphModel.getTimeGranularity()) {
                        double cost = 0.0;
                        int impressionCount = 0;

                        while (cdIndex < clickData.size() && clickData.get(cdIndex).timestamp <= i) {
                            if (isRowAccepted(clickData.get(cdIndex))) {
                                cost += clickData.get(cdIndex).clickCost;
                            }

                            cdIndex += 1;
                        }

                        while (idIndex < impressionData.size() && impressionData.get(idIndex).timestamp <= i) {
                            if (isRowAccepted(impressionData.get(idIndex))) {
                                cost += impressionData.get(idIndex).impressionCost;
                                impressionCount++;
                            }

                            idIndex += 1;
                        }

                        if (impressionCount != 0) {
                            var dataPoint = new XYChart.Data(i, cost * 1000.0 / impressionCount, lastTimestamp);
                            dataPoints[actualDataPointsSize++] = dataPoint;
                        }

                        lastTimestamp = i;
                    }
                }

                case BOUNCE_RATE -> {
                    //todo custom bounce
                    series.setName("Bounce Rate");
                    ArrayList<ServerDataRow> data = graphModel.getServerData().getData();

                    var lastTimestamp = data.get(0).timestamp;
                    var bounceCount = 0;
                    var clickCount = 0;

                    for (var row : data) {
                        if (!isRowAccepted(row)) continue;
                        clickCount++;

                        if (graphModel.getBounceModel().isBounced(row)) {
                            bounceCount += 1;
                        }

                        if (row.timestamp - lastTimestamp >= graphModel.getTimeGranularity() || row == data.get(data.size() - 1)) {
                            if (clickCount != 0) {
                                var dataPoint = new XYChart.Data(row.timestamp, (double) bounceCount / clickCount, lastTimestamp);
                                dataPoints[actualDataPointsSize++] = dataPoint;
                            }

                            lastTimestamp = row.timestamp;
                            bounceCount = 0;
                            clickCount = 0;
                        }
                    }
                }
            }

            series.setData(FXCollections.observableList(Arrays.asList(dataPoints).subList(0, actualDataPointsSize)));
            lineChart.getData().add(series);

            logger.info("Line computed");
        }

        //centerPane
        var graphXAxisRange = graphModel.getTimeInterval();
        
        // We need this at the end, since it modifies the opacity of symbols which have been added above
        dynamicUpdateChart(lineChart, graphXAxisRange.getKey(), graphXAxisRange.getValue());

        if (!graphModel.getHasCustomMaxY()) {
            graphModel.setCustomMaxY(yAxis.getUpperBound());
        }
    }

    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }

    // private void addMetric(Series series, DataType[] rawDataTypesNeeded, Function<ArrayList<DataRow>, Double>[] dataProcessors, Function<ArrayList<Double>, Double> finalMerger) {
    //     var dataTypesNeeded = Arrays.asList(rawDataTypesNeeded);
    //     ArrayList<ClickDataRow> clickData = graphModel.getClickData().getData();
    //     ArrayList<ImpressionDataRow> impressionData = graphModel.getImpressionData().getData();
    //     ArrayList<ServerDataRow> serverData = graphModel.getServerData().getData();
        
    //     var startTime = Integer.MAX_VALUE;
    //     var endTime = Integer.MIN_VALUE;

    //     for (var data : new Data[]{graphModel.getClickData(), graphModel.getImpressionData(), graphModel.getServerData()}) {
    //         var earliestTime = (int) (data.getEarliestDate().getTime() / 1000);
    //         var latestTime = (int) (data.getLatestDate().getTime() / 1000);
    //         if (earliestTime < startTime) startTime = earliestTime;
    //         if (latestTime > endTime) endTime = latestTime;
    //     }
        
    //     startTime += graphModel.getTimeGranularity();
    //     endTime += graphModel.getTimeGranularity();

    //     int cdIndex = 0;
    //     int idIndex = 0;
    //     int sdIndex = 0;

    //     int lastTimestamp = startTime - graphModel.getTimeGranularity();
    //     int maxTimestampCurrently = startTime;

    //     for (int i = startTime; i <= endTime; i += graphModel.getTimeGranularity()) {
    //         var values = new ArrayList<Double>();
    
    //         // This is very copypasted, but I couldn't find a better way to do it
    //         if (dataTypesNeeded.contains(DataType.CLICK_LOG)) {
    //             var dataRows = new ArrayList<DataRow>();

    //             while (cdIndex < clickData.size() && clickData.get(cdIndex).timestamp <= i) {
    //                 var row = clickData.get(cdIndex);
    //                 if (isRowAccepted(row)) {
    //                     dataRows.add(row);
    //                     var t = row.timestamp;
    //                     if (t > maxTimestampCurrently) maxTimestampCurrently = t;
    //                 }

    //                 cdIndex += 1;
    //             }

    //             values.add(dataProcessors[0].apply(dataRows));
    //         }

    //         if (dataTypesNeeded.contains(DataType.IMPRESSION_LOG)) {
    //             var dataRows = new ArrayList<DataRow>();

    //             while (idIndex < impressionData.size() && impressionData.get(idIndex).timestamp <= i) {
    //                 var row = impressionData.get(idIndex);
    //                 if (isRowAccepted(row)) {
    //                     dataRows.add(row);
    //                     var t = row.timestamp;
    //                     if (t > maxTimestampCurrently) maxTimestampCurrently = t;
    //                 }

    //                 idIndex += 1;
    //             }

    //             values.add(dataProcessors[1].apply(dataRows));
    //         }

    //         if (dataTypesNeeded.contains(DataType.SERVER_LOG)) {
    //             var dataRows = new ArrayList<DataRow>();

    //             while (sdIndex < serverData.size() && serverData.get(sdIndex).timestamp <= i) {
    //                 var row = serverData.get(sdIndex);
    //                 if (isRowAccepted(row)) {
    //                     dataRows.add(row);
    //                     var t = row.timestamp;
    //                     if (t > maxTimestampCurrently) maxTimestampCurrently = t;
    //                 }

    //                 sdIndex += 1;
    //             }

    //             values.add(dataProcessors[2].apply(dataRows));
    //         }

    //         var finalValue = finalMerger.apply(values);
    //         var dataPoint = new XYChart.Data(maxTimestampCurrently, finalValue, lastTimestamp);
    //         series.getData().add(dataPoint);
            
    //         lastTimestamp = maxTimestampCurrently;
    //     }
    // }

    public int dateToTimestamp(Date date) {
        return (int) (date.getTime() / 1000);
    }

    public boolean isRowAccepted(DataRow row) {
        if (graphModel.getFilters().size() == 0) {
            return true;
        }

        ImpressionDataRow idRow;

        if (row instanceof ImpressionDataRow) {
            idRow = (ImpressionDataRow) row;
        } else {
            var uidIndex = graphModel.getImpressionData().getIdMap().get(row.id);

            if (uidIndex == null) {
                logger.info("Could not find user id " + row.id + " in the data!");
                return true;
            }

            idRow = (ImpressionDataRow) graphModel.getImpressionData().getData().get(uidIndex.intValue());
        }

        // should this be moved to a class member for optimization purposes?
        
        for (int i = 0; i < filterAcceptance.length; i++) {
            filterAcceptance[i] = FilterAcceptance.NO_FILTER.ordinal();
        }
        
        for (var filter : graphModel.getFilters()) {
            var index = filter.getFilterType().ordinal();

            if (filter.isAccepted(row, idRow)) {
                if (filterAcceptance[index] == FilterAcceptance.NO_FILTER.ordinal()) {
                    filterAcceptance[index] = FilterAcceptance.HAS_TRUE.ordinal();
                }

                if (filterAcceptance[index] == FilterAcceptance.HAS_FALSE.ordinal()) {
                    filterAcceptance[index] = FilterAcceptance.HAS_TRUE.ordinal();
                }
            } else {
                if (filterAcceptance[index] == FilterAcceptance.NO_FILTER.ordinal()) {
                    filterAcceptance[index] = FilterAcceptance.HAS_FALSE.ordinal();
                }
            }
        }

        for (int i = 0; i < filterAcceptance.length; i++) {
            if (filterAcceptance[i] == FilterAcceptance.HAS_FALSE.ordinal()) {
                return false;
            }
        }

        return true;
    }

    public boolean isAlive() {
        return this.displayPane.getScene() != null;
    }
}