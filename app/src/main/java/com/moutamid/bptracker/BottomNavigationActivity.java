package com.moutamid.bptracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moutamid.bptracker.ui.charts.ChartsFragment;
import com.moutamid.bptracker.ui.readings.ReadingsFragment;
import com.moutamid.bptracker.ui.statistics.StatisticsFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class BottomNavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_readings, R.id.navigation_statistics, R.id.navigation_charts)
                .build();

        loadFragment(new ReadingsFragment());

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                TextView headerTextView = findViewById(R.id.action_bar_text_view);
                switch (item.getItemId()) {

                    case R.id.navigation_statistics:
                        headerTextView.setText("Statistics");
                        loadFragment(new StatisticsFragment());
                        break;

                    case R.id.navigation_charts:
                        headerTextView.setText("Charts");
                        loadFragment(new ChartsFragment());
                        break;

                    case R.id.navigation_readings:
                    default:
                        headerTextView.setText("Readings");
                        loadFragment(new ReadingsFragment());
                        break;
                }

                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (!new Utils().getStoredBoolean(
//                BottomNavigationActivity.this, "firstTime")){
//
//            startActivity(new Intent(BottomNavigationActivity.this, CreateProfileActivity.class));
//
//        }

    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
        } else {
            Toast.makeText(this, "Fragment is null!", Toast.LENGTH_SHORT).show();
        }
    }

}