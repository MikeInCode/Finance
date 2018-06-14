package mike.finance.rates;

import android.view.MenuItem;
import android.widget.ImageButton;

import java.util.List;

import mike.finance.CurrencyInformation;
import mike.finance.IErrorHandler;

public interface IRatesView extends IErrorHandler {
    void displayResult(List<CurrencyInformation> list);
    void hideRefreshingStatus();
    void goToSettings();
    void setMenuItemChecked(MenuItem item, boolean value);
    void buildDialogCurrencyList(List<CurrencyInformation> list);
    void showFavoriteStatus(ImageButton btn);
    void hideFavoriteStatus(ImageButton btn);
    void showEmptyFavoritesView();
    void hideEmptyFavoritesView();
    void showDataRefreshedToast();
}
