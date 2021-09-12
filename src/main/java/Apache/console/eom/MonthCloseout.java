package Apache.console.eom;

import java.util.List;

public class MonthCloseout {

    private final List<CustomerStatement> customerStatements;

    public MonthCloseout() {
        this.customerStatements = null;
    }

    public MonthCloseout(List<CustomerStatement> customerStatements) {
        this.customerStatements = customerStatements;
    }




    public boolean prepareReceivableShift() {
//        try {
//            statement.addBatch(
//                    "UPDATE Invoices SET AccountingPeriod = 4 WHERE AccountingPeriod = 3;"
//            );
//            statement.addBatch(
//                    "UPDATE Invoices SET AccountingPeriod = 3 WHERE AccountingPeriod = 2;"
//            );
//            statement.addBatch(
//                    "UPDATE Invoices SET AccountingPeriod = 2 WHERE AccountingPeriod = 1;"
//            );
//            statement.addBatch(
//                    "UPDATE Payments SET AccountingPeriod = 4 WHERE AccountingPeriod = 3;"
//            );
//            statement.addBatch(
//                    "UPDATE Payments SET AccountingPeriod = 3 WHERE AccountingPeriod = 2;"
//            );
//            statement.addBatch(
//                    "UPDATE Payments SET AccountingPeriod = 2 WHERE AccountingPeriod = 1;"
//            );
//            return true;
//        } catch (SQLException sqlException) {
//            sqlException.printStackTrace();
//            return false;
//        }
        return false;
    }

//    public boolean commitCloseout() {
//        try {
//            System.out.println(statement);
//
//            statement.executeBatch();
//            return true;
//        } catch (SQLException sqlException) {
//            sqlException.printStackTrace();
//            try {
//                System.out.println("Attempting rollback");
//                connection.rollback();
//                System.out.println("Rollback success");
//                return false;
//            } catch (SQLException sqlException1) {
//                sqlException1.printStackTrace();
//                System.out.println("Rollback failed");
//                return false;
//            }
//        }
//    }

}
