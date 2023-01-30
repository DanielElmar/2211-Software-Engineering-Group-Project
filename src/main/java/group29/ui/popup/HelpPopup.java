package group29.ui.popup;

import group29.model.TableRow;
import group29.ui.SceneManager;
import group29.ui.element.ADLabel;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.ArrayList;

public class HelpPopup extends Popup {

    private final ArrayList<TableRow> defTableRows = new ArrayList<>();
    private ArrayList<TableRow> shortcutTableRows = new ArrayList<>();

    
    public HelpPopup(SceneManager sceneManager, ArrayList<TableRow> shortcutTableRows) {
        super(sceneManager, "Help");
        this.shortcutTableRows = shortcutTableRows;

        defTableRows.add(new TableRow("Acquisition/Conversion", "A conversion, or acquisition, occurs when a user clicks and then acts on an ad. The specific definition of an action depends on the campaign (e.g., buying a product, registering as a new customer or joining a mailing list)."));
        defTableRows.add(new TableRow("Bounce","A user clicks on an ad, but then fails to interact with the website (typically detected when a user navigates away from the website after a short time, or when only a single page has been viewed)"));
        defTableRows.add(new TableRow("Bounce Rate","The average number of bounces per click."));
        defTableRows.add(new TableRow("Campaign", "An effort by the marketing agency to gain exposure for a client's website by participating in a range of ad auctions offered by different providers and networks. Bid amounts, keywords and other variables will be tailored to the client's needs. "));
        defTableRows.add(new TableRow("Click", "A click occurs when a user clicks on an ad that is shown to them."));
        defTableRows.add(new TableRow("CLick Cost", "The cost of a particular click (usually determined through an auction process."));
        defTableRows.add(new TableRow("Click-through-rate (CTR)", "The average number of clicks per impression."));
        defTableRows.add(new TableRow("Conversion Rate", "The average number of conversions per click."));
        defTableRows.add(new TableRow("Cost-per-acquisition (CPA)", "The average amount of money spent on an advertising campaign for each acquisition (i.e. conversion)."));
        defTableRows.add(new TableRow("Cost-per-click (CPC)", "The average amount of money spent on an advertising campaign for each click."));
        defTableRows.add(new TableRow("Cost-per-thousand impressions (CPM)", "The average amount of money spent on an advertising campaign for every one thousand impressions"));
        defTableRows.add(new TableRow("Impression", "An impression occurs whenever an ad is shown to a user, regardless of whether they click on it."));
        defTableRows.add(new TableRow("Uniques", "The number of unique users that click on an ad during the course of a campaign."));
        
        var labelShortcut = new ADLabel("Key Shortcuts", 16);
        labelShortcut.setFont("NunitoSans-ExtraLight.ttf");
        VBox mainWrapper = new VBox(renderDefinitionsTable(), labelShortcut, renderShortcutsTable());
        mainWrapper.setPadding(new Insets(16));
        displayPane.setCenter(mainWrapper);
    }

    private TableView<TableRow> renderDefinitionsTable() {
        TableView<TableRow> tableView = new TableView<>();
        tableView.setMinHeight(240);
        tableView.setEditable(false);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRow, String> column1 = new TableColumn<>("Key Term");
        column1.setCellValueFactory(new PropertyValueFactory<>("key"));

        TableColumn<TableRow, String> column2 = new TableColumn<>("Definition");
        column2.setCellValueFactory(new PropertyValueFactory<>("value"));
        column2.setCellFactory(new Callback<>() {
            @Override
            public TableCell<TableRow, String> call(TableColumn<TableRow, String> param) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            var text = new Text(item);
                            text.setWrappingWidth(400); // Setting the wrapping width to the Text
                            setGraphic(text);
                        }
                    }
                };
            }
        });

        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);

        for (TableRow tableRow : defTableRows) {
            tableView.getItems().add(tableRow);
        }

        return tableView;
    }

    private TableView renderShortcutsTable(){

        TableView tableView = new TableView();
        tableView.setEditable(false);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TableRow, String> column1 = new TableColumn<>("Key");
        column1.setCellValueFactory(new PropertyValueFactory<>("key"));

        TableColumn<TableRow, String> column2 = new TableColumn<>("Action");
        column2.setCellValueFactory(new PropertyValueFactory<>("value"));
        column2.setCellFactory(new Callback<>() {
            @Override
            public TableCell<TableRow, String> call(TableColumn<TableRow, String> param) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            var text = new Text(item);
                            text.setWrappingWidth(400); // Setting the wrapping width to the Text
                            setGraphic(text);
                        }
                    }
                };
            }
        });

        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);


        for (TableRow tableRow : shortcutTableRows) {
            tableView.getItems().add(tableRow);
        }

        return tableView;

    }
    
}