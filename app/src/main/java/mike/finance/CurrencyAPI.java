package mike.finance;

public class CurrencyAPI {

    private static String Link = "https://v3.exchangerate-api.com/";
    private static String Key = "de816dcd9dc77c6cb298060e";

    public static String getRatesLink(String baseCurrency) {
        return Link + "bulk/" + Key + "/" + baseCurrency;
    }

    public static String getExchangeLink(String initialCurrency, String targetCurrency) {
        return Link + "pair/" + Key + "/" + initialCurrency + "/" + targetCurrency;
    }
}