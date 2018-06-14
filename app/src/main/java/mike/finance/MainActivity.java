package mike.finance;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import mike.finance.exchange.ExchangeFragment;
import mike.finance.news.NewsFragment;
import mike.finance.rates.RatesFragment;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    private RatesFragment ratesFragment = new RatesFragment();
    private ExchangeFragment exchangeFragment = new ExchangeFragment();
    private NewsFragment newsFragment = new NewsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getIntent().putExtra("data_manager", new DataManager());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, ratesFragment).commit();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        switch (item.getItemId()) {
            case R.id.navigation_rates:
                transaction.replace(R.id.fragment_content, ratesFragment).commit();
                return true;
            case R.id.navigation_exchange:
                transaction.replace(R.id.fragment_content, exchangeFragment).commit();
                return true;
            case R.id.navigation_news:
                transaction.replace(R.id.fragment_content, newsFragment).commit();
                return true;
        }
        return false;
    }
}