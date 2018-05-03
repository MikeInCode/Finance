package mike.finance.exchange;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mike.finance.CurrencyInformation;
import mike.finance.DataManager;
import mike.finance.DialogAdapter;
import mike.finance.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExchangeFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.from_currency_code) TextView fromCurrency;
    @BindView(R.id.to_currency_code) TextView toCurrency;
    @BindView(R.id.from_currency_value) TextView exchangeAmount;
    @BindView(R.id.to_currency_value) TextView exchangeResult;
    private DataManager dataManager;
    private String initialCurrency;
    private String targetCurrency;
    private StringBuilder exchangeAmountValue;
    private boolean isFirstRun = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isFirstRun) {
            initialCurrency = "USD";
            targetCurrency = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getString(getString(R.string.base_currency), "UAH");
            exchangeAmountValue = new StringBuilder("1");
            isFirstRun = false;
        }
        fromCurrency.setText(initialCurrency);
        toCurrency.setText(targetCurrency);
        exchangeAmount.setText(exchangeAmountValue);

        dataManager = (DataManager) getActivity().getIntent().getSerializableExtra("data_manager");
        dataManager.getRefreshedExchangeResult();

        fromCurrency.setOnClickListener(this);
        toCurrency.setOnClickListener(this);

        ImageButton exchangeSwitcher = view.findViewById(R.id.exchange_switcher);
        exchangeSwitcher.setOnClickListener(this);

        Button btnZero = view.findViewById(R.id.zero);
        btnZero.setOnClickListener(this);

        Button btnOne = view.findViewById(R.id.one);
        btnOne.setOnClickListener(this);

        Button btnTwo = view.findViewById(R.id.two);
        btnTwo.setOnClickListener(this);

        Button btnThree = view.findViewById(R.id.three);
        btnThree.setOnClickListener(this);

        Button btnFour = view.findViewById(R.id.four);
        btnFour.setOnClickListener(this);

        Button btnFive = view.findViewById(R.id.five);
        btnFive.setOnClickListener(this);

        Button btnSix = view.findViewById(R.id.six);
        btnSix.setOnClickListener(this);

        Button btnSeven = view.findViewById(R.id.seven);
        btnSeven.setOnClickListener(this);

        Button btnEight = view.findViewById(R.id.eight);
        btnEight.setOnClickListener(this);

        Button btnNine = view.findViewById(R.id.nine);
        btnNine.setOnClickListener(this);

        Button btnDot = view.findViewById(R.id.dot);
        btnDot.setOnClickListener(this);

        Button btnClearLast = view.findViewById(R.id.clear_last);
        btnClearLast.setOnClickListener(this);

        Button btnClearAll = view.findViewById(R.id.clear_all);
        btnClearAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.from_currency_code:
                showDialog(fromCurrency);
                break;
            case R.id.to_currency_code:
                showDialog(toCurrency);
                break;
            case R.id.exchange_switcher:
                String fromCurrencyCode = toCurrency.getText().toString();
                String toCurrencyCode = fromCurrency.getText().toString();
                fromCurrency.setText(fromCurrencyCode);
                toCurrency.setText(toCurrencyCode);
                dataManager.getRefreshedExchangeResult();
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
        if (textField.getId() == R.id.from_currency_code) {
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
        DialogAdapter dialogAdapter = new DialogAdapter(getContext(), dataManager.getDialogCurrencyList());
        dialogListView.setAdapter(dialogAdapter);

        dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CurrencyInformation currency = (CurrencyInformation) dialogListView.getItemAtPosition(i);
                if (textField.getId() == R.id.from_currency_code) {
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
                dialogAdapter.filter(newText);
                return false;
            }
        });
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
