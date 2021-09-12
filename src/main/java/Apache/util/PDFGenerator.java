package Apache.util;

import Apache.config.Config;
import Apache.console.eod.EODReport;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import static Apache.util.General.cleanDouble;

public class PDFGenerator {

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.UNDERLINE + Font.BOLD);

    private static Font generationFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.ITALIC);

    private static Font subCat = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);

    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);

    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);


    public static boolean createEODReport(
            EODReport today,
            EODReport mtd,
            int month,
            int day,
            int year
    ){
        try{
            Document document = new Document();
            com.itextpdf.text.Rectangle rectangle = new com.itextpdf.text.Rectangle(612,792);
            document.setPageSize(rectangle);
            String fileName = Config.TMP_PATH + "eod-temp.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            Paragraph header = new Paragraph();

            header.add(
                    new Paragraph(
                    "End of Day Report " + month + "/" + day + "/" + year,
                    catFont
                    )
            );


            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat stf = new SimpleDateFormat("hh:mm a");

            header.add(
                    new Paragraph(
                            "Report generated on " + sdf.format(today.getDate()) +
                                    " at " +
                                    stf.format(today.getDate()),
                            generationFont
                    )
            );

            addEmptyLine(header, 2);

            header.add(
                    new Paragraph(
                            "Sales Summary:",
                            subCat
                    )
            );

            addEmptyLine(header, 1);


            Paragraph paragraph = new Paragraph();

            PdfPTable salesSummaryTable = new PdfPTable(3);

            PdfPCell c1 = new PdfPCell(new Phrase(""));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            salesSummaryTable.addCell(c1);

            c1 = new PdfPCell(new Phrase("Today"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            salesSummaryTable.addCell(c1);

            c1 = new PdfPCell(new Phrase("Month To Date"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            salesSummaryTable.addCell(c1);
            salesSummaryTable.setHeaderRows(1);

            salesSummaryTable.addCell("+Net Cash Invoices");
            salesSummaryTable.addCell(cleanDouble(today.getSalesSummary().getNetCash(), 2));
            salesSummaryTable.addCell(cleanDouble(mtd.getSalesSummary().getNetCash(), 2));

            salesSummaryTable.addCell("+Net Charge Invoices");
            salesSummaryTable.addCell(cleanDouble(today.getSalesSummary().getNetCharge(), 2));
            salesSummaryTable.addCell(cleanDouble(mtd.getSalesSummary().getNetCharge(), 2));

            salesSummaryTable.addCell("-Net Freight");
            salesSummaryTable.addCell(cleanDouble(today.getSalesSummary().getNetFreight(), 2));
            salesSummaryTable.addCell(cleanDouble(mtd.getSalesSummary().getNetFreight(), 2));

            salesSummaryTable.addCell("-Net Interstore");
            salesSummaryTable.addCell(cleanDouble(today.getSalesSummary().getNetInterStore(), 2));
            salesSummaryTable.addCell(cleanDouble(mtd.getSalesSummary().getNetInterStore(), 2));

            salesSummaryTable.addCell("-Net Sales Tax");
            salesSummaryTable.addCell(cleanDouble(today.getSalesSummary().getNetSalesTax(), 2));
            salesSummaryTable.addCell(cleanDouble(mtd.getSalesSummary().getNetSalesTax(), 2));

            salesSummaryTable.addCell("Net Sales");
            salesSummaryTable.addCell(cleanDouble(today.getSalesSummary().getNetSales(), 2));
            salesSummaryTable.addCell(cleanDouble(mtd.getSalesSummary().getNetSales(), 2));


            paragraph.add(salesSummaryTable);

            addEmptyLine(paragraph, 2);

            paragraph.add(
                    new Paragraph(
                            "Sales Tax Summary:",
                            subCat
                    )
            );

            addEmptyLine(paragraph, 1);

            PdfPTable salesTaxSummaryTable = new PdfPTable(3);

            c1 = new PdfPCell(new Phrase(""));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            salesTaxSummaryTable.addCell(c1);

            c1 = new PdfPCell(new Phrase("Today"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            salesTaxSummaryTable.addCell(c1);

            c1 = new PdfPCell(new Phrase("Month To Date"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            salesTaxSummaryTable.addCell(c1);
            salesTaxSummaryTable.setHeaderRows(1);

            salesTaxSummaryTable.addCell("Net Taxable Sales");
            salesTaxSummaryTable.addCell(cleanDouble(today.getSalesTaxSummary().getNetTaxable(), 2));
            salesTaxSummaryTable.addCell(cleanDouble(mtd.getSalesTaxSummary().getNetTaxable(), 2));

            salesTaxSummaryTable.addCell("Net Non-Taxable Sales");
            salesTaxSummaryTable.addCell(cleanDouble(today.getSalesTaxSummary().getNetNonTaxable(), 2));
            salesTaxSummaryTable.addCell(cleanDouble(mtd.getSalesTaxSummary().getNetNonTaxable(), 2));

            salesTaxSummaryTable.addCell("Total Tax");
            salesTaxSummaryTable.addCell(cleanDouble(today.getSalesTaxSummary().getTotalSalesTax(), 2));
            salesTaxSummaryTable.addCell(cleanDouble(mtd.getSalesTaxSummary().getTotalSalesTax(), 2));

            paragraph.add(salesTaxSummaryTable);

            header.add(paragraph);


            document.add(header);
            document.close();

            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }





    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }




}
