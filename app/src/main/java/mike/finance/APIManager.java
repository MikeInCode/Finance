package mike.finance;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class APIManager {

    private final String CURRENCY_API_LINK = "https://v3.exchangerate-api.com";
    private final String CURRENCY_API_KEY = "de816dcd9dc77c6cb298060e";
    private final String NEWS_API_LINK = "https://newsapi.org";
    private final String NEWS_API_KEY = "d77d193ac48b477a92222cd1df5afdd7";
    private SharedPreferencesAccessor prefs;

    public APIManager(Context context) {
        prefs = new SharedPreferencesAccessor(context);
    }

    public String getRatesLink(String baseCurrency) {
        return CURRENCY_API_LINK + "/bulk/" + CURRENCY_API_KEY + "/" + baseCurrency;
    }

    public String getExchangeLink(String initialCurrency, String targetCurrency) {
        return CURRENCY_API_LINK + "/pair/" + CURRENCY_API_KEY + "/" +
                initialCurrency + "/" + targetCurrency;
    }

    public String getNewsLink() {
        return generateNewsLink();
    }

    private String generateNewsLink() {
        StringBuilder keyWord;
        StringBuilder datePeriod;
        StringBuilder sortingType;

        keyWord = new StringBuilder("q=" + prefs.getNewsSearchingKeywordValue());

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        switch (prefs.getNewsPeriodValue()) {
            case "1":
                datePeriod = new StringBuilder("from=" + DateTime.now().toString(format) + "&to=" +
                        DateTime.now().toString(format));
                break;
            case "3":
                datePeriod = new StringBuilder("from=" + DateTime.now().minus(2).toString(format) + "&to=" +
                        DateTime.now().toString(format));
                break;
            case "7":
                datePeriod = new StringBuilder("from=" + DateTime.now().minus(6).toString(format) + "&to=" +
                        DateTime.now().toString(format));
                break;
            default:
                datePeriod = new StringBuilder("from=" + DateTime.now().minus(2).toString(format) + "&to=" +
                        DateTime.now().toString(format));
        }

        if (prefs.getNewsSortingTypeValue()) {
            sortingType = new StringBuilder("sortBy=publishedAt");
        } else {
            sortingType = new StringBuilder("sortBy=popularity");
        }

        return NEWS_API_LINK + "/v2/everything?language=en&" + keyWord + "&" + datePeriod + "&"
                + sortingType + "&apiKey=" + NEWS_API_KEY;
    }
}