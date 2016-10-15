package de.dtonal.moneykeeperapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import static de.dtonal.moneykeeperapp.LoginActivity.settingsFileName;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AddCostFragment.OnFragmentInteractionListener,
        CostsFragment.OnFragmentInteractionListener,
        MonthStatisticsFragment.OnFragmentInteractionListener,
        WeekStatisticFragment.OnFragmentInteractionListener,
        BudgetSettings.OnFragmentInteractionListener,
        StatusFragment.OnFragmentInteractionListener{

    public MoneyKeeperRestClientWithAuth client;

    private String mMail;
    private String mPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SharedPreferences preferences = getSharedPreferences(settingsFileName, 0);

        mMail = preferences.getString("mail", null);
        mPass = preferences.getString("pass", null);


        AddCostFragment addCostFragment = AddCostFragment.newInstance(mMail, mPass);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_main, addCostFragment, addCostFragment.getTag()).commit();
    }

    @Override
    public void onBackPressed() {

        AddCostFragment addCostFragment = AddCostFragment.newInstance(mMail, mPass);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_main, addCostFragment, addCostFragment.getTag()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences preferences = getSharedPreferences(settingsFileName, 0);

            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putString("mail", null);
            prefEditor.putString("pass", null);
            prefEditor.commit();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_cost) {
            AddCostFragment addCostFragment = AddCostFragment.newInstance(mMail, mPass);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, addCostFragment, addCostFragment.getTag()).commit();
        } else if (id == R.id.nav_list_costs) {
            CostsFragment costsFragment = CostsFragment.newInstance(mMail, mPass);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, costsFragment, costsFragment.getTag()).commit();

        }else if (id == R.id.nav_week_statistics) {
            WeekStatisticFragment weekStatisticFragment = WeekStatisticFragment.newInstance(mMail, mPass);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, weekStatisticFragment, weekStatisticFragment.getTag()).commit();

        }else if (id == R.id.nav_month_statistics) {
            MonthStatisticsFragment monthStatisticFragment = MonthStatisticsFragment.newInstance(mMail, mPass);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, monthStatisticFragment, monthStatisticFragment.getTag()).commit();

        }else if (id == R.id.nav_budget) {
            BudgetSettings budgetSettingsFragment = BudgetSettings.newInstance(mMail, mPass);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, budgetSettingsFragment, budgetSettingsFragment.getTag()).commit();

        }else if (id == R.id.nav_status) {
            StatusFragment statusFragment = StatusFragment.newInstance(mMail, mPass);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, statusFragment, statusFragment.getTag()).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public MoneyKeeperRestClientWithAuth getClient() {
        return client;
    }


}
