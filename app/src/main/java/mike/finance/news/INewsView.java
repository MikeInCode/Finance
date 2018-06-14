package mike.finance.news;

import android.view.MenuItem;

import java.util.List;

import mike.finance.ArticleInformation;
import mike.finance.IErrorHandler;

public interface INewsView extends IErrorHandler {
    void displayResult(List<ArticleInformation> list);
    void hideRefreshingStatus();
    void goToSettings();
    void goToArticle(String url);
    void setMenuItemChecked(MenuItem item, boolean value);
    void showDataRefreshedToast();
}
