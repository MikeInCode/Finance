package mike.finance.exchange;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

        EditText primaryCurrencyValue = view.findViewById(R.id.primary_currency_value);
        //primaryCurrencyValue.setShowSoftInputOnFocus(false);



        // exchangeResult = view.findViewById(R.id.secondary_currency_value);
        //DataUpdater dataUpdater = new DataUpdater(view.getContext(), view);
        //dataUpdater.refreshRatesData(exchangeResult);

        return view;
    }
}
