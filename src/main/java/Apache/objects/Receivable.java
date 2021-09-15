package Apache.objects;

public class Receivable {

    private final double curr;
    private final double days30;
    private final double days60;
    private final double days90;
    private final double paid;

    public Receivable(
            double curr,
            double days30,
            double days60,
            double days90,
            double paid
    ){
        this.curr = curr;
        this.days30 = days30;
        this.days60 = days60;
        this.days90 = days90;
        this.paid = paid;
    }

    public double getCurr() {
        return curr;
    }

    public double getDays30() {
        return days30;
    }

    public double getDays60() {
        return days60;
    }

    public double getDays90() {
        return days90;
    }

    public double getPaid(){
        return paid;
    }

    public double getTotal(){
        return curr + days30 + days60 + days90;
    }

}
