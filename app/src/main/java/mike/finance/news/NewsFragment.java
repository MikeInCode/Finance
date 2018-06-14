package mike.finance.news;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mike.finance.ArticleInformation;
import mike.finance.DataManager;
import mike.finance.R;
import mike.finance.SettingsActivity;
import mike.finance.SharedPreferencesAccessor;

public class NewsFragment extends Fragment implements INewsView, SwipeRefreshLayout.OnChildScrollUpCallback,
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.news_list_refresh) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.news_list_view) ListView newsListView;
    @BindView(R.id.no_internet_view) View noInternetView;
    @BindView(R.id.server_side_error_view) View serverSideErrorView;

    private NewsAdapter newsAdapter;
    private NewsPresenter newsPresenter;
    private Handler handler;
    private Runnable runnable;
    private int refreshInterval;
    private SharedPreferencesAccessor prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = new SharedPreferencesAccessor(getContext());
        DataManager dataManager = (DataManager) getActivity().getIntent().getSerializableExtra("data_manager");
        dataManager.init(view);
        newsPresenter = new NewsPresenter(this, dataManager);
        newsAdapter = new NewsAdapter(getContext(), new NewsAdapterCallback() {
            @Override
            public void onReadButtonClicked(View view, int position, String url) {
                newsPresenter.startReading(url);
            }
        });

        refreshLayout.setOnChildScrollUpCallback(this);
        refreshLayout.setOnRefreshListener(this);
    }

    public interface NewsAdapterCallback {
        void onReadButtonClicked(View view, int position, String url);
    }

    private void startAutoRefresh() {
        handler.postDelayed(runnable, refreshInterval);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
        onRefresh();
        if (!prefs.getAutoRefreshValue().equals("off")) {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                    showDataRefreshedToast();
                    startAutoRefresh();
                }
            };
            refreshInterval = Integer.valueOf(prefs.getAutoRefreshValue());
            startAutoRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            handler.removeCallbacks(runnable);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        newsPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
        return newsListView.getFirstVisiblePosition() != 0;
    }

    @Override
    public void onRefresh() {
        newsPresenter.refreshData();
    }

    @Override
    public void displayResult(List<ArticleInformation> list) {
        newsAdapter.setNewsList(list);
        newsListView.setAdapter(newsAdapter);
    }

    @Override
    public void hideRefreshingStatus() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void goToSettings() {
        startActivity(new Intent(getContext(), SettingsActivity.class));
    }

    @Override
    public void goToArticle(String url) {
        Intent intent = new Intent(getContext(), NewsWatcherActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void showDataRefreshedToast() {
        Toast.makeText(getContext(), "Data refreshed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setMenuItemChecked(MenuItem item, boolean value) {
        item.setChecked(value);
    }

    @Override
    public void hideAllRequestErrorViews() {
        noInternetView.setVisibility(View.INVISIBLE);
        serverSideErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNoInternetErrorView() {
        noInternetView.setVisibility(View.VISIBLE);
        noInternetView.bringToFront();
    }

    @Override
    public void showServerSideErrorView() {
        serverSideErrorView.setVisibility(View.VISIBLE);
        serverSideErrorView.bringToFront();
    }

    @Override
    public void showSystemErrorToast() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.news_toolbar_menu, menu);

        if (prefs.getNewsSortingTypeValue()) {
            menu.findItem(R.id.by_published_date).setChecked(true);
        } else {
            menu.findItem(R.id.by_popularity).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        newsPresenter.onOptionsMenuButtonClicked(prefs, item);
        return super.onOptionsItemSelected(item);
    }
}
