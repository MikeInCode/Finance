package mike.finance.exchange;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import mike.finance.CurrencyInformation;
import mike.finance.DataManager;
import mike.finance.DialogListAdapter;
import mike.finance.R;
import mike.finance.SettingsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExchangeFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.initial_currency_code) TextView initialCurrencyCode;
    @BindView(R.id.target_currency_code) TextView targetCurrencyCode;
    @BindView(R.id.exchange_amount) TextView exchangeAmount;
    @BindView(R.id.exchange_result) TextView exchangeResult;
    @BindView(R.id.exchange_switcher) ImageButton exchangeSwitcher;
    @BindView(R.id.zero) Button zeroBtn;
    @BindView(R.id.one) Button oneBtn;
    @BindView(R.id.two) Button twoBtn;
    @BindView(R.id.three) Button threeBtn;
    @BindView(R.id.four) Button fourBtn;
    @BindView(R.id.five) Button fiveBtn;
    @BindView(R.id.six) Button sixBtn;
    @BindView(R.id.seven) Button sevenBtn;
    @BindView(R.id.eight) Button eightBtn;
    @BindView(R.id.nine) Button nineBtn;
    @BindView(R.id.dot) Button dotBtn;
    @BindView(R.id.clear_last) Button clearLastBtn;
    @BindView(R.id.clear_all) Button clearAllBtn;

    @BindString(R.string.base_currency) String baseCurrencyKey;
    @BindString(R.string.auto_refresh) String autoRefreshKey;

    private DataManager dataManager;
    private String initialCurrency;
    private String targetCurrency;
    private StringBuilder exchangeAmountValue;
    private boolean isFirstRun = true;
    private Handler handler;
    private Runnable runnable;
    private int refreshInterval;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (isFirstRun) {
            initialCurrency = "USD";
            targetCurrency = preferences.getString(baseCurrencyKey, "UAH");
            exchangeAmountValue = new StringBuilder("1");
            isFirstRun = false;
        }
        initialCurrencyCode.setText(initialCurrency);
        targetCurrencyCode.setText(targetCurrency);
        exchangeAmount.setText(exchangeAmountValue);

        dataManager = (DataManager) getActivity().getIntent().getSerializableExtra("data_manager");
        dataManager.getRefreshedExchangeResult();

        initialCurrencyCode.setOnClickListener(this);
        targetCurrencyCode.setOnClickListener(this);
        exchangeSwitcher.setOnClickListener(this);
        zeroBtn.setOnClickListener(this);
        oneBtn.setOnClickListener(this);
        twoBtn.setOnClickListener(this);
        threeBtn.setOnClickListener(this);
        fourBtn.setOnClickListener(this);
        fiveBtn.setOnClickListener(this);
        sixBtn.setOnClickListener(this);
        sevenBtn.setOnClickListener(this);
        eightBtn.setOnClickListener(this);
        nineBtn.setOnClickListener(this);
        dotBtn.setOnClickListener(this);
        clearLastBtn.setOnClickListener(this);
        clearAllBtn.setOnClickListener(this);

        if (!preferences.getString(autoRefreshKey, "off").equals("off")) {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    dataManager.getRefreshedExchangeResult();
                    Toast.makeText(getContext(), "Data refreshed!", Toast.LENGTH_SHORT).show();
                    startAutoRefresh();
                }
            };
            refreshInterval = Integer.valueOf(preferences.getString(autoRefreshKey, "off"));
            startAutoRefresh();
        }
    }

    private void startAutoRefresh() {
        handler.postDelayed(runnable, refreshInterval);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacks(runnable);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.exchange_toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_btn:
                dataManager.getRefreshedExchangeResult();
                Toast.makeText(getContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.switch_btn:
                switchCurrencies();
                break;
            case R.id.settings_btn:
                try {
                    handler.removeCallbacks(runnable);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.initial_currency_code:
                showDialog(initialCurrencyCode);
                break;
            case R.id.target_currency_code:
                showDialog(targetCurrencyCode);
                break;
            case R.id.exchange_switcher:
                switchCurrencies();
                break;
            case R.id.zero:
                enterNumber("0");
                break;
            case R.id.one:
                enterNumber("1");
                break;
            case R.id.two:
                enterNumber("2");
                break;
            case R.id.three:
                enterNumber("3");
                break;
            case R.id.four:
                enterNumber("4");
                break;
            case R.id.five:
                enterNumber("5");
                break;
            case R.id.six:
                enterNumber("6");
                break;
            case R.id.seven:
                enterNumber("7");
                break;
            case R.id.eight:
                enterNumber("8");
                break;
            case R.id.nine:
                enterNumber("9");
                break;
            case R.id.dot:
                if (checkFieldLength()) {
                    if (!exchangeAmountValue.toString().contains(".")) {
                        exchangeAmountValue.append(".");
                        exchangeAmount.setText(exchangeAmountValue);
                    }
                } else {
                    showToast();
                }
                break;
            case R.id.clear_last:
                if (exchangeAmountValue.length() != 0) {
                    exchangeAmountValue = new StringBuilder(exchangeAmountValue
                            .substring(0, exchangeAmountValue.length() - 1));
                    if (exchangeAmountValue.length() != 0) {
                        exchangeAmount.setText(exchangeAmountValue);
                        dataManager.getRefreshedExchangeResult();
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

    private void showDialog(TextView textField) {
        View alertDialogView = getLayoutInflater().inflate(R.layout.dialog_currency_list, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setView(alertDialogView);
        if (textField.getId() == R.id.initial_currency_code) {
            alertBuilder.setTitle("Choose initial currency");
        } else {
            alertBuilder.setTitle("Choose target currency");
        }
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        ListView dialogListView = alertDialogView.findViewById(R.id.dialog_list_view);
        DialogListAdapter dialogListAdapter = new DialogListAdapter(getContext(), dataManager.getDialogCurrencyList());
        dialogListView.setAdapter(dialogListAdapter);

        dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CurrencyInformation currency = (CurrencyInformation) dialogListView.getItemAtPosition(i);
                if (textField.getId() == R.id.initial_currency_code) {
                    initialCurrency = currency.getCode();
                    textField.setText(initialCurrency);
                } else {
                    targetCurrency = currency.getCode();
                    textField.setText(targetCurrency);
                }
                alertDialog.dismiss();
                dataManager.getRefreshedExchangeResult();
            }
        });

        SearchView searchView = alertDialogView.findViewById(R.id.dialog_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dialogListAdapter.filter(newText);
                return false;
            }
        });
    }

    private void switchCurrencies() {
        String fromCurrencyCode = targetCurrencyCode.getText().toString();
        String toCurrencyCode = initialCurrencyCode.getText().toString();
        initialCurrencyCode.setText(fromCurrencyCode);
        targetCurrencyCode.setText(toCurrencyCode);
        dataManager.getRefreshedExchangeResult();
    }

    private void enterNumber(String value) {
        if (checkFieldLength()) {
            if (exchangeAmountValue.toString().equals("0")) {
                exchangeAmountValue = new StringBuilder(value);
            } else {
                exchangeAmountValue.append(value);
            }
            exchangeAmount.setText(exchangeAmountValue);
            dataManager.getRefreshedExchangeResult();
        } else {
            showToast();
        }
    }

    private boolean checkFieldLength() {
        return exchangeAmountValue.length() < 9;
    }

    private void clearFields() {
        exchangeAmountValue = new StringBuilder("0");
        exchangeAmount.setText(exchangeAmountValue);
        exchangeResult.setText("0");
    }

    private void showToast() {
        Toast.makeText(getContext(), "Maximum field length is reached!", Toast.LENGTH_SHORT).show();
    }
}
