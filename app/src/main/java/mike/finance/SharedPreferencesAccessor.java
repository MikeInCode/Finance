package mike.finance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesAccessor {

    private final String BASE_CURRENCY = "base_currency";
    private final String SHOW_FAVORITES = "show_favorites";
    private final String IS_FAVORITE = "_is_favorite";
    private final String RATES_SORTING_TYPE = "rates_sorting_type";
    private final String NEWS_SORTING_TYPE = "news_sorting_type";
    private final String AUTO_REFRESH = "auto_refresh";
    private final String NUMBER_PRECISION = "number_precision";
    private final String NEWS_PERIOD = "news_period";
    private final String NEWS_SEARCHING_KEYWORD = "news_searching_keyword";
    private final String NEWS_LANGUAGE = "news_language";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesAccessor(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getBaseCurrencyValue() {
        return sharedPreferences.getString(BASE_CURRENCY, "UAH");
    }

    public void setBaseCurrencyValue(String newValue) {
        sharedPreferences.edit().putString(BASE_CURRENCY, newValue).apply();
    }

    public boolean getShowFavoritesValue() {
        return sharedPreferences.getBoolean(SHOW_FAVORITES, false);
    }

    public void setShowFavoritesValue(boolean newValue) {
        sharedPreferences.edit().putBoolean(SHOW_FAVORITES, newValue).apply();
    }

    public boolean isFavorite(String currencyCode) {
        return sharedPreferences.getBoolean(currencyCode + IS_FAVORITE, false);
    }

    public void setFavorite(String currencyCode, boolean isFavorite) {
        sharedPreferences.edit().putBoolean(currencyCode + IS_FAVORITE, isFavorite).apply();
    }

    public int getRatesSortingTypeValue() {
        return sharedPreferences.getInt(RATES_SORTING_TYPE, 0);
    }

    public void setRatesSortingTypeValue(int newValue) {
        sharedPreferences.edit().putInt(RATES_SORTING_TYPE, newValue).apply();
    }

    public boolean getNewsSortingTypeValue() {
        return sharedPreferences.getBoolean(NEWS_SORTING_TYPE, true);
    }

    public void setNewsSortingTypeValue(boolean newValue) {
        sharedPreferences.edit().putBoolean(NEWS_SORTING_TYPE, newValue).apply();
    }

    public String getAutoRefreshValue() {
        return sharedPreferences.getString(AUTO_REFRESH, "off");
    }

    public String getNumberPrecisionValue() {
        return sharedPreferences.getString(NUMBER_PRECISION, "3");
    }

    public String getNewsPeriodValue() {
        return sharedPreferences.getString(NEWS_PERIOD, "3");
    }

    public String getNewsSearchingKeywordValue() {
        return sharedPreferences.getString(NEWS_SEARCHING_KEYWORD, "finance");
    }
}
