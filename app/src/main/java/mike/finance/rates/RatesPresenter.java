package mike.finance.rates;


import android.view.MenuItem;
import android.widget.ImageButton;

import java.util.List;

import mike.finance.CurrencyInformation;
import mike.finance.DataManager;
import mike.finance.R;
import mike.finance.SharedPreferencesAccessor;

public class RatesPresenter implements IRatesPresenter {

    private IRatesView ratesView;
    private DataManager dataManager;

    public RatesPresenter(IRatesView view, DataManager dataManager) {
        this.ratesView = view;
        this.dataManager = dataManager;
    }

    @Override
    public void refreshData() {
        dataManager.getRatesData(new DataManager.RatesCallback() {
            @Override
            public void onSuccessfulRequest(List<CurrencyInformation> list) {
                ratesView.displayResult(list);
                ratesView.hideAllRequestErrorViews();
                ratesView.hideRefreshingStatus();
                if (list.isEmpty()) {
                    ratesView.showEmptyFavoritesView();
                } else {
                    ratesView.hideEmptyFavoritesView();
                }
            }

            @Override
            public void onNoInternetError() {
                ratesView.showNoInternetErrorView();
                ratesView.hideRefreshingStatus();
            }

            @Override
            public void onServerSideError() {
                ratesView.showServerSideErrorView();
                ratesView.hideRefreshingStatus();
            }
        });
    }

    @Override
    public void addToFavorites(SharedPreferencesAccessor prefs, ImageButton btn, String currencyCode) {
        if (!prefs.isFavorite(currencyCode)) {
            prefs.setFavorite(currencyCode, true);
            ratesView.showFavoriteStatus(btn);
        } else {
            prefs.setFavorite(currencyCode, false);
            ratesView.hideFavoriteStatus(btn);
        }
    }

    @Override
    public void onOptionsMenuButtonClicked(SharedPreferencesAccessor prefs, MenuItem item) {
        int ratesSortType = prefs.getRatesSortingTypeValue();
        switch (item.getItemId()) {
            case R.id.base_currency_btn:
                getDialogCurrencyList();
                break;
            case R.id.refresh_btn:
                refreshData();
                ratesView.showDataRefreshedToast();
                break;
            case R.id.name_natural:
                if (ratesSortType != 0) {
                    prefs.setRatesSortingTypeValue(0);
                    refreshData();
                    ratesView.setMenuItemChecked(item, true);
                }
                break;
            case R.id.name_reverse:
                if (ratesSortType != 1) {
                    prefs.setRatesSortingTypeValue(1);
                    refreshData();
                    ratesView.setMenuItemChecked(item, true);
                }
                break;
            case R.id.value_natural:
                if (ratesSortType != 2) {
                    prefs.setRatesSortingTypeValue(2);
                    refreshData();
                    ratesView.setMenuItemChecked(item, true);
                }
                break;
            case R.id.value_reverse:
                if (ratesSortType != 3) {
                    prefs.setRatesSortingTypeValue(3);
                    refreshData();
                    ratesView.setMenuItemChecked(item, true);
                }
                break;
            case R.id.show_only_favorites_btn:
                if (item.isChecked()) {
                    prefs.setShowFavoritesValue(false);
                    ratesView.setMenuItemChecked(item, false);
                } else {
                    prefs.setShowFavoritesValue(true);
                    ratesView.setMenuItemChecked(item, true);
                }
                refreshData();
                break;
            case R.id.settings_btn:
                ratesView.goToSettings();
                break;
        }
    }

    @Override
    public void getDialogCurrencyList() {
        List<CurrencyInformation> dialogCurrencyList = dataManager.getDialogCurrencyList();
        if (!dialogCurrencyList.isEmpty()) {
            ratesView.buildDialogCurrencyList(dialogCurrencyList);
        } else {
            ratesView.showSystemErrorToast();
        }
    }

    @Override
    public void onDestroy() {
        ratesView = null;
    }
}
