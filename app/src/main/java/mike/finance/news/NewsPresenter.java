package mike.finance.news;

import android.view.MenuItem;

import java.util.List;

import mike.finance.ArticleInformation;
import mike.finance.DataManager;
import mike.finance.R;
import mike.finance.SharedPreferencesAccessor;

public class NewsPresenter implements INewsPresenter {

    private INewsView newsView;
    private DataManager dataManager;

    public NewsPresenter(INewsView view, DataManager dataManager) {
        this.newsView = view;
        this.dataManager = dataManager;
    }

    @Override
    public void refreshData() {
        dataManager.getNewsData(new DataManager.NewsCallback() {
            @Override
            public void onSuccessfulRequest(List<ArticleInformation> list) {
                newsView.displayResult(list);
                newsView.hideAllRequestErrorViews();
                newsView.hideRefreshingStatus();
            }

            @Override
            public void onNoInternetError() {
                newsView.showNoInternetErrorView();
                newsView.hideRefreshingStatus();
            }

            @Override
            public void onServerSideError() {
                newsView.showServerSideErrorView();
                newsView.hideRefreshingStatus();
            }
        });
    }

    @Override
    public void startReading(String url) {
        newsView.goToArticle(url);
    }

    @Override
    public void onOptionsMenuButtonClicked(SharedPreferencesAccessor prefs, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_btn:
                newsView.showDataRefreshedToast();
                break;
            case R.id.by_published_date:
                if (!prefs.getNewsSortingTypeValue()) {
                    prefs.setNewsSortingTypeValue(true);
                    refreshData();
                    newsView.setMenuItemChecked(item, true);
                }
                break;
            case R.id.by_popularity:
                if (prefs.getNewsSortingTypeValue()) {
                    prefs.setNewsSortingTypeValue(false);
                    refreshData();
                    newsView.setMenuItemChecked(item, true);
                }
                break;
            case R.id.settings_btn:
                newsView.goToSettings();
                break;
        }
    }

    @Override
    public void onDestroy() {
        newsView = null;
    }
}
