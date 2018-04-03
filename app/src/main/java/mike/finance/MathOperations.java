package mike.finance;

import java.math.BigDecimal;
import java.math.MathContext;

public class MathOperations {

    //public static String roundRate(String value) {
    //    return new BigDecimal(value).setScale(3, BigDecimal.ROUND_HALF_UP).toString();
    //}

    public static String buildCurrencyRate(String value) {
        return new BigDecimal(1).divide(new BigDecimal(value), 3, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    public static String calculateExchangeResult(String value, String value2) {
        return new BigDecimal(value).multiply(new BigDecimal(value2))
                .setScale(3, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
    }
}
