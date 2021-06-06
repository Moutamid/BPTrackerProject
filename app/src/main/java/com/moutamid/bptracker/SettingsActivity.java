package com.moutamid.bptracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.rate_app_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(SettingsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_rating);
                dialog.setCancelable(true);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // CODE HERE
                        dialog.dismiss();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp")));

                    }
                });
                dialog.show();
                dialog.getWindow().setAttributes(layoutParams);

            }
        });

        findViewById(R.id.back_btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView weightText = findViewById(R.id.weight_option_settings);
        TextView classificationText = findViewById(R.id.classification_option_settings);

        classificationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                final CharSequence[] items = {"ISH 2020", "Hypertension Canada 2020",
                        "NICE 2019 - Clinic BP",
                        "NICE 2019 - HBPM",
                        "IGH - IV (2019)",
                        "ESC/ESH 2018",
                        "ACC/AHA 2017",
                        "National Heart Foundation of Australia 2016",
                        "JNC7",
                        "WHO/ISH 1999"
                };
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        classificationText.setText(items[position]);
                        utils.storeString(SettingsActivity.this, "classification", items[position].toString());

                    }
                });

                dialog = builder.create();
                dialog.show();
            }
        });

        weightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                final CharSequence[] items = {"kg", "lb"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        weightText.setText(items[position]);
                        utils.storeString(SettingsActivity.this, "weight", items[position].toString());

                    }
                });

                dialog = builder.create();
                dialog.show();
            }
        });

        TextView lowPressureTExt = findViewById(R.id.low_pressure_option_settings);
        lowPressureTExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                final CharSequence[] items = {"On", "Off"
                };
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        lowPressureTExt.setText(items[position]);
                        utils.storeString(SettingsActivity.this, "low_pressure", items[position].toString());

                    }
                });

                dialog = builder.create();
                dialog.show();
            }
        });

        TextView bloodGlucoseText = findViewById(R.id.blood_glucose_option_settings);
        lowPressureTExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                final CharSequence[] items = {"mmolL", "mgdL"
                };
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        bloodGlucoseText.setText(items[position]);
                        utils.storeString(SettingsActivity.this, "blood_glucose", items[position].toString());

                    }
                });

                dialog = builder.create();
                dialog.show();
            }
        });

        findViewById(R.id.reset_settings_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showDialog(SettingsActivity.this,
                        "Are you sure?",
                        "Settings will be reset to default values.",
                        "Reset",
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                classificationText.setText("JNC7");
                                lowPressureTExt.setText("Off");
                                weightText.setText("kg");
                                bloodGlucoseText.setText("mmolL");

                                utils.storeString(SettingsActivity.this, "classification", "JNC7");
                                utils.storeString(SettingsActivity.this, "weight", "kg");
                                utils.storeString(SettingsActivity.this, "low_pressure", "Off");
                                utils.storeString(SettingsActivity.this, "blood_glucose", "mmolL");

                                Toast.makeText(SettingsActivity.this, "Done", Toast.LENGTH_SHORT).show();

                                dialogInterface.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }, true);
            }
        });

    }
}