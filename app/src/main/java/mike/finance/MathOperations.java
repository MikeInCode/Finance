package mike.finance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.math.BigDecimal;

public class MathOperations {

    public static String setRightRate(Context context, String rate) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int decimalDigits = preferences.getInt(context.getString(R.string.decimal_digits), 3);
        return new BigDecimal(1).divide(new BigDecimal(rate), decimalDigits, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    public static String calculateExchangeResult(Context context, String amount, String rate) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int decimalDigits = preferences.getInt(context.getString(R.string.decimal_digits), 3);
        return new BigDecimal(amount).multiply(new BigDecimal(rate))
                .setScale(decimalDigits, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
    }
}