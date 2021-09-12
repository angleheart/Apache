package Apache.util;

import java.util.Locale;

public class InputRefiner {


    public static String capitalizeFirstLetters(String in){
        in = in.toLowerCase(Locale.ROOT);
        String[] nameArray = in.split(" ");
        String refinedName = "";
        for(String str : nameArray){
            refinedName = refinedName.concat(str.substring(0,1).toUpperCase(Locale.ROOT).concat(str.substring(1)));
            refinedName = refinedName.concat(" ");
        }
        return refinedName.trim();
    }

    public static String refinePhoneNumber(String in){
        in = in.replaceAll("[^0-9]", "");
        char[] arr;
        if(in.length() == 10){
            arr = new char[14];
            arr[0] = '(';
            arr[1] = in.charAt(0);
            arr[2] = in.charAt(1);
            arr[3] = in.charAt(2);
            arr[4] = ')';
            arr[5] = ' ';
            arr[6] = in.charAt(3);
            arr[7] = in.charAt(4);
            arr[8] = in.charAt(5);
            arr[9] = '-';
            arr[10] = in.charAt(6);
            arr[11] = in.charAt(7);
            arr[12] = in.charAt(8);
            arr[13] = in.charAt(9);

        }else{
            arr = new char[8];
            arr[0] = in.charAt(0);
            arr[1] = in.charAt(1);
            arr[2] = in.charAt(2);
            arr[3] = '-';
            arr[4] = in.charAt(3);
            arr[5] = in.charAt(4);
            arr[6] = in.charAt(5);
            arr[7] = in.charAt(6);
        }
        return String.valueOf(arr);
    }


}
