package group29.ui.popup;

import group29.ui.SceneManager;
import group29.ui.element.ADButton;
import group29.ui.element.ADComboBox;
import group29.ui.element.ADLabel;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

public class PrintPopup extends Popup {

    public PrintPopup(SceneManager sceneManager) {
        super(sceneManager, "Print");

        ImageView preview = sceneManager.capturePrint();
        preview.setPreserveRatio(true);
        preview.setFitWidth(320);
        
        var printerChooser = new ADComboBox();
        for (Printer p : Printer.getAllPrinters()) {
            printerChooser.getItems().add(p);
        }

        // Auto choose the first option
        printerChooser.getSelectionModel().select(Printer.getDefaultPrinter());

        PrinterJob job = PrinterJob.createPrinterJob();

        ADButton printButton = new ADButton("Print", "btn-blue");
        printButton.setOnAction( (e) -> {
            print(job, preview, (Printer) printerChooser.getSelectionModel().getSelectedItem());
            sceneManager.popStackpane();
        });
        
        var printLabel = new ADLabel("Choose a printer:");

        displayPane.setCenter(new VBox(preview, new HBox(printLabel, printerChooser), printButton));
    }

    public void print(PrinterJob job, ImageView ivSnapshot, Printer printer) {
        if (job != null) {
            boolean proceed = job.showPageSetupDialog(sceneManager.getJavaFxStage());
            if (!proceed)
                return;
            job.setPrinter(printer);

            // compute the needed scaling (aspect ratio must be kept)
            final PageLayout pageLayout = job.getJobSettings().getPageLayout();
            final double scaleX = pageLayout.getPrintableWidth() / ivSnapshot.getImage().getWidth();
            final double scaleY = pageLayout.getPrintableHeight() / ivSnapshot.getImage().getHeight();
            final double scale = Math.min(scaleX, scaleY);

            logger.info("scale: " + scale);
            // scale the calendar image only when it's too big for the selected page
            ivSnapshot.getTransforms().add(new Scale(scale, scale));

            boolean success = job.printPage(ivSnapshot);
            if (success) {
                job.endJob();
            }
        }
    }
}