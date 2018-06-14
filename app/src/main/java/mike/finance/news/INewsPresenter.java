package mike.finance.news;

import android.view.MenuItem;

import mike.finance.SharedPreferencesAccessor;

public interface INewsPresenter {
    void refreshData();
    void startReading(String url);
    void onOptionsMenuButtonClicked(SharedPreferencesAccessor prefs, MenuItem item);
    void onDestroy();
}
