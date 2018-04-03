package mike.finance.exchange;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import mike.finance.DataUpdater;
import mike.finance.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExchangeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);

        TextView primaryCurrencyValue = view.findViewById(R.id.primary_currency_value);
        primaryCurrencyValue.setMaxEms(5);

        TextView exchangeResult = view.findViewById(R.id.secondary_currency_value);
        DataUpdater dataUpdater = new DataUpdater(view.getContext(), view);
        dataUpdater.refreshRatesData("1", exchangeResult);

        Button btnOne = view.findViewById(R.id.one);
        btnOne.setOnClickListener(l -> {
            if (primaryCurrencyValue.getText().toString().equals("0")) {
                primaryCurrencyValue.setText("");
            }
            primaryCurrencyValue.append("1");
            dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                    exchangeResult);
        });

        Button btnTwo = view.findViewById(R.id.two);
        btnTwo.setOnClickListener(l -> {
            if (primaryCurrencyValue.getText().toString().equals("0")) {
                primaryCurrencyValue.setText("");
            }
            primaryCurrencyValue.append("2");
            dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                    exchangeResult);
        });

        Button btnThree = view.findViewById(R.id.three);
        btnThree.setOnClickListener(l -> {
            if (primaryCurrencyValue.getText().toString().equals("0")) {
                primaryCurrencyValue.setText("");
            }
            primaryCurrencyValue.append("3");
            dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                    exchangeResult);
        });

        Button btnFour = view.findViewById(R.id.four);
        btnFour.setOnClickListener(l -> {
            if (primaryCurrencyValue.getText().toString().equals("0")) {
                primaryCurrencyValue.setText("");
            }
            primaryCurrencyValue.append("4");
            dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                    exchangeResult);
        });

        Button btnFive = view.findViewById(R.id.five);
        btnFive.setOnClickListener(l -> {
            if (primaryCurrencyValue.getText().toString().equals("0")) {
                primaryCurrencyValue.setText("");
            }
            primaryCurrencyValue.append("5");
            dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                    exchangeResult);
        });

        Button btnSix = view.findViewById(R.id.six);
        btnSix.setOnClickListener(l -> {
            if (primaryCurrencyValue.getText().toString().equals("0")) {
                primaryCurrencyValue.setText("");
            }
            primaryCurrencyValue.append("6");
            dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                    exchangeResult);
        });

        Button btnSeven = view.findViewById(R.id.seven);
        btnSeven.setOnClickListener(l -> {
            if (primaryCurrencyValue.getText().toString().equals("0")) {
                primaryCurrencyValue.setText("");
            }
            primaryCurrencyValue.append("7");
            dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                    exchangeResult);
        });

        Button btnEight = view.findViewById(R.id.eight);
        btnEight.setOnClickListener(l -> {
            if (primaryCurrencyValue.getText().toString().equals("0")) {
                primaryCurrencyValue.setText("");
            }
            primaryCurrencyValue.append("8");
            dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                    exchangeResult);
        });

        Button btnNine = view.findViewById(R.id.nine);
        btnNine.setOnClickListener(l -> {
            if (primaryCurrencyValue.getText().toString().equals("0")) {
                primaryCurrencyValue.setText("");
            }
            primaryCurrencyValue.append("9");
            dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                    exchangeResult);
        });

        Button btnZero = view.findViewById(R.id.zero);
        btnZero.setOnClickListener(l -> {
            if (primaryCurrencyValue.getText().toString().equals("0")) {
                primaryCurrencyValue.setText("");
            }
            primaryCurrencyValue.append("0");
            dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                    exchangeResult);
        });

        Button btnDot = view.findViewById(R.id.dot);
        btnDot.setOnClickListener(l -> {
            if (!primaryCurrencyValue.getText().toString().equals("0") &&
                    !primaryCurrencyValue.getText().toString().contains(".")) {
                primaryCurrencyValue.append(".");
            }
        });

        Button btnClearLast = view.findViewById(R.id.clear_last);
        btnClearLast.setOnClickListener(l -> {
            String str = primaryCurrencyValue.getText().toString();
            if (str.length() > 0) {
                str = str.substring(0, str.length() - 1);
                primaryCurrencyValue.setText(str);
            }
            if (str.length() == 0) {
                primaryCurrencyValue.setText("0");
                exchangeResult.setText("0");
            } else {
                dataUpdater.refreshRatesData(primaryCurrencyValue.getText().toString(),
                        exchangeResult);
            }

        });

        Button btnClearAll = view.findViewById(R.id.clear_all);
        btnClearAll.setOnClickListener(l -> {
            primaryCurrencyValue.setText("0");
            exchangeResult.setText("0");
        });

        return view;
    }
}
