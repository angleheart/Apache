package Apache.util;

import Apache.config.Config;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import java.awt.print.*;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.File;
import java.io.IOException;

public final class PrintUtility {


    public static boolean printPDF(String name) {
        PDDocument document = null;

        try{
            System.out.println("Print ordered");

            if(!Config.ENABLE_PRINT){
                System.out.println("Printing is currently disabled in Apache.config");
                return false;
            }

            document = PDDocument.load(new File(name));
            PrinterJob job = PrinterJob.getPrinterJob();

            PrintService myPrintService = findPrintService(Config.PRINTER_NAME);

            Paper paper = new Paper();
            paper.setSize(612,792);
            paper.setImageableArea(12, 12, paper.getWidth()-24, paper.getHeight()-24);

            PageFormat pageFormat = new PageFormat();
            pageFormat.setPaper(paper);

            Book book = new Book();
            book.append(new PDFPrintable(document, Scaling.SHRINK_TO_FIT), pageFormat, document.getNumberOfPages());
            job.setPageable(book);
            job.setPrintService(myPrintService);

            job.print();
            document.close();
            return true;
        }catch(PrinterException printerException){
            try{
                document.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            System.out.println("Printer could not be found");
        } catch (IOException e) {
            if(document != null)
            try{

                document.close();
            }catch(Exception ee){
                ee.printStackTrace();
            }
            e.printStackTrace();
            System.out.println("File could not be found");
        }
        return false;
    }


    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }



}