package com.moutamid.bptracker;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
    private static final String TAG = "BottomNavigationActivit";

    private RelativeLayout profileLayout;
    private TextView profileLetterTv;
    private Utils utils = new Utils();
private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView headerTextView;
private         BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        navView = findViewById(R.id.nav_view);
        profileLayout = findViewById(R.id.profile_bg_bottom_activity);
        profileLetterTv = findViewById(R.id.profile_text_view_activity_bottom);

        profileLayout.setOnClickListener(profileLayoutClickListener());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_readings, R.id.navigation_statistics, R.id.navigation_charts)
                .build();

        headerTextView = findViewById(R.id.action_bar_text_view);

        loadFragment(new ReadingsFragment());

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

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

    private View.OnClickListener profileLayoutClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(BottomNavigationActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_profile);
                dialog.setCancelable(true);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                RelativeLayout profileDialog = dialog.findViewById(R.id.profile_bg_bottom_activity_dialog);
                TextView profileLetterDialog = dialog.findViewById(R.id.profile_text_view_activity_bottom_dialog);
                TextView profileNameDialog = dialog.findViewById(R.id.profile_name_dialog);

                profileDialog.setBackgroundColor(color);
                profileLetterDialog.setText(name);
                profileNameDialog.setText(name);

                if (mAuth.getCurrentUser().isAnonymous())
                dialog.findViewById(R.id.login_text_view_dialog).setVisibility(View.VISIBLE);

                dialog.findViewById(R.id.login_text_view_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(BottomNavigationActivity.this, ActivitySignUp.class).putExtra("anon", true));

                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.manage_current_profile_text_view_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String profileKey = new Utils().getStoredString(BottomNavigationActivity.this,
                                "currentProfileKey");

                        Intent intent = new Intent(BottomNavigationActivity.this, CreateProfileActivity.class);
                        intent.putExtra("key", profileKey);

                        startActivity(intent);

                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.switch_profile_text_view_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(BottomNavigationActivity.this, ProfilesListActivity.class);
                        intent.putExtra("select", true);

                        startActivity(intent);

                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.add_profile_text_view_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(BottomNavigationActivity.this, CreateProfileActivity.class));

                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.manage_profile_text_view_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(BottomNavigationActivity.this, ProfilesListActivity.class));

                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.settings_text_view_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(BottomNavigationActivity.this, SettingsActivity.class));

                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.export_and_send_text_view_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(BottomNavigationActivity.this, PdfCreater.class));

                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getWindow().setAttributes(layoutParams);

            }
        };
    }

    private int color;
    private String name;

    @Override
    protected void onResume() {
        super.onResume();

        if (!new Utils().getStoredBoolean(
                BottomNavigationActivity.this, "isFirstProfileAdded")) {

            startActivity(new Intent(BottomNavigationActivity.this, CreateProfileActivity.class));

        } else {
            name = new Utils().getStoredString(BottomNavigationActivity.this,
                    "currentProfileName");
            color = new Utils().getStoredInteger(BottomNavigationActivity.this,
                    "currentProfileColor");

            profileLayout.setBackgroundColor(color);
            profileLetterTv.setText(name);

            headerTextView.setText("Readings");
            navView.setSelectedItemId(R.id.navigation_readings);
            loadFragment(new ReadingsFragment());
        }

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