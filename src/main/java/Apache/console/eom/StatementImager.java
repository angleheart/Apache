package Apache.console.eom;

import Apache.config.Config;
import Apache.util.PrintUtility;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import static Apache.util.General.cleanDouble;

public class StatementImager {

    
    static AnchorPane ROOT_PANE;

    static Label CUSTOMER_NAME_LABEL;
    static Label CUSTOMER_ADDRESS_LABEL;
    static Label CUSTOMER_ADDRESS_LABEL2;
    static Label GENERATED_LABEL;
    static Label PAID_THIS_MONTH_LABEL;
    static Label PAY_THIS_AMOUNT_LABEL;
    static Label PAGE_NUMBER_LABEL;
    static Label PAGE_COUNT_LABEL;

    static Label[] INVOICE_DATE_LABELS;
    static Label[] INVOICE_NUMBER_LABELS;
    static Label[] DETAIL_LABELS;
    static Label[] ORIGINAL_AMOUNT_LABELS;
    static Label[] AMOUNT_APPLIED_LABELS;
    static Label[] BALANCE_LABELS;
    static Label[] AMOUNT_DUE_LABELS;
    
    static Label STATEMENT_DATE_LABEL;
    static Label CUSTOMER_NUMBER_LABEL;
    static Label ACCOUNT_TOTAL_LABEL;
    static Label DAYS_90_LABEL;
    static Label DAYS_60_LABEL;
    static Label DAYS_30_LABEL;
    static Label CURRENT_LABEL;

    static List<CustomerStatement> statements;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
    private static CustomerStatement currentStatement;

    static void printAllStatements(){
        for(CustomerStatement statement : statements){
            currentStatement = statement;
            System.out.println("Printing statement for " + statement.getCustomer().getName());
            printStatement();
        }
    }

    private static void printStatement(){
        clearArrays();
        clearTotals();
        loadConstants();
        PAGE_NUMBER_LABEL.setText("1");

        int row = 0;
        for(StatementLine line : currentStatement.getStatementLines()){

            if(row == 36){
                CURRENT_LABEL.setText("CONTINUED");
                printCurrentState();
                PAGE_NUMBER_LABEL.setText(Integer.toString(Integer.parseInt(PAGE_NUMBER_LABEL.getText())+1));
                clearArrays();
                row = 0;
            }

            String[] cols = line.columns();
            INVOICE_DATE_LABELS[row].setText(cols[0]);
            INVOICE_NUMBER_LABELS[row].setText(cols[1]);
            DETAIL_LABELS[row].setText(cols[2]);
            ORIGINAL_AMOUNT_LABELS[row].setText(cols[3]);
            AMOUNT_APPLIED_LABELS[row].setText(cols[4]);
            BALANCE_LABELS[row].setText(cols[5]);
            AMOUNT_DUE_LABELS[row].setText(cols[6]);
            row++;
        }
        loadTotals();
        printCurrentState();
    }


    private static void loadConstants(){
        CUSTOMER_NAME_LABEL.setText(currentStatement.getCustomer().getName());
        CUSTOMER_ADDRESS_LABEL.setText(currentStatement.getCustomer().getAddress());
        CUSTOMER_ADDRESS_LABEL2.setText(currentStatement.getCustomer().getCityStateZip());
        GENERATED_LABEL.setText("Generated " + sdf.format(currentStatement.getDate()));
        STATEMENT_DATE_LABEL.setText(sdf.format(currentStatement.getDate()));
        PAID_THIS_MONTH_LABEL.setText(cleanDouble(currentStatement.getTotalPaid(), 2));
        PAY_THIS_AMOUNT_LABEL.setText(cleanDouble(currentStatement.getTotalBalance(), 2));
        PAGE_COUNT_LABEL.setText(Integer.toString(currentStatement.getStatementLines().size() / 37 + 1));
        CUSTOMER_NUMBER_LABEL.setText(currentStatement.getCustomer().getNumber());
    }

    private static void loadTotals(){
        ACCOUNT_TOTAL_LABEL.setText(cleanDouble(currentStatement.getTotalBalance(), 2));
        DAYS_90_LABEL.setText(cleanDouble(currentStatement.getBal90(), 2));
        DAYS_60_LABEL.setText(cleanDouble(currentStatement.getBal60(), 2));
        DAYS_30_LABEL.setText(cleanDouble(currentStatement.getBal30(), 2));
        CURRENT_LABEL.setText(cleanDouble(currentStatement.getBalCurr(), 2));
    }

    private static void clearTotals(){
        ACCOUNT_TOTAL_LABEL.setText("");
        DAYS_90_LABEL.setText("");
        DAYS_60_LABEL.setText("");
        DAYS_30_LABEL.setText("");
        CURRENT_LABEL.setText("");
    }

    private static void printCurrentState() {
        try{
            WritableImage writableImage = ROOT_PANE.snapshot(new SnapshotParameters(), null);
            BufferedImage bufferedImage = new BufferedImage(2550, 3300, BufferedImage.TYPE_INT_ARGB);

            BufferedImage image;
            image = javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, bufferedImage);
            Graphics2D gd = (Graphics2D) image.getGraphics();
            gd.translate(2550, 3300);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);


            Document document = new Document();
            com.itextpdf.text.Rectangle rectangle = new com.itextpdf.text.Rectangle(612,792);
            document.setPageSize(rectangle);

            String fileName = Config.TMP_PATH + "statement-temp.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(baos.toByteArray());
            img.scaleToFit(612, 1056);
            img.setAbsolutePosition(0,0);

            document.open();
            document.add(img);
            document.close();
            PrintUtility.printPDF(fileName);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void clearArrays(){
        clearArray(INVOICE_DATE_LABELS);
        clearArray(INVOICE_NUMBER_LABELS);
        clearArray(DETAIL_LABELS);
        clearArray(ORIGINAL_AMOUNT_LABELS);
        clearArray(AMOUNT_APPLIED_LABELS);
        clearArray(BALANCE_LABELS);
        clearArray(AMOUNT_DUE_LABELS);
    }

    private static void clearArray(Label[] labels){
        for(Label label : labels)
            label.setText("");
    }




}
