package Apache.util;

public class InputVerifier {

    public static boolean verifyCore(String candidate) {
        return candidate.equalsIgnoreCase("Y") || candidate.equalsIgnoreCase("N");
    }

    public static boolean verifyStockQuantity(String candidate) {
        try {
            int stock = Integer.parseInt(candidate);
            if (stock < 0)
                return false;
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean verifyAvailQty(String candidate) {
        try {
            Integer.parseInt(candidate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean verifyQty(String candidate, String transCode) {
        try {
            int qty = Integer.parseInt(candidate);
            if (qty == 0)
                return false;
            if (transCode.equalsIgnoreCase("RET"))
                return qty > 0;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static boolean verifyMfg(String candidate) {
        return candidate.length() == 3 && candidate.charAt(2) != '9';
    }

    public static boolean verifyPartNumber(String candidate) {
        if (candidate.length() < 1)
            return false;
        if (candidate.length() > 50)
            return false;
        return preventInjection(candidate);
    }

    public static boolean verifyShipTo(String candidate) {
        if (candidate.length() > 50)
            return false;
        return preventInjection(candidate);
    }

    public static boolean verifyDescription(String candidate) {
        if (candidate.length() > 50)
            return false;
        return preventInjection(candidate);
    }

    public static boolean verifyDocumentDetail(String candidate){
        return preventInjection(candidate) && candidate.length() < 25;
    }

    public static boolean verifyPrice(String candidate) {
        try {
            double price = Double.parseDouble(candidate);
            return price > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static boolean verifyTx(String candidate) {
        return candidate.equalsIgnoreCase("T") ||
                candidate.equalsIgnoreCase("N");
    }


    public static boolean verifyPO(String candidate) {
        if (candidate.length() > 15)
            return false;
        return preventInjection(candidate);
    }


    public static boolean verifyCustomerNumber(String candidate) {
        if(candidate.equalsIgnoreCase("DEALER"))
            return true;
        if (candidate.length() != 3 && !candidate.equalsIgnoreCase("1001"))
            return false;
        return isNumeric(candidate);
    }


    public static boolean verifyCustomerName(String candidate) {
        if (candidate.length() > 30 || candidate.length() < 3)
            return false;
        return preventInjection(candidate);
    }

    public static boolean verifyCustomerRequest(String candidate){
        return isAlphaNumeric(candidate);
    }

    public static boolean verifyPhoneNumber(String candidate) {
        candidate = candidate.replaceAll("[^0-9]", "");
        return candidate.length() == 10 || candidate.length() == 7;
    }


    public static boolean verifyCost(String candidate){
        try{
            double d = Double.parseDouble(candidate);
            if(d == 0)
                return true;

        }catch(Exception e){
            return false;
        }
        return verifyPrice(candidate);
    }

    public static boolean verifyAddress(String candidate) {
        if (!candidate.contains(" "))
            return false;
        if (candidate.length() > 50)
            return false;
        return preventInjection(candidate);
    }


    public static boolean verifyCity(String candidate) {
        if (candidate.length() > 30)
            return false;
        return preventInjection(candidate);
    }


    public static boolean verifyState(String candidate) {
        return candidate.length() == 2 && isAlphabetic(candidate);
    }


    public static boolean verifyZip(String candidate) {
        return isNumeric(candidate) && candidate.length() == 5;
    }

    public static boolean verifyPriceMultiplier(String candidate) {
        try {
            double d = Double.parseDouble(candidate);
            return d < 9 && d >= 1;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean verifyCounterPersonNumber(String candidate) {
        try {
            int counterPersonNumber = Integer.parseInt(candidate);
            return counterPersonNumber > 0 && counterPersonNumber < 10;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean verifyCounterPersonName(String candidate) {
        return isAlphabetic(candidate) && candidate.length() < 15 && candidate.length() > 1;
    }

    public static boolean verifyAllInvoiceSearch(String candidate) {
        if (candidate.startsWith("D"))
            candidate = candidate.substring(1);
        return isNumeric(candidate);
    }

    public static boolean verifyOpenInvoiceSearch(String candidate) {
        if (!candidate.startsWith("D"))
            return false;
        return isNumeric(candidate.substring(1));
    }

    private static boolean isAlphabetic(String candidate) {
        return candidate.chars().allMatch(Character::isAlphabetic);
    }


    public static boolean isNumeric(String candidate) {
        return candidate.chars().allMatch(Character::isDigit);
    }


    private static boolean isAlphaNumeric(String candidate) {
        return candidate.chars().allMatch(Character::isLetterOrDigit);
    }

    public static boolean preventInjection(String candidate) {
        String finalized = candidate.replaceAll("[^a-zA-Z0-9\\/\\#\\.\\&\\-\\s]", "");
        return finalized.length() == candidate.length();
    }

    public static boolean verifyReleaseCode(String candidate) {
        return candidate.equalsIgnoreCase("31") ||
                candidate.equalsIgnoreCase("11") ||
                candidate.equalsIgnoreCase("12") ||
                candidate.equalsIgnoreCase("13");
    }

    public static boolean verifyInvoiceNumber(String candidate){
        if(candidate.startsWith("D"))
            candidate = candidate.substring(1);
        return isNumeric(candidate);
    }

    public static boolean verifyPaymentAmount(String candidate){
        try{
            double d = Double.parseDouble(candidate);
            return d >= 0;
        }catch(Exception e){
            return false;
        }
    }

}
