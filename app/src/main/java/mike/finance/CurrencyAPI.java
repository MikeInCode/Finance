package mike.finance;

public class CurrencyAPI {

    private static String Link = "https://v3.exchangerate-api.com/";
    private static String Key = "eea141b9e02d415609d257a1/";

    public static String getRatesLink(String toCurrency){
        return Link + "bulk/" + Key + toCurrency;
    }

    public static String getExchangeLink(String fromCurrency, String toCurrency){
        return Link + "pair/" + Key + fromCurrency + "/" + toCurrency;
    }
}