package Apache.console;

import Apache.config.Config;
import Apache.database.EODBase;
import Apache.database.PaymentBase;
import Apache.console.eod.EODReport;
import Apache.http.Gateway;
import Apache.util.PDFGenerator;
import Apache.util.PrintUtility;
import Apache.objects.Invoice;
import Apache.objects.Payment;
import Apache.objects.Selectable;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static Apache.util.General.cleanDouble;

public class EODConsole extends Console {

    private HelpMenu helpMenu;

    EODConsole(Scanner scanner, String prompt) {
        super(scanner, prompt);
        helpMenu = new HelpMenu();
        helpMenu.addOption("balance  ", "Balance the drawer");
        helpMenu.addOption("closeout ", "Close out day and generate report");
        helpMenu.addOption("print    ", "Print daily reports");
        helpMenu.addOption("backup   ", "Back up the Apache.database");
    }

    @Override
    boolean handle(String input) {
        input = input.toUpperCase(Locale.ROOT);
        switch (input) {
            case "BALANCE" -> {
//                List<Selectable> openReleases = EODBase.getAllOpenReleases();
//                if (openReleases == null) {
//                    Console.printError("A Apache.database error occurred");
//                    return true;
//                }
//
//                double netCashSales = 0;
//                double netCheckSales = 0;
//                double netPlasticSales = 0;
//                double cashPayments = 0;
//                double checkPayments = 0;
//                double plasticPayments = 0;
//
//                for (Selectable releaseSelectable : openReleases) {
//                    Release release = (Release) releaseSelectable;
//                    switch (release.getType()) {
//
//                        case INVOICE -> {
//                            switch (release.getReleaseCode()) {
//                                case CASH -> netCashSales += release.getReleaseTotal();
//                                case CHECK -> netCheckSales += release.getReleaseTotal();
//                                case PLASTIC -> netPlasticSales += release.getReleaseTotal();
//                            }
//                        }
//
//                        case PAYMENT -> {
//                            switch (release.getReleaseCode()) {
//                                case CASH -> cashPayments += release.getReleaseTotal();
//                                case CHECK -> checkPayments += release.getReleaseTotal();
//                                case PLASTIC -> plasticPayments += release.getReleaseTotal();
//                            }
//                        }
//                    }
//                }
//                String netBalance =
//                        cleanDouble(
//                                netCashSales +
//                                        netCheckSales +
//                                        netPlasticSales +
//                                        cashPayments +
//                                        checkPayments +
//                                        plasticPayments,
//                                2
//                        );
//                System.out.println("-----Cash Balance-----");
//                System.out.println("Net Cash Sales:\t\t" + cleanDouble(netCashSales, 2));
//                System.out.println("Net Check Sales:\t" + cleanDouble(netCheckSales, 2));
//                System.out.println("Net Plastic Sales:\t" + cleanDouble(netPlasticSales, 2));
//                System.out.println("Total Cash Invoices:\t" + COLOR_GREEN +
//                        cleanDouble(netCashSales + netPlasticSales + netCheckSales, 2) + COLOR_RESET);
//
//                System.out.println();
//                System.out.println("Cash Payments:\t\t" + cleanDouble(cashPayments, 2));
//                System.out.println("Check Payments:\t\t" + cleanDouble(checkPayments, 2));
//                System.out.println("Plastic Payments:\t" + cleanDouble(plasticPayments, 2));
//                System.out.println("Total Payments:\t\t" + COLOR_GREEN +
//                        cleanDouble(cashPayments + plasticPayments + checkPayments, 2) + COLOR_RESET);
//                System.out.println();
//                System.out.println("Expected Balance:\t" + COLOR_GREEN + netBalance + COLOR_RESET);
//                System.out.print("Your Balance:\t\t");
//                String total = scanner.nextLine();
//                System.out.println("----------------------");
//                if (isExitCommand(total))
//                    return true;
//                try {
//                    double totalDouble = Double.parseDouble(cleanDouble(Double.parseDouble(total), 2));
//
//                    if (totalDouble >= Double.parseDouble(netBalance)) {
//                        double abs = (totalDouble - Double.parseDouble(netBalance));
//                        if(abs <= 5)
//                            System.out.print(COLOR_GREEN);
//                        else if(abs <= 10)
//                            System.out.print(COLOR_YELLOW);
//                        else
//                            System.out.print(COLOR_RED);
//
//                        System.out.println("OVER " +
//                                cleanDouble(abs, 2) +
//                                COLOR_RESET);
//                    } else if (totalDouble < Double.parseDouble(netBalance)) {
//                        double abs = ((totalDouble - Double.parseDouble(netBalance)) * -1);
//                        if(abs <= 5)
//                            System.out.print(COLOR_GREEN);
//                        else if(abs <= 10)
//                            System.out.print(COLOR_YELLOW);
//                        else
//                            System.out.print(COLOR_RED);
//                        System.out.println("SHORT " +
//                                cleanDouble(abs, 2) +
//                                COLOR_RESET);
//                    }
//                    System.out.println("----------------------");
//
//                } catch (Exception e) {
//                    printError("Invalid input");
//                }
                return true;
            }

            case "CLOSEOUT" -> {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                String dateString = sdf.format(date);

                int month = Integer.parseInt(dateString.substring(0, 2));
                int day = Integer.parseInt(dateString.substring(3, 5));
                int year = Integer.parseInt(dateString.substring(6));

                Boolean exists = EODBase.eodAlreadyExists(month, day, year);

                if (exists == null) {
                    Console.printError("A Apache.database error occurred- please contact support");
                    return true;
                }

                if (exists) {
                    Console.printError("A report has already been generated for today.");
                    return true;
                }

                printWarning(
                        "You are about to close out the store for the day.\n" +
                                "A report will be generated for " + month + "/" + day + "/" + year + "." +
                                "\nYou will be unable to generate another report until tomorrow.\n\n" +

                                "Please \"F12\" all terminals before running end of day.\n" +
                                "Any other Apache.database activities at this time will jeopardize the\n" +
                                "integrity of this report."
                );

                printConfirmRequest();
                String confirm = scanner.nextLine();

                if (!confirm.equalsIgnoreCase("confirm")) {
                    printError("End of day closeout was cancelled");
                    return true;
                }

                System.out.println("Collecting all open invoices...");
                List<Invoice> openInvoicesAsSelectable = Gateway.getOpenInvoices();
                System.out.println("Collecting all open payments...");
                List<Selectable> openPaymentsAsSelectable = PaymentBase.getAllOpenBasicPayments();
                Date cutOff = new Date();

                if (openInvoicesAsSelectable == null) {
                    Console.printError("Failed to collect open invoices");
                    return true;
                }

                if (openPaymentsAsSelectable == null) {
                    Console.printError("Failed to collect open payments");
                    return true;
                }

                if (openInvoicesAsSelectable.size() == 0) {
                    Console.printWarning("There are currently no open invoices.\n" +
                            "Are you sure you would like to generate a report?");
                    Console.printConfirmRequest();
                    String confirm2 = scanner.nextLine().trim();
                    if (!confirm2.equalsIgnoreCase("CONFIRM")) {
                        printError("Cancelled report generation");
                        return true;
                    }
                }

                List<Invoice> openInvoices = new ArrayList<>();
                for (Selectable invoice : openInvoicesAsSelectable)
                    openInvoices.add((Invoice) invoice);
                Console.printSuccess("Collected all open invoices");


                List<Payment> openPayments = new ArrayList<>();
                for (Selectable payment : openPaymentsAsSelectable) {
                    openPayments.add((Payment) payment);
                }
                Console.printSuccess("Collected all open payments");

                System.out.println("Generating end of day report...");
                EODReport eodReport = new EODReport(openInvoices, cutOff);
                printSuccess("Daily Report Generated");
                System.out.println("Saving report to Apache.database...");

                if (!EODBase.saveEODReport(month, day, year, eodReport)) {
                    printError("A Apache.database error occurred. Please contact support\n" +
                            "before attempting this operation again.");
                    return true;
                }
                printSuccess("End of day report saved");

                System.out.println("Moving invoices and payments to current...");
                if (!EODBase.closeDay(cutOff)) {
                    Console.printError("Failed to update accounting period. Please contact support.");
                    return true;
                }

                printSuccess("Your end of day closeout is complete. Use the \"print\" command to print the report");
                return true;
            }

            case "PRINT" -> {
                System.out.print("Enter month as a number (1-12): ");
                String monthStr = scanner.nextLine().trim();
                if (isExitCommand(monthStr)) {
                    System.out.println("Aborting...");
                    return true;
                }
                int month;
                try {
                    month = Integer.parseInt(monthStr);
                    if (month < 1 || month > 12) {
                        printError("Invalid month");
                        return true;
                    }
                } catch (Exception e) {
                    printError("Invalid month");
                    return true;
                }

                System.out.print("Enter day as a number (1-31): ");
                String dayStr = scanner.nextLine().trim();
                if (isExitCommand(dayStr)) {
                    System.out.println("Aborting...");
                    return true;
                }

                int day;

                try {
                    day = Integer.parseInt(dayStr);
                    if (day > 31 || day < 1) {
                        printError("Invalid day");
                        return true;
                    }
                } catch (Exception e) {
                    printError("Invalid day");
                    return true;
                }

                System.out.print("Enter year as a 4 digit number: ");
                String yearStr = scanner.nextLine();
                if (isExitCommand(yearStr)) {
                    System.out.println("Aborting...");
                    return true;
                }
                int year;
                if (yearStr.length() != 4) {
                    printError("Invalid year");
                    return true;
                }
                try {
                    year = Integer.parseInt(yearStr);
                } catch (Exception e) {
                    printError("Invalid year");
                    return true;
                }
                Boolean exists = EODBase.eodAlreadyExists(
                        month, day, year
                );
                if (exists == null) {
                    printError("A Apache.database error occurred, please contact support");
                    return true;
                }
                if (!exists) {
                    printError("Sorry, this end of day report does not exist");
                    return true;
                }
                System.out.println("Pulling end of day report...");

                EODReport todayEODReport;
                try {
                    todayEODReport = EODBase.getEODReport(
                            month,
                            day,
                            year
                    );
                } catch (SQLException sqlException) {
                    printError("A Apache.database error occurred, please contact support");
                    return true;
                }

                if (todayEODReport != null && todayEODReport.isSane()) {
                    printSuccess("Loaded end of day report");
                } else {
                    printError("Daily EOD Report failed sanity check, please contact support");
                    return true;
                }

                System.out.println("Concatenating monthly report...");
                EODReport mtdEODReport = EODBase.getMonthToDateEODReport(
                        month,
                        day,
                        year
                );
                if (mtdEODReport != null && mtdEODReport.isSane()) {
                    printSuccess("Concatenated monthly report");
                } else {
                    printError("Monthly report concatenation failed, please contact support");
                    return true;
                }

                boolean softCopy;
                do {
                    System.out.print("Print soft copy? (Y/N): ");
                    String yn = scanner.nextLine().trim();
                    if (isExitCommand(yn)) {
                        System.out.println("Aborting...");
                        return true;
                    }

                    if (yn.equalsIgnoreCase("Y")) {
                        softCopy = true;
                        break;
                    } else if (yn.equalsIgnoreCase("N")) {
                        softCopy = false;
                        break;
                    }
                    printError("Invalid entry, enter \"Y\" or \"N\" only");
                } while (true);

                if(softCopy) {

                    System.out.println(
                            "-----------------" +
                                    "[End Of Day Report " + month + "/" + day + "/" + year + "]" +
                                    "-----------------"
                    );
                    System.out.println("SALES SUMMARY:\t\t\tTODAY\t\tMONTH TO DATE" + COLOR_GREEN);
                    System.out.println(
                            "\t+Net Cash Invoices:\t" +
                                    cleanDouble(todayEODReport.getSalesSummary().getNetCash(), 2) + "\t\t" +
                                    cleanDouble(mtdEODReport.getSalesSummary().getNetCash(), 2)
                    );
                    System.out.println(
                            "\t+Net Charge Invoices:\t" +
                                    cleanDouble(todayEODReport.getSalesSummary().getNetCharge(), 2) + "\t\t" +
                                    cleanDouble(mtdEODReport.getSalesSummary().getNetCharge(), 2)
                    );
                    System.out.println(
                            "\t-Net Freight:\t\t" +
                                    cleanDouble(todayEODReport.getSalesSummary().getNetFreight(), 2) + "\t\t" +
                                    cleanDouble(mtdEODReport.getSalesSummary().getNetFreight(), 2)
                    );
                    System.out.println(
                            "\t-Net Interstore:\t" +
                                    cleanDouble(todayEODReport.getSalesSummary().getNetInterStore(), 2) + "\t\t" +
                                    cleanDouble(mtdEODReport.getSalesSummary().getNetInterStore(), 2)
                    );
                    System.out.println(
                            "\t-Net Sales Tax:\t\t" +
                                    cleanDouble(todayEODReport.getSalesSummary().getNetSalesTax(), 2) + "\t\t" +
                                    cleanDouble(mtdEODReport.getSalesSummary().getNetSalesTax(), 2)
                    );
                    System.out.println(
                            "\t NET SALES:\t\t" +
                                    COLOR_CYAN + cleanDouble(todayEODReport.getSalesSummary().getNetSales(), 2)
                                    + "\t\t" +
                                    cleanDouble(mtdEODReport.getSalesSummary().getNetSales(), 2) + COLOR_RESET
                    );
                    System.out.println();
                    System.out.println("SALES TAX SUMMARY:\t\tTODAY\t\tMONTH TO DATE" + COLOR_GREEN);

                    System.out.println(
                            "\tNet Taxable:\t\t" +
                                    cleanDouble(todayEODReport.getSalesTaxSummary().getNetTaxable(), 2) + "\t\t" +
                                    cleanDouble(mtdEODReport.getSalesTaxSummary().getNetTaxable(), 2)

                    );

                    System.out.println(
                            "\tNet Non Taxable:\t" +
                                    cleanDouble(todayEODReport.getSalesTaxSummary().getNetNonTaxable(), 2) + "\t\t" +
                                    cleanDouble(mtdEODReport.getSalesTaxSummary().getNetNonTaxable(), 2)
                    );

                    System.out.println(
                            "\tTotal Tax:\t\t" +
                                    cleanDouble(todayEODReport.getSalesTaxSummary().getTotalSalesTax(), 2) + "\t\t" +
                                    cleanDouble(mtdEODReport.getSalesTaxSummary().getTotalSalesTax(), 2) +
                                    COLOR_RESET
                    );
                    System.out.println("-------------------------------------------------------------");
                }

                boolean hardCopy;
                do {
                    System.out.print("Print hard copy? (Y/N): ");
                    String hc = scanner.nextLine().trim();
                    if (isExitCommand(hc)) {
                        System.out.println("Aborting...");
                        return true;
                    }

                    if (hc.equalsIgnoreCase("Y")) {
                        hardCopy = true;
                        break;
                    } else if (hc.equalsIgnoreCase("N")) {
                        hardCopy = false;
                        break;
                    }
                    printError("Invalid entry, enter \"Y\" or \"N\" only");
                } while (true);


                if (!hardCopy)
                    return true;
                System.out.println("Generating PDF report...");
                if (!PDFGenerator.createEODReport(
                        todayEODReport,
                        mtdEODReport,
                        month,
                        day,
                        year
                )) {
                    printError("Failed to generate PDF report, please contact support");
                    return true;
                }
                printSuccess("Generated PDF Report");
                System.out.println("Printing PDF report...");
                if (PrintUtility.printPDF(Config.TMP_PATH + "eod-temp.pdf")) {
                    printSuccess("PDF to printer");
                    return true;
                }
                printError("Failed to order print for PDF");
                return true;
            }
        }
        return false;
    }

    @Override
    HelpMenu getHelpMenu() {
        return helpMenu;
    }
}
