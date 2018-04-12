package mike.finance;

public class CurrencyAPI {

    private static String Link = "https://v3.exchangerate-api.com/";
    private static String Key = "de816dcd9dc77c6cb298060e";

    public static String getRatesLink() {
        return Link + "bulk/" + Key + "/";
    }

    public static String getExchangeLink() {
        return Link + "pair/" + Key + "/";
    }
}