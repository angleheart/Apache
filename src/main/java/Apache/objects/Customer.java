package Apache.objects;

import java.util.Locale;

public class Customer implements Selectable {

    private final String number;
    private final String name;
    private final String address;
    private final String city;
    private final String state;
    private final String zip;
    private final String phone;
    private final double priceMultiplier;
    private final boolean taxable;
    private final boolean payByInvoice;

    public Customer(
            String number,
            String name,
            String address,
            String city,
            String state,
            String zip,
            String phone,
            double priceMultiplier,
            boolean taxable,
            boolean payByInvoice
    ) {
        this.number = number;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
        this.priceMultiplier = priceMultiplier;
        this.taxable = taxable;
        this.payByInvoice = payByInvoice;
    }

    public String getCityStateZip() {
        StringBuilder str = new StringBuilder();
        if (!city.equalsIgnoreCase(""))
            str.append(city);
        if (!state.equalsIgnoreCase("") || !zip.equalsIgnoreCase(""))
            str.append(",");
        if (!state.equalsIgnoreCase(""))
            str.append(" ").append(state);
        if (!zip.equalsIgnoreCase(""))
            str.append(" ").append(zip);
        return str.toString();
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getPhone() {
        return phone;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public boolean paysByInvoice() {
        return payByInvoice;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(number).append("\n");
        str.append(name).append("\n");
        str.append(address).append("\n");
        str.append(getCityStateZip()).append("\n");
        str.append(phone).append("\n");
        str.append(priceMultiplier).append("\n");
        if (taxable)
            str.append("Taxable").append("\n");
        else
            str.append("Non-Taxable").append("\n");
        if (payByInvoice)
            str.append("Pay By Invoice");
        else
            str.append("Balance Forward");
        return str.toString();
    }

    @Override
    public String getSelectableName() {
        return number.toUpperCase(Locale.ROOT) + " - " + name.toUpperCase(Locale.ROOT);
    }

}
