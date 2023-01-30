package group29.ui.popup;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import group29.enums.data.*;
import group29.enums.graph.FilterType;
import group29.enums.graph.LineType;
import group29.model.FilterModel;
import group29.model.GraphModel;
import group29.ui.SceneManager;
import group29.ui.element.ADButton;
import group29.ui.element.ADComboBox;
import group29.ui.element.ADLabel;
import group29.ui.element.ADTextField;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GraphPopup extends Popup {

    private final VBox linesVBox;
    private final VBox filtersVBox;
    private final ArrayList<ComboBox> metricComboBoxes;
    private final ArrayList<ComboBox> filter1ComboBoxes;
    private final ArrayList<ComboBox> filter2ComboBoxes;

    final List<Age> ageList = new ArrayList<>();
    final List<Context> contextList = new ArrayList<>();
    final List<Conversion> conversionList = new ArrayList<>();
    final List<Gender> genderList = new ArrayList<>();
    final List<Income> incomeList = new ArrayList<>();

    private final GraphModel graphModel;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.ENGLISH);

    public GraphPopup(SceneManager sceneManager, GraphModel graphModel) {
        super(sceneManager, "Edit Graph");

        this.graphModel = graphModel;
        this.metricComboBoxes = new ArrayList<>();
        this.filter1ComboBoxes = new ArrayList<>();
        this.filter2ComboBoxes = new ArrayList<>();

        var graphMinDate = new Date((long) Math.floor(graphModel.getTimeInterval().getKey() * 1000));
        var graphMaxDate = new Date((long) Math.floor(graphModel.getTimeInterval().getValue() * 1000));

        ADTextField timeGranularityField = new ADTextField();
        timeGranularityField.setNumeric();
        timeGranularityField.setText(graphModel.getTimeGranularity() + "");
        timeGranularityField.setMaxWidth(128);

        linesVBox = new VBox();
        filtersVBox = new VBox();

        for (var lineType : graphModel.getActiveLines()) {
            System.out.println("Saving line presets "+lineType);
            linesVBox.getChildren().add(createLineHBox(lineType));
        }

        ADButton newMetricButton = new ADButton("Add Line");
        newMetricButton.setOnAction(ev -> linesVBox.getChildren().add(createLineHBox()));

        for (var filter : graphModel.getFilters()) {
            var selections = getFilterModelDefaultSelections(filter);
            filtersVBox.getChildren().add(createFilterHBox(selections.getKey(), selections.getValue()));
        }

        ADButton newFiltersButton = new ADButton("Add Filter");
        newFiltersButton.setOnAction(ev -> filtersVBox.getChildren().add(createFilterHBox()));

        var saveBtn = new ADButton("Save", "btn-blue");

        var minTimeIntervalField = new ADTextField();
        var maxTimeIntervalField = new ADTextField();

        // Custom bounce begin

        var bounceModel = graphModel.getBounceModel();
        
        var pageCountField = new ADTextField();
        pageCountField.setNumeric();
        pageCountField.setMaxWidth(100);
        
        var timeSpentField = new ADTextField();
        timeSpentField.setNumeric();
        timeSpentField.setMaxWidth(100);

        pageCountField.setText(bounceModel.getPageCount() + "");
        timeSpentField.setText(bounceModel.getTimeSpent() + "");

        var checkPageCount = new CheckBox();
        var checkTimeSpent = new CheckBox();

        checkPageCount.setSelected(bounceModel.isUsingPageCount());
        checkTimeSpent.setSelected(bounceModel.isUsingTimeSpent());
        
        pageCountField.setDisable(!checkPageCount.isSelected());
        timeSpentField.setDisable(!checkTimeSpent.isSelected());

        checkPageCount.setOnAction(ev -> {pageCountField.setDisable(!checkPageCount.isSelected());});
        checkTimeSpent.setOnAction(ev -> {timeSpentField.setDisable(!checkTimeSpent.isSelected());});

        var labelBounce = new ADLabel("Configure what classifies as a bounce", 16);
        labelBounce.setFont("NunitoSans-ExtraLight.ttf");

        var bounceModelContainer = new VBox(
            labelBounce,
            new HBox(new ADLabel("Less than X pages viewed"), pageCountField, checkPageCount, new ADLabel("Enable")),
            new HBox(new ADLabel("Less than X seconds spent on website"), timeSpentField, checkTimeSpent, new ADLabel("Enable"))
        );

        // Custom bounce end

        var customMaxYField = new ADTextField();
        customMaxYField.setText(graphModel.getCustomMaxY() + "");

        var checkMaxY = new CheckBox();
        checkMaxY.setSelected(graphModel.getHasCustomMaxY());
        customMaxYField.setDisable(!checkMaxY.isSelected());

        checkMaxY.setOnAction(e -> customMaxYField.setDisable(!checkMaxY.isSelected()));

        saveBtn.setOnAction(ev -> {
            // Update lines
            graphModel.getActiveLines().clear();

            for (ComboBox metricComboBox : metricComboBoxes) {
                var index = metricComboBox.getSelectionModel().getSelectedIndex();
                graphModel.addActiveLine(LineType.values()[index], false); // false - means don't trigger listeners yet, since that would lag everything
            }

            // Update filters
            graphModel.getFilters().clear();

            for (int i = 0; i < filter1ComboBoxes.size(); i++) {
                FilterType filterType = FilterType.values()[filter1ComboBoxes.get(i).getSelectionModel().getSelectedIndex()];
                var selectionIndex2 = filter2ComboBoxes.get(i).getSelectionModel().getSelectedIndex(); // an IndexOutOfBounds waiting to happen? it should be safe though

                switch (filterType) {
                    case AGE -> graphModel.addFilter(new FilterModel(Age.values()[selectionIndex2], false));
                    case CONTEXT -> graphModel.addFilter(new FilterModel(Context.values()[selectionIndex2], false));
                    case CONVERSION -> graphModel.addFilter(new FilterModel(Conversion.values()[selectionIndex2], false));
                    case GENDER -> graphModel.addFilter(new FilterModel(Gender.values()[selectionIndex2], false));
                    case INCOME -> graphModel.addFilter(new FilterModel(Income.values()[selectionIndex2], false));
                }
            }

            try {
                var minTime = dateFormatter.parse(minTimeIntervalField.getText()).getTime() / 1000;
                var maxTime = dateFormatter.parse(maxTimeIntervalField.getText()).getTime() / 1000;
                graphModel.setTimeInterval(new Pair<>((double) minTime, (double) maxTime), false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            var pageCount = Integer.valueOf(pageCountField.getText());
            var timeSpent = Integer.valueOf(timeSpentField.getText());
            
            if (pageCount < 0) pageCount = 0;
            if (timeSpent < 0) timeSpent = 0;

            bounceModel.setPageCount(pageCount);
            bounceModel.setTimeSpent(timeSpent);

            bounceModel.setUsingPageCount(checkPageCount.isSelected());
            bounceModel.setUsingTimeSpent(checkTimeSpent.isSelected());

            graphModel.setHasCustomMaxY(false);
            if (checkMaxY.isSelected()) {
                try {
                    double newValue = Double.valueOf(customMaxYField.getText());
                    graphModel.setCustomMaxY(newValue);
                    graphModel.setHasCustomMaxY(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Update time granularity
            graphModel.setTimeGranularity(Integer.parseInt(timeGranularityField.getText())); // listeners are being triggered here
            
            sceneManager.popStackpane();
        });

        var labelLines = new ADLabel("Lines", 16);
        labelLines.setFont("NunitoSans-ExtraLight.ttf");
        var metricsVBox = new VBox(labelLines, linesVBox, newMetricButton);

        var labelFilters = new ADLabel("Filters", 16);
        labelFilters.setFont("NunitoSans-ExtraLight.ttf");
        var filtersVBoxRendered = new VBox(labelFilters, filtersVBox, newFiltersButton);

        var labelControls = new ADLabel("Graph controls", 16);
        labelControls.setFont("NunitoSans-ExtraLight.ttf");

        var deleteButton = new ADButton("Delete");
        deleteButton.addStyle("-fx-text-fill: #EE6055");
        deleteButton.setOnAction(e -> {graphModel.getCampaignModel().removeGraph(graphModel); sceneManager.popStackpane();});

        var exportButton = new ADButton("Export");
        exportButton.setOnAction(e -> export());

        minTimeIntervalField.setPromptText("Min time");
        maxTimeIntervalField.setPromptText("Max time");

        minTimeIntervalField.setText(dateFormatter.format(graphMinDate));
        maxTimeIntervalField.setText(dateFormatter.format(graphMaxDate));

        minTimeIntervalField.textProperty().addListener(ev-> {
            saveBtn.setDisable(!isDateInputValid(minTimeIntervalField, maxTimeIntervalField));
        });

        maxTimeIntervalField.textProperty().addListener(ev -> {
            saveBtn.setDisable(!isDateInputValid(minTimeIntervalField, maxTimeIntervalField));
        });


        var controlsVBox = new VBox(
            labelControls, 
            new HBox(new ADLabel("Point sample rate (in seconds)"), timeGranularityField),
            new HBox(new ADLabel("Manual Y axis upper limit"), customMaxYField, checkMaxY, new ADLabel("Enable")),
            new HBox(new ADLabel("Time interval"), minTimeIntervalField, maxTimeIntervalField)
        );

        metricsVBox.setMinWidth(400);
        filtersVBoxRendered.setMinWidth(400);

        var mainHBox = new HBox(metricsVBox, filtersVBoxRendered);

        metricsVBox.setStyle("-fx-alignment: top-center");
        filtersVBoxRendered.setStyle("-fx-alignment: top-center");

        var advancedSettings = new VBox(controlsVBox, bounceModelContainer);
        
        var checkAdvanced = new CheckBox();
        checkAdvanced.setSelected(graphModel.showingAdvancedSettings);
        advancedSettings.setManaged(checkAdvanced.isSelected());
        advancedSettings.setVisible(checkAdvanced.isSelected());

        checkAdvanced.setOnAction(ev -> {
            advancedSettings.setManaged(checkAdvanced.isSelected());
            advancedSettings.setVisible(checkAdvanced.isSelected());
            graphModel.showingAdvancedSettings = checkAdvanced.isSelected();
        });


        // Bounce model logic
        var contentVBox = new VBox(
            mainHBox,
            new HBox(checkAdvanced, new ADLabel("Show advanced settings")),
            advancedSettings,
            new HBox(deleteButton, exportButton, saveBtn)
        );
        
        displayPane.setCenter(contentVBox);
        BorderPane.setMargin(contentVBox, new Insets(0, 0, 16, 0));
    }

    private boolean isDateInputValid(ADTextField minInput, ADTextField maxInput) {
        if (hasValidDate(minInput) && hasValidDate(maxInput)) {
            try {
                var minTime = dateFormatter.parse(minInput.getText()).getTime() / 1000;
                var maxTime = dateFormatter.parse(maxInput.getText()).getTime() / 1000;

                if (minTime < maxTime) {
                    var timeBounds = graphModel.getTimeBoundsOfData();
                    var maxTimeInterval = (timeBounds.getValue() - timeBounds.getKey()) * 2;
                    logger.info(minTime + " " + maxTime + " " + maxTimeInterval);

                    return maxTime - minTime <= maxTimeInterval;
                } else return false;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean hasValidDate(ADTextField textField) {
        boolean valid = false;
        try {
            Date date = dateFormatter.parse(textField.getText());
            valid = true;
        } catch (Exception e) {
        }

        return valid;
    }

    public void export() {
        var csvFiletype = "CSV file";
        var pngFiletype = "PNG file";
        var jpgFiletype = "JPG file";
        var pdfFiletype = "PDF document";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Graph");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter(csvFiletype, "*.csv"),
            new FileChooser.ExtensionFilter(pngFiletype, "*.png"),
            new FileChooser.ExtensionFilter(jpgFiletype, "*.jpg"),
            new FileChooser.ExtensionFilter(pdfFiletype, "*.pdf")
        );

        File file = fileChooser.showSaveDialog(sceneManager.getJavaFxStage());
        if (file == null) {
            logger.info("File was null");
            return;
        }

        String selectedFiletype = fileChooser.getSelectedExtensionFilter().getDescription();
        String newFilepath = file.getPath();

        if (selectedFiletype == csvFiletype && !file.getName().endsWith(".csv")) newFilepath += ".csv";
        if (selectedFiletype == pngFiletype && !file.getName().endsWith(".png")) newFilepath += ".png";
        if (selectedFiletype == jpgFiletype && !file.getName().endsWith(".jpg")) newFilepath += ".jpg";
        if (selectedFiletype == pdfFiletype && !file.getName().endsWith(".pdf")) newFilepath += ".pdf";

        if (!newFilepath.equals(file.getPath())) {
            file = new File(newFilepath);
        }

        if (selectedFiletype.equals(csvFiletype)) {
            var graphData = graphModel.getGraphRenderer().getLineChart().getData();

            try {
                var csvWriter = new BufferedWriter(new FileWriter(file));
                int length = Collections.max(graphData.stream()
                        .map(x -> x.getData().size()).collect(Collectors.toList()));
                StringBuilder headingLine = new StringBuilder();
                for (var series : graphData) {
                    headingLine.append("Time (").append(series.getName()).append("),")
                            .append(series.getName()).append(",");
                }
                csvWriter.write(headingLine + "\n");
                for (int i = 0; i < length; i++) {
                    StringBuilder line = new StringBuilder();
                    for(var series : graphData){
                        if(series.getData().size() > i) {
                            line.append(series.getData().get(i).getXValue()).append(",")
                                    .append(series.getData().get(i).getYValue()).append(",");
                        } else {
                            line.append(",,");
                        }
                    }
                    csvWriter.write(line.substring(0,line.length()-1)); // to strip following comma
                    csvWriter.write("\n");
                }
                csvWriter.flush();
                csvWriter.close();

            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        } else if (selectedFiletype.equals(pngFiletype) || selectedFiletype.equals(jpgFiletype)) {
            String fileType = selectedFiletype.equals(pngFiletype) ? "png" : "jpeg";

            var graphImage = graphModel.getGraphRenderer().getDisplayPane().snapshot(null, null);

            try {
                ImageIO.write(getBufferedImage(graphImage), fileType, file);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (selectedFiletype.equals(pdfFiletype)) {
            try {
                var graphImage = graphModel.getGraphRenderer().getDisplayPane().snapshot(null, null);
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                ImageIO.write(getBufferedImage(graphImage), "png", byteOutput);
                logger.info(byteOutput.size());

                Document doc = new Document();
                PdfWriter.getInstance(doc, new FileOutputStream(file)); // This is needed for some reason
                doc.open();

                Image pdfImg = Image.getInstance(byteOutput.toByteArray());
                doc.add(pdfImg);
                doc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage getBufferedImage(javafx.scene.image.Image img) {
        var width = (int) img.getWidth();
        var height = (int) img.getHeight();
        int[] pixels = new int[width * height];

        img.getPixelReader().getPixels(0, 0, width, height, (WritablePixelFormat<IntBuffer>) img.getPixelReader().getPixelFormat(), pixels, 0, width);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var pixel = pixels[y * width + x];
                int r = (pixel & 0xFF0000) >> 16;
                int g = (pixel & 0xFF00) >> 8;
                int b = (pixel & 0xFF) >> 0;

                outputImage.getRaster().setPixel(x, y, new int[]{r, g, b});
            }
        }

        return outputImage;
    }

    HBox createLineHBox() {
        return createLineHBox(LineType.NUM_OF_IMPRESSIONS);
    }

    HBox createLineHBox(LineType defaultSelection) {
        ComboBox metricComboBox = new ADComboBox();

        var index = 0;
        var indexOfSelection = 0;

        for (LineType lineType : LineType.values()) {
            metricComboBox.getItems().add(lineType.toString().replace("_", " "));

            if (lineType == defaultSelection) {
                indexOfSelection = index;
            }

            index += 1;
        }

        metricComboBox.getSelectionModel().select(indexOfSelection);

        ADButton clearBtn = new ADButton("");
        clearBtn.setIcon("close.png");
        clearBtn.addStyle("-fx-padding: 4px 16px 4px 16px");

        var hBox = new HBox(metricComboBox, clearBtn); 

        clearBtn.setOnAction(ev -> {
            linesVBox.getChildren().remove(hBox);
            metricComboBoxes.remove(metricComboBox);
        });

        metricComboBoxes.add(metricComboBox);
        return hBox;
    }

    HBox createFilterHBox() {
        return createFilterHBox(0, 0);
    }

    ComboBox createFilterTypeCombobox() {
        ComboBox combobox = new ADComboBox();
        combobox.setMinWidth(150);

        for (var filterType : FilterType.values()) {
            combobox.getItems().add(filterType);
        }

        return combobox;
    }

    ComboBox createFilterValuesCombobox(FilterType filterType) {
        ComboBox combobox = new ADComboBox();
        combobox.setMinWidth(150);

        changeComboboxToFilterValues(combobox, filterType);

        return combobox;
    }

    HBox createFilterHBox(int defaultSelection1, int defaultSelection2) {
        var filter1Combobox = createFilterTypeCombobox();
        filter1Combobox.getSelectionModel().select(defaultSelection1);
        var filter2Combobox = createFilterValuesCombobox(FilterType.values()[defaultSelection1]);
        filter2Combobox.getSelectionModel().select(defaultSelection2);
        
        ADButton clearBtn = new ADButton("");
        clearBtn.setIcon("close.png");
        clearBtn.addStyle("-fx-padding: 4px 16px 4px 16px");

        HBox hBox = new HBox(filter1Combobox, filter2Combobox, clearBtn);

        filter1Combobox.setOnAction(ev -> changeComboboxToFilterValues(filter2Combobox, FilterType.values()[filter1Combobox.getSelectionModel().getSelectedIndex()]));

        clearBtn.setOnAction(ev -> {
            filtersVBox.getChildren().remove(hBox);
            filter1ComboBoxes.remove(filter1Combobox);
            filter2ComboBoxes.remove(filter2Combobox);
        });

        filter1ComboBoxes.add(filter1Combobox);
        filter2ComboBoxes.add(filter2Combobox);
        return hBox;
    }

    // Weird that it modifies it like that...
    void changeComboboxToFilterValues(ComboBox combobox, FilterType filterType) {
        combobox.getItems().clear();

        if (filterType == FilterType.AGE) {
            for (var ageFilter : Age.values()) {
                combobox.getItems().add(ageFilter.toString().replace(("_"), " "));
            }
        } else if (filterType == FilterType.CONTEXT) {
            for (var contextFilter : Context.values()) {
                combobox.getItems().add(contextFilter.toString().replace(("_"), " "));
            }
        } else if (filterType == FilterType.CONVERSION) {
            for (var conversionFilter : Conversion.values()) {
                combobox.getItems().add(conversionFilter);
            }
        } else if (filterType == FilterType.GENDER) {
            for (var genderFilter : Gender.values()) {
                combobox.getItems().add(genderFilter);
            }
        } else if (filterType == FilterType.INCOME) {
            for (var incomeFilter : Income.values()) {
                combobox.getItems().add(incomeFilter);
            }
        }

        combobox.getSelectionModel().select(0);
    }

    Pair<Integer, Integer> getFilterModelDefaultSelections(FilterModel filterModel) {
        int selection1 = Arrays.asList(FilterType.values()).indexOf(filterModel.getFilterType());
        int selection2 = 0;

        switch (filterModel.getFilterType()) {
            case AGE -> selection2 = Arrays.asList(Age.values()).indexOf(filterModel.getAge());
            case CONTEXT -> selection2 = Arrays.asList(Context.values()).indexOf(filterModel.getContext());
            case CONVERSION -> selection2 = Arrays.asList(Conversion.values()).indexOf(filterModel.getConversion());
            case GENDER -> selection2 = Arrays.asList(Gender.values()).indexOf(filterModel.getGender());
            case INCOME -> selection2 = Arrays.asList(Income.values()).indexOf(filterModel.getIncome());
        }

        return new Pair<>(selection1, selection2);
    }

    private FilterModel createFilterModel(int index) {
        ageList.addAll(Arrays.asList(Age.values()));
        contextList.addAll(Arrays.asList(Context.values()));
        conversionList.addAll(Arrays.asList(Conversion.values()));
        genderList.addAll(Arrays.asList(Gender.values()));
        incomeList.addAll(Arrays.asList(Income.values()));

        FilterModel filterModel;
        var filter1 = filter1ComboBoxes.get(index);
        var filter2 = filter2ComboBoxes.get(index);
        var filter2Index = filter2.getSelectionModel().getSelectedIndex();

        if (filter1.getValue().toString().contains("AGE")) {
            filterModel = new FilterModel(ageList.get(filter2Index), false);
        }
        else if (Objects.equals(filter1.getValue().toString(), "CONTEXT")) {
            filterModel = new FilterModel(contextList.get(filter2Index), false);
        }
        else if (Objects.equals(filter1.getValue().toString(), "CONVERSION")) {
            filterModel = new FilterModel(conversionList.get(filter2Index), false);
        }
        else if (Objects.equals(filter1.getValue().toString(), "GENDER")) {
            filterModel = new FilterModel(genderList.get(filter2Index), false);
        }
        else if (Objects.equals(filter1.getValue().toString(), "INCOME")) {
            filterModel = new FilterModel(incomeList.get(filter2Index), false);
        }
        else {
            filterModel = new FilterModel(Income.LOW, false);
        }
        return filterModel;
    }
}
