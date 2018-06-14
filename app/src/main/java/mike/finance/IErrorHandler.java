package mike.finance;

public interface IErrorHandler {
    void hideAllRequestErrorViews();
    void showNoInternetErrorView();
    void showServerSideErrorView();
    void showSystemErrorToast();
}
