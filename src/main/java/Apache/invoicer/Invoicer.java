package Apache.invoicer;

import Apache.config.Config;
import Apache.objects.*;
import Apache.util.PrintUtility;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;

import static Apache.util.General.cleanDouble;

public class Invoicer {

    static AnchorPane ROOT_PANE;

    static Label customerNameLabel;
    static Label customerAddressLabel;
    static Label customerAddress2Label;
    static Label vehicleDescriptionLabel;
    static Label shipToLabel;
    static Label invoiceNumberLabel;
    static Label customerNumberLabel;
    static Label dateLabel;
    static Label timeLabel;
    static Label poLabel;
    static Label counterPersonLabel;
    static Label termsLabel;
    static Label freightTotalLabel;
    static Label subTotalLabel;
    static Label salesTaxLabel;
    static Label totalLabel;
    static Label pageNumberLabel;
    static Label pageCountLabel;
    static Label partsForLabel;

    static Label[] mfgPartNumberArray = new Label[12];
    static Label[] descriptionArray = new Label[12];
    static Label[] quantityBoxArray = new Label[12];
    static Label[] listBoxArray = new Label[12];
    static Label[] unitBoxArray = new Label[12];
    static Label[] coreBoxArray = new Label[12];
    static Label[] extendedBoxArray = new Label[12];
    static Label[] taxBoxArray = new Label[12];

    private static Invoiceable INVOICE;

    public static void setInvoice(Invoice invoice) {
        INVOICE = invoice;
    }

    private static void clearLabel(Label label) {
        label.setText("");
    }

    private static void resetConsistent() {
        partsForLabel.setText("PARTS FOR:");
        clearLabel(customerNameLabel);
        clearLabel(customerAddressLabel);
        clearLabel(customerAddress2Label);
        clearLabel(vehicleDescriptionLabel);
        clearLabel(shipToLabel);
        clearLabel(pageCountLabel);
        clearLabel(invoiceNumberLabel);
        clearLabel(customerNumberLabel);
        clearLabel(dateLabel);
        clearLabel(timeLabel);
        clearLabel(poLabel);
        clearLabel(counterPersonLabel);
        clearLabel(termsLabel);
    }

    private static void resetInconsistent() {
        clearLabel(pageNumberLabel);
        clearLabel(freightTotalLabel);
        clearLabel(subTotalLabel);
        clearLabel(salesTaxLabel);
        clearLabel(totalLabel);
        resetLabelArray(mfgPartNumberArray);
        resetLabelArray(descriptionArray);
        resetLabelArray(quantityBoxArray);
        resetLabelArray(listBoxArray);
        resetLabelArray(unitBoxArray);
        resetLabelArray(coreBoxArray);
        resetLabelArray(extendedBoxArray);
        resetLabelArray(taxBoxArray);
    }

    private static void resetLabelArray(Label[] labelArray) {
        for (Label label : labelArray)
            label.setText("");
    }

