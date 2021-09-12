package Apache.console.eom;

import Apache.objects.Customer;
import Apache.objects.ReceivableReport;
import java.util.Date;
import java.util.List;

public class CustomerStatement {

    private Customer customer;
    private Date date;
    private double balCurr;
    private double bal30;
    private double bal60;
    private double bal90;
    private double totalPaid;
    private List<StatementLine> statementLines;

    public CustomerStatement(
            Customer customer,
            Date date,
            double balCurr,
            double bal30,
            double bal60,
            double bal90,
            double totalPaid,
            List<StatementLine> lines
    ) {
        this.customer = customer;
        this.date = date;
        this.balCurr = balCurr;
        this.bal30 = bal30;
        this.bal60 = bal60;
        this.bal90 = bal90;
        this.totalPaid = totalPaid;
        this.statementLines = lines;
    }

    public CustomerStatement(Customer customer) {
        this.customer = customer;
    }

    public boolean generate() {
        date = new Date();
        ReceivableReport receivableReport = new ReceivableReport(customer.getNumber());
        if (!receivableReport.runReceivables())
            return false;
        balCurr = receivableReport.getCurrentBalance();
        bal30 = receivableReport.getDay30Balance();
        bal60 = receivableReport.getDay60Balance();
        bal90 = receivableReport.getDay90Balance();
        totalPaid = receivableReport.getTotalPaidThisMonth();
        statementLines = StatementGenerator.getStatementLines(customer);
        return statementLines != null;
    }


    public Customer getCustomer() {
        return customer;
    }

    public Date getDate() {
        return date;
    }

    public double getBalCurr() {
        return balCurr;
    }

    public double getBal30() {
        return bal30;
    }

    public double getBal60() {
        return bal60;
    }

    public double getBal90() {
        return bal90;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public double getTotalBalance() {
        return
                balCurr +
                        bal30 +
                        bal60 +
                        bal90;

    }

    public List<StatementLine> getStatementLines() {
        return statementLines;
    }

}

