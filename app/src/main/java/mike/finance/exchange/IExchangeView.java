package mike.finance.exchange;

import android.widget.TextView;

import java.util.List;

import mike.finance.CurrencyInformation;
import mike.finance.IErrorHandler;

public interface IExchangeView extends IErrorHandler {
    void displayResult(CurrencyInformation initialCurrency, CurrencyInformation targetCurrency);
    void goToSettings();
    void showClearFields();
    void showDot(String amount);
    void showDataRefreshedToast();
    void buildDialogCurrencyList(List<CurrencyInformation> list, TextView textView);
}
