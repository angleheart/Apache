package Apache.util;

import Apache.workstation.pos.CalculatorPricingOption;
import Apache.objects.Transferable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class General {

    public static String cleanDouble(double d, int places) {
        DecimalFormat format;
        if (places == 3)
            format = new DecimalFormat("0.000");
        else if (places == 2)
            format = new DecimalFormat("0.00");
        else if (places == 1)
            format = new DecimalFormat("0.0");
        else
            format = new DecimalFormat("0");
        return format.format(d);
    }

    public static List<Transferable> getCalculatorOptions(double cost) {

        List<Transferable> pricingOptions = new ArrayList<>();

        pricingOptions.add(new CalculatorPricingOption(
                "1.35",
                cleanDouble(cost * 1.35, 3)
        ));

        pricingOptions.add(new CalculatorPricingOption(
                "1.43",
                cleanDouble(cost * 1.43, 3)
        ));

        pricingOptions.add(new CalculatorPricingOption(
                "1.54",
                cleanDouble(cost * 1.54, 3)
        ));

        pricingOptions.add(new CalculatorPricingOption(
                "1.67",
                cleanDouble(cost * 1.67, 3)
        ));

        return pricingOptions;
    }


}
