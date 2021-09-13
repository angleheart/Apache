package Apache.workstation.pos;

import Apache.objects.Transferable;

public class CalculatorPricingOption implements Transferable {

    private final String multiplier;
    private final String price;

    public CalculatorPricingOption(String multiplier, String price){
        this.multiplier = multiplier;
        this.price = price;
    }

    @Override
    public String getSelectableName() {
        return multiplier + "\t" + "$" + price;
    }

    public String getMultiplier() {
        return multiplier;
    }

    public String getPrice() {
        return price;
    }

}
