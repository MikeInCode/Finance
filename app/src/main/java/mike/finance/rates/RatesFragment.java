package mike.finance.rates;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
public class RatesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        SwipeRefreshLayout.OnChildScrollUpCallback {

    private SwipeRefreshLayout refreshLayout;
    private ListView ratesListView;
    private DataUpdater dataUpdater;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ratesListView = view.findViewById(R.id.rates_list_view);

        dataUpdater = new DataUpdater(view);
        dataUpdater.refreshData(ratesListView);

        refreshLayout = view.findViewById(R.id.rates_list_refresh);
        refreshLayout.setOnChildScrollUpCallback(this);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
        return ratesListView.getFirstVisiblePosition() != 0;
    }

    @Override
    public void onRefresh() {
        dataUpdater.refreshData(ratesListView);
        refreshLayout.setRefreshing(false);
    }
}
