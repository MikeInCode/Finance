package mike.finance.rates;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import mike.finance.DataUpdater;
import mike.finance.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatesFragment extends Fragment {

    private ListView ratesListView;
    private DataUpdater dataUpdater;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rates, container, false);

        ratesListView = view.findViewById(R.id.rates_list_view);

        dataUpdater = new DataUpdater(view.getContext(), view);
        dataUpdater.refreshRatesData(ratesListView);

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.rates_list_refresh);
        refreshLayout.setOnRefreshListener(() -> {
            dataUpdater.refreshRatesData(ratesListView);
            refreshLayout.setRefreshing(false);
        });

        return view;
    }
}
