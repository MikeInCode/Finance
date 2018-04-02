package mike.finance;

import java.text.DecimalFormat;

public class MathOperations {

    public static String round(String value) {
        if (value.contains(".")) {
            String[] numberSplit = value.split("\\.");
            Double buf = Double.parseDouble(numberSplit[1]);
            if (buf == 0) {
                return numberSplit[0];
            } else {
                buf = Double.parseDouble(value);
                DecimalFormat df = new DecimalFormat("#.###");
                buf = Double.valueOf(df.format(buf));
                return buf.toString();
            }
        } else {
            return value;
        }
    }

    public static String buildCurrencyRate(String value) {
        Double buf = Double.parseDouble(value);
        Double result = 1.0 / buf;
        return result.toString();
    }
}