    public static boolean printPayment(Payment payment) {
        try {
            resetConsistent();
            resetInconsistent();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
            SimpleDateFormat stf = new SimpleDateFormat("hh:mm a");

            customerNameLabel.setText(payment.getCustomer().getName());
            customerAddressLabel.setText(payment.getCustomer().getAddress());
            customerAddress2Label.setText(payment.getCustomer().getCityStateZip());
            customerNumberLabel.setText(payment.getCustomer().getNumber());
            dateLabel.setText(sdf.format(payment.getDate()));
            timeLabel.setText(stf.format(payment.getDate()));
            termsLabel.setText("PAYMENT");
            partsForLabel.setText("PAYMENT DETAIL:");
            vehicleDescriptionLabel.setText(payment.getDocumentDetail());
            descriptionArray[1].setText("   THANK YOU");
            totalLabel.setText(cleanDouble(payment.getAmount(), 2));
            printState("payment-temp");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean printInvoice() {
        return printInvoice(INVOICE);
    }

    public static boolean printInvoice(Invoiceable invoice) {
        INVOICE = invoice;
        try {
            resetConsistent();
            resetInconsistent();
            loadConsistent();
            process();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void printState(String fileName) throws Exception {
        WritableImage writableImage = ROOT_PANE.snapshot(new SnapshotParameters(), null);
        BufferedImage bufferedImage = new BufferedImage(2550, 1570, BufferedImage.TYPE_INT_ARGB);

        BufferedImage image;
        image = javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, bufferedImage);
        Graphics2D gd = (Graphics2D) image.getGraphics();
        gd.translate(2550, 1570);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);


        Document document = new Document();
        com.itextpdf.text.Rectangle rectangle = new com.itextpdf.text.Rectangle(612, 792);
        document.setPageSize(rectangle);

        String filePath = Config.TMP_PATH + fileName + ".pdf";

        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        Image img = Image.getInstance(baos.toByteArray());
        Image img2 = Image.getInstance(baos.toByteArray());

        img.scaleToFit(612, 1056);
        img2.scaleToFit(612, 1056);

        img.setAbsolutePosition(0, 415);
        img2.setAbsolutePosition(0, 0);

        document.open();
        document.add(img);
        document.add(img2);
        document.close();

        PrintUtility.printPDF(filePath);
    }

    private static void process() throws Exception {
        int pageNumber = 1;
        int lineIndex = 0;
        pageNumberLabel.setText(Integer.toString(pageNumber));

        for (InvoiceableLine line : INVOICE.getInvoiceableLines()) {
            if (lineIndex == 12) {
                totalLabel.setText("CONTINUED");
                printState("D" + INVOICE.getInvoiceNumber());
                resetInconsistent();
                pageNumber++;
                pageNumberLabel.setText(Integer.toString(pageNumber));
                lineIndex = 0;
            }
            mfgPartNumberArray[lineIndex].setText(line.getMfg().concat(" ").concat(line.getPartNumber()));
            descriptionArray[lineIndex].setText(line.getDescription());
            quantityBoxArray[lineIndex].setText(Integer.toString(line.getQty()));
            listBoxArray[lineIndex].setText(cleanDouble(line.getListPrice(), 3));
            unitBoxArray[lineIndex].setText(cleanDouble(line.getUnitPrice(), 3));
            extendedBoxArray[lineIndex].setText(cleanDouble(line.getExtendedPrice(), 2));
            double corePrice = line.getCorePrice();
            if (corePrice != 0) {
                coreBoxArray[lineIndex].setText(cleanDouble(corePrice, 2));
                extendedBoxArray[lineIndex].setText(cleanDouble(corePrice + line.getExtendedPrice(), 2));
            }
            taxBoxArray[lineIndex].setText(line.getTx());
            lineIndex++;
        }
        setTotals();
        printState("D" + INVOICE.getInvoiceNumber());
    }

    private static void setTotals() {
        double freight = INVOICE.getTotals().getFreightTotal();
        if (freight != 0) {
            freightTotalLabel.setText(cleanDouble(freight, 2));
        }
        subTotalLabel.setText(cleanDouble(INVOICE.getTotals().getSubTotal(), 2));
        salesTaxLabel.setText(cleanDouble(INVOICE.getTotals().getTaxTotal(), 2));
        totalLabel.setText(cleanDouble(INVOICE.getTotals().getGrandTotal(), 2));
    }


    private static void loadConsistent() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        SimpleDateFormat stf = new SimpleDateFormat("hh:mm a");

        customerNameLabel.setText(INVOICE.getCustomer().getName());
        customerAddressLabel.setText(INVOICE.getCustomer().getAddress());
        customerAddress2Label.setText(INVOICE.getCustomer().getCityStateZip());

        vehicleDescriptionLabel.setText(INVOICE.getVehicleDescription());
        shipToLabel.setText(INVOICE.getShipTo());
        pageCountLabel.setText(Integer.toString(INVOICE.getInvoiceableLines().size() / 13 + 1));
        invoiceNumberLabel.setText("D".concat(Integer.toString(INVOICE.getInvoiceNumber())));
        customerNumberLabel.setText(INVOICE.getCustomer().getNumber());
        dateLabel.setText(sdf.format(INVOICE.getDate()));
        timeLabel.setText(stf.format(INVOICE.getDate()));
        poLabel.setText(INVOICE.getPo());
        counterPersonLabel.setText(Integer.toString(INVOICE.getCounterPersonNumber()));
        termsLabel.setText(getTermsByCode(INVOICE.getReleaseCode()));
    }

    private static String getTermsByCode(ReleaseCode releaseCode) {
        String terms = "";
        if (INVOICE.getTransCode().equalsIgnoreCase("SAL"))
            terms += "SALE-";
        else if (INVOICE.getTransCode().equalsIgnoreCase("RET"))
            terms += "RETURN-";
        return terms + releaseCode.name();
    }

}
