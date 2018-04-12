package mike.finance;

import android.content.SharedPreferences;

import java.math.BigDecimal;

public class MathOperations {

    public static String setRightRate(SharedPreferences prefs, String rate) {
        int decimalDigits = prefs.getInt("decimal_digits", 3);
        return new BigDecimal(1).divide(new BigDecimal(rate), decimalDigits, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    public static String calculateExchangeResult(SharedPreferences prefs, String amount, String rate) {
        int decimalDigits = prefs.getInt("decimal_digits", 3);
        return new BigDecimal(amount).multiply(new BigDecimal(rate))
                .setScale(decimalDigits, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
    }
}