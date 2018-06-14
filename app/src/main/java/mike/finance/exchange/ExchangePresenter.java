package mike.finance.exchange;


import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import mike.finance.CurrencyInformation;
import mike.finance.DataManager;
import mike.finance.R;

public class ExchangePresenter implements IExchangePresenter {

    private IExchangeView exchangeView;
    private DataManager dataManager;

    public ExchangePresenter(IExchangeView view, DataManager dataManager) {
        this.exchangeView = view;
        this.dataManager = dataManager;
    }

    @Override
    public void refreshData(String initialCode, String targetCode, String amount) {
        dataManager.getExchangeData(initialCode, targetCode, amount, new DataManager.ExchangeCallback() {
            @Override
            public void onSuccessfulRequest(CurrencyInformation initialCurrency, CurrencyInformation targetCurrency) {
                exchangeView.displayResult(initialCurrency, targetCurrency);
                exchangeView.hideAllRequestErrorViews();
            }

            @Override
            public void onNoInternetError() {
                exchangeView.showNoInternetErrorView();
            }

            @Override
            public void onServerSideError() {
                exchangeView.showServerSideErrorView();
            }
        });
    }

    @Override
    public void onButtonClicked(int id, TextView initialCode, TextView targetCode,
                                TextView exchangeAmount) {
        String currentAmount = exchangeAmount.getText().toString();
        switch (id) {
            case R.id.initial_currency_code:
                getDialogCurrencyList(initialCode);
                break;
            case R.id.target_currency_code:
                getDialogCurrencyList(targetCode);
                break;
            case R.id.exchange_switcher:
                refreshData(targetCode.getText().toString(), initialCode.getText().toString(), currentAmount);
                break;
            case R.id.zero:
                currentAmount = generateNewNumber("0", currentAmount);
                if (!currentAmount.equals("0")) {
                    refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                }
                break;
            case R.id.one:
                currentAmount = generateNewNumber("1", currentAmount);
                refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                break;
            case R.id.two:
                currentAmount = generateNewNumber("2", currentAmount);
                refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                break;
            case R.id.three:
                currentAmount = generateNewNumber("3", currentAmount);
                refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                break;
            case R.id.four:
                currentAmount = generateNewNumber("4", currentAmount);
                refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                break;
            case R.id.five:
                currentAmount = generateNewNumber("5", currentAmount);
                refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                break;
            case R.id.six:
                currentAmount = generateNewNumber("6", currentAmount);
                refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                break;
            case R.id.seven:
                currentAmount = generateNewNumber("7", currentAmount);
                refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                break;
            case R.id.eight:
                currentAmount = generateNewNumber("8", currentAmount);
                refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                break;
            case R.id.nine:
                currentAmount = generateNewNumber("9", currentAmount);
                refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                break;
            case R.id.dot:
                if (checkFieldLength(currentAmount)) {
                    if (!currentAmount.contains(".")) {
                        currentAmount += ".";
                        exchangeView.showDot(currentAmount);
                    }
                } else {
                    exchangeView.showSystemErrorToast();
                }
                break;
            case R.id.clear_last:
                if (currentAmount.length() != 0) {
                    currentAmount = currentAmount.substring(0, currentAmount.length() - 1);
                    if (currentAmount.length() != 0) {
                        refreshData(initialCode.getText().toString(), targetCode.getText().toString(), currentAmount);
                    } else {
                        clearFields();
                    }
                }
                break;
            case R.id.clear_all:
                clearFields();
                break;
        }
    }

    private String generateNewNumber(String value, String currentAmount) {
        String newNumber = currentAmount;
        if (checkFieldLength(newNumber)) {
            if (newNumber.equals("0")) {
                newNumber = value;
            } else {
                newNumber += value;
            }
        } else {
            exchangeView.showSystemErrorToast();
        }
        return newNumber;
    }

    private boolean checkFieldLength(String currentAmount) {
        return currentAmount.length() < 9;
    }

    private void clearFields() {
        exchangeView.showClearFields();
    }

    @Override
    public void onOptionsMenuButtonClicked(MenuItem item, TextView initialCode, TextView targetCode, TextView amount) {
        switch (item.getItemId()) {
            case R.id.refresh_btn:
                refreshData(initialCode.getText().toString(), targetCode.getText().toString(),
                        amount.getText().toString());
                exchangeView.showDataRefreshedToast();
                break;
            case R.id.switch_btn:
                refreshData(targetCode.getText().toString(), initialCode.getText().toString(),
                        amount.getText().toString());
                break;
            case R.id.settings_btn:
                exchangeView.goToSettings();
                break;
        }
    }

    @Override
    public void getDialogCurrencyList(TextView textView) {
        List<CurrencyInformation> dialogCurrencyList = dataManager.getDialogCurrencyList();
        if (!dialogCurrencyList.isEmpty()) {
            exchangeView.buildDialogCurrencyList(dialogCurrencyList, textView);
        }
    }

    @Override
    public void onDestroy() {
        exchangeView = null;
    }
}
