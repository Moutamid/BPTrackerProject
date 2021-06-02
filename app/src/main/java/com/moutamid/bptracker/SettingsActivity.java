package com.moutamid.bptracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
private Utils utils = new Utils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

    }
}