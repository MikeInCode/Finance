package mike.finance;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import mike.finance.exchange.ExchangeFragment;
import mike.finance.rates.RatesFragment;

public class MainActivity extends AppCompatActivity {

    private RatesFragment ratesFragment;
    private ExchangeFragment exchangeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ratesFragment = new RatesFragment();
        exchangeFragment = new ExchangeFragment();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_rates:
                        transaction.replace(R.id.fragment_content, ratesFragment).commit();
                        return true;
                    case R.id.navigation_exchange:
                        transaction.replace(R.id.fragment_content, exchangeFragment).commit();
                        return true;
                    case R.id.navigation_news:

                        return true;
                }
                return false;
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, ratesFragment).commit();
    }
}