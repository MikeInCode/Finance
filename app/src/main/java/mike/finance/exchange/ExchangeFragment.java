package mike.finance.exchange;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import mike.finance.DataUpdater;
import mike.finance.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExchangeFragment extends Fragment implements View.OnClickListener {

    private TextView fromCurrencyCode;
    private TextView toCurrencyCode;
    private TextView exchangeAmount;
    private TextView exchangeResult;
    private DataUpdater dataUpdater;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exchange, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(view.getContext());

        fromCurrencyCode = view.findViewById(R.id.from_currency_code);
        fromCurrencyCode.setText("USD");
        toCurrencyCode = view.findViewById(R.id.to_currency_code);
        toCurrencyCode.setText(prefs.getString("primary_currency", "UAH"));

        exchangeAmount = view.findViewById(R.id.from_currency_value);
        exchangeAmount.setText("1");
        exchangeResult = view.findViewById(R.id.to_currency_value);

        dataUpdater = new DataUpdater(view);
        refreshExchangeResult();

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
            case R.id.zero:
                activateNumberButton("0");
                break;
            case R.id.one:
                activateNumberButton("1");
                break;
            case R.id.two:
                activateNumberButton("2");
                break;
            case R.id.three:
                activateNumberButton("3");
                break;
            case R.id.four:
                activateNumberButton("4");
                break;
            case R.id.five:
                activateNumberButton("5");
                break;
            case R.id.six:
                activateNumberButton("6");
                break;
            case R.id.seven:
                activateNumberButton("7");
                break;
            case R.id.eight:
                activateNumberButton("8");
                break;
            case R.id.nine:
                activateNumberButton("9");
                break;
            case R.id.dot:
                if (checkFieldLength()) {
                    if (!exchangeAmount.getText().toString().contains(".")) {
                        exchangeAmount.append(".");
                    }
                } else {
                    showSnackbar();
                }
                break;
            case R.id.clear_last:
                String str = exchangeAmount.getText().toString();
                if (exchangeAmount.length() != 0) {
                    exchangeAmount.setText(str.substring(0, str.length() - 1));
                    if (exchangeAmount.length() != 0) {
                        refreshExchangeResult();
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

    private void refreshExchangeResult() {
        dataUpdater.refreshData(fromCurrencyCode.getText().toString(), toCurrencyCode.getText().toString(),
                exchangeAmount.getText().toString(), exchangeResult);
    }

    private void activateNumberButton(String value) {
        if (checkFieldLength()) {
            if (exchangeAmount.getText().toString().equals("0")) {
                exchangeAmount.setText(value);
            } else {
                exchangeAmount.append(value);
            }
            refreshExchangeResult();
        } else {
            showSnackbar();
        }
    }

    private boolean checkFieldLength() {
        return exchangeAmount.length() < 9;
    }

    private void clearFields() {
        exchangeAmount.setText("0");
        exchangeResult.setText("0");
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar.make(getView(), "Maximum field length reached!", Snackbar.LENGTH_LONG);
        TextView sbText = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        sbText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }
}
