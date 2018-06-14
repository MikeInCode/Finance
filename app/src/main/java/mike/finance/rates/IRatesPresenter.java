package mike.finance.rates;

import android.view.MenuItem;
import android.widget.ImageButton;

import mike.finance.SharedPreferencesAccessor;

public interface IRatesPresenter {
    void refreshData();
    void addToFavorites(SharedPreferencesAccessor prefs, ImageButton btn, String currencyCode);
    void onOptionsMenuButtonClicked(SharedPreferencesAccessor prefs, MenuItem item);
    void getDialogCurrencyList();
    void onDestroy();
}
