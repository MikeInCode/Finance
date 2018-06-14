package mike.finance.exchange;

import android.view.MenuItem;
import android.widget.TextView;

public interface IExchangePresenter {
    void refreshData(String initialCode, String targetCode, String amount);
    void onButtonClicked(int id, TextView initialCode, TextView targetCode, TextView exchangeAmount);
    void onOptionsMenuButtonClicked(MenuItem item, TextView initialCode, TextView targetCode, TextView amount);
    void getDialogCurrencyList(TextView textView);
    void onDestroy();
}
