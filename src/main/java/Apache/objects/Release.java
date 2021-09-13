package Apache.objects;

import java.util.Date;

public abstract class Release implements Transferable {

    private final ReleaseType releaseType;
    private final ReleaseCode releaseCode;
    private final int releaseCodeInt;
    private final Customer customer;

    private final Date date;
    private final int accountingPeriod;

    private final double releaseTotal;

    public Release(
            ReleaseType releaseType,
            int releaseCode,
            Date date,
            int accountingPeriod,
            double releaseTotal,
            Customer customer
    ) {
        switch (releaseCode) {
            case 11 -> this.releaseCode = ReleaseCode.CASH;
            case 12 -> this.releaseCode = ReleaseCode.CHECK;
            case 13 -> this.releaseCode = ReleaseCode.PLASTIC;
            case 31 -> this.releaseCode = ReleaseCode.CHARGE;
            default -> this.releaseCode = ReleaseCode.UNKNOWN;
        }
        this.date = date;
        this.accountingPeriod = accountingPeriod;
        this.releaseType = releaseType;
        this.releaseTotal = releaseTotal;
        this.releaseCodeInt = releaseCode;
        this.customer = customer;
    }

    public ReleaseCode getReleaseCode() {
        return releaseCode;
    }

    public Date getDate() {
        return date;
    }

    public int getAccountingPeriod(){
        return accountingPeriod;
    }

    public ReleaseType getType(){
        return releaseType;
    }

    public double getReleaseTotal(){
        return releaseTotal;
    }

    public int getReleaseCodeInt(){
        return releaseCodeInt;
    }

    public boolean isVoidable(){
        return accountingPeriod == 0;
    }

    public Customer getCustomer(){
        return customer;
    }

}
