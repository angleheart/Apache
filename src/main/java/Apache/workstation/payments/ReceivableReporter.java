package Apache.workstation.payments;

import Apache.objects.Customer;
import Apache.objects.ReceivableReport;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import static Apache.util.General.cleanDouble;

public class ReceivableReporter {

    private static VBox MAIN_BOX;
    private static Label CUSTOMER_NAME_LABEL;
    private static Label P4_LABEL;
    private static Label P3_LABEL;
    private static Label P2_LABEL;
    private static Label P1_LABEL;
    private static Label TOTAL_LABEL;

    static void initiate(
            VBox main,
            Label name,
            Label p1,
            Label p2,
            Label p3,
            Label p4,
            Label total
    ){
        MAIN_BOX = main;
        CUSTOMER_NAME_LABEL = name;
        P1_LABEL = p1;
        P2_LABEL = p2;
        P3_LABEL = p3;
        P4_LABEL = p4;
        TOTAL_LABEL = total;
    }

    static void hide(){
        MAIN_BOX.setOpacity(0);
    }

    static boolean post(Customer customer){
        ReceivableReport report =
                new ReceivableReport(customer.getNumber());

        if(!report.runReceivables()){
            System.out.println("Failed to generate receivable report for customer");
            PaymentError.sendError("Sorry, a Apache.database error occurred");
            return false;
        }

        CUSTOMER_NAME_LABEL.setText(customer.getName());
        P4_LABEL.setText(cleanDouble(report.getDay90Balance(), 2));
        P3_LABEL.setText(cleanDouble(report.getDay60Balance(), 2));
        P2_LABEL.setText(cleanDouble(report.getDay30Balance(), 2));
        P1_LABEL.setText(cleanDouble(report.getCurrentBalance(), 2));
        TOTAL_LABEL.setText(cleanDouble(report.getTotalBalance(), 2));
        if(
                report.getDay90Balance() + report.getDay60Balance() > 0
        )PaymentError.alertPastDue();
        MAIN_BOX.setOpacity(1);
        return true;
    }

}
