package com.moutamid.bptracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;

public class CreateReadingActivity extends AppCompatActivity {
    private static final String TAG = "CreateReadingActivity";
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Utils utils = new Utils();

    private Context context = CreateReadingActivity.this;

    private TextView dateTextView, cuffLocationTextView, bodyPositionTextView;
    private TextInputEditText systolicEdittext, diastolicEdittext, pulseEdittext,
            weightEditText, spo2Edittext, glucoseEditText;

    private String dateStr, cuffLocationStr, bodyPositionStr, systolicStr,
            diastolicStr, pulseStr, weightStr, spo2Str, glucoseStr;

    private int currentColor;

    private String key;

    private int greenColor = -10044566;
    private int yellowColor = -4520;
    private int lightOrangeColor = -13784;
    private int darkOrangeColor = -22746;
    private int redColor = -1092784;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reading);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        dateTextView = findViewById(R.id.date_text_view_reading);
        cuffLocationTextView = findViewById(R.id.cuff_location_reading);
        bodyPositionTextView = findViewById(R.id.body_position_reading);
        systolicEdittext = findViewById(R.id.systolic_textview_create_reading);
        diastolicEdittext = findViewById(R.id.diastolic_textview_create_reading);
        pulseEdittext = findViewById(R.id.pulse_textview_create_reading);
        weightEditText = findViewById(R.id.weight_reading);
        spo2Edittext = findViewById(R.id.spo2_reading);
        glucoseEditText = findViewById(R.id.glucose_reading);

        dateTextView.setText(utils.getDate());
        dateStr = utils.getDate();

        dateTextView.setOnClickListener(dateTextViewListener());

        cuffLocationTextView.setOnClickListener(cuffLocationTextViewListener());
        bodyPositionTextView.setOnClickListener(bodyPositionTextViewListener());

        setSaveBtnClickListener();

        if (getIntent().hasExtra("key")) {
            key = getIntent().getStringExtra("key");
            progressDialog.show();

            databaseReference.child("readings").child(mAuth.getCurrentUser().getUid())
                    .child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (!snapshot.exists()) {
                                progressDialog.dismiss();
                                key = "";
                                return;
                            }

                            ReadingModel readingModel = snapshot.getValue(ReadingModel.class);

                            //private TextView dateTextView,
                            // cuffLocationTextView, bodyPositionTextView;
                            //    private TextInputEditText systolicEdittext,
                            //    diastolicEdittext, pulseEdittext,
                            //            weightEditText, spo2Edittext,
                            //            glucoseEditText;
                            //
                            //    private String dateStr, cuffLocationStr,
                            //    bodyPositionStr, systolicStr,
                            //            diastolicStr, pulseStr, weightStr,
                            //            spo2Str, glucoseStr;
                            //
                            //    private int currentColor;

                            currentColor = readingModel.getColor();
                            glucoseStr = readingModel.getGlucose();
                            spo2Str = readingModel.getSpo2();
                            weightStr = readingModel.getWeight();
                            pulseStr = readingModel.getPulse();
                            diastolicStr = readingModel.getDiastolic();
                            systolicStr = readingModel.getSystolic();
                            bodyPositionStr = readingModel.getBodyPosition();
                            cuffLocationStr = readingModel.getCuffLocation();
                            dateStr = readingModel.getDate();
                            glucoseEditText.setText(readingModel.getGlucose());
                            spo2Edittext.setText(readingModel.getSpo2());
                            weightEditText.setText(readingModel.getWeight());
                            pulseEdittext.setText(readingModel.getPulse());
                            diastolicEdittext.setText(readingModel.getDiastolic());
                            systolicEdittext.setText(readingModel.getSystolic());
                            bodyPositionTextView.setText(readingModel.getBodyPosition());
                            cuffLocationTextView.setText(readingModel.getCuffLocation());
                            dateTextView.setText(readingModel.getDate());

                            progressDialog.dismiss();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(context, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private View.OnClickListener dateTextViewListener() {
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MaterialDatePicker.Builder singleDatePicker = MaterialDatePicker.Builder.datePicker();
                singleDatePicker.setTitleText("Select date");
                singleDatePicker.setSelection(today);
                final MaterialDatePicker materialDatePicker = singleDatePicker.build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        dateStr = materialDatePicker.getHeaderText();
                        dateTextView.setText(dateStr);
                    }
                });

                materialDatePicker.show(getSupportFragmentManager(), "DATE");

            }
        };
    }

    private View.OnClickListener bodyPositionTextViewListener() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.body_positions, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        bodyPositionTextView.setText(item.getTitle().toString());
                        bodyPositionStr = item.getTitle().toString();

                        return false;
                    }
                });
                popupMenu.show();
            }
        };
    }

    private View.OnClickListener cuffLocationTextViewListener() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.cuff_locations, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        cuffLocationTextView.setText(item.getTitle().toString());
                        cuffLocationStr = item.getTitle().toString();

                        return false;
                    }
                });
                popupMenu.show();
            }
        };
    }

    private void setSaveBtnClickListener() {
        findViewById(R.id.save_btn_create_reading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //private String dateStr, cuffLocationStr, bodyPositionStr, systolicStr,
                //    diastolicStr, pulseStr, weightStr, spo2Str, glucoseStr;

                systolicStr = systolicEdittext.getText().toString();
                diastolicStr = diastolicEdittext.getText().toString();
                pulseStr = pulseEdittext.getText().toString();
                weightStr = weightEditText.getText().toString();
                spo2Str = spo2Edittext.getText().toString();
                glucoseStr = glucoseEditText.getText().toString();

                if (systolicStr == null || systolicStr.isEmpty()) {
                    Toast.makeText(context, "Systolic is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (diastolicStr == null || diastolicStr.isEmpty()) {
                    Toast.makeText(context, "Diastolic is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pulseStr == null || pulseStr.isEmpty()) {
                    Toast.makeText(context, "Pulse is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
//                else {
//                    pulseStr = pulseStr + "BPM";
//                }
                if (weightStr == null || weightStr.isEmpty()) {
                    weightStr = "null";
//                    return;
                }
//                else {
//                    weightStr = weightStr + utils.getStoredString(context, "weight");
//                }
                if (spo2Str == null || spo2Str.isEmpty()) {
                    spo2Str = "null";
//                    return;
                }
//                else {
//                    spo2Str = spo2Str + "% SpO2";
//                }

                if (glucoseStr == null || glucoseStr.isEmpty()) {
                    glucoseStr = "null";
//                    return;
                }
//                else {
//                    glucoseStr = glucoseStr + "mmolL";
//                }

                if (bodyPositionStr == null || bodyPositionStr.isEmpty()) {
                    Toast.makeText(context, "Please select a body position!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cuffLocationStr == null || cuffLocationStr.isEmpty()) {
                    Toast.makeText(context, "Please select cuff location!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dateStr == null || dateStr.isEmpty()) {
                    Toast.makeText(context, "Please select a date!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();

                if (key == null || key.isEmpty()) {
                    key = databaseReference.child("readings")
                            .child(mAuth.getCurrentUser().getUid())
                            .push().getKey();
                }

                ReadingModel readingModel = new ReadingModel();
                readingModel.setDate(dateStr);
                readingModel.setPushKey(key);
                readingModel.setUserUid(mAuth.getCurrentUser().getUid());
                readingModel.setCuffLocation(cuffLocationStr);
                readingModel.setBodyPosition(bodyPositionStr);
                readingModel.setPulse(pulseStr);
                readingModel.setWeight(weightStr);
                readingModel.setSpo2(spo2Str);
                readingModel.setGlucose(glucoseStr);
                int systolicInt = Integer.parseInt(systolicStr);
                int diastolicInt = Integer.parseInt(diastolicStr);
                readingModel.setSystolic(systolicStr);
                readingModel.setDiastolic(diastolicStr);
                readingModel.setStatus(getPressureStageStatus(systolicInt, diastolicInt));
                readingModel.setColor(currentColor);
                readingModel.setPp((systolicInt - diastolicInt) + "");
                readingModel.setMap((
                        (systolicInt + (2 * diastolicInt)) / 3
                ) + "");

                String profileKey = utils.getStoredString(context, "currentProfileKey");

                databaseReference.child("readings").child(mAuth.getCurrentUser().getUid())
                        .child(profileKey)
                        .child(key)
                        .setValue(readingModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    private String getPressureStageStatus(int systolicInt, int diastolicInt) {
        String statusString = "null";

        if (systolicInt <= 120) {
            statusString = "Normal";
            currentColor = greenColor;
        }

        if (diastolicInt <= 80) {
            statusString = "Normal";
            currentColor = greenColor;
        }

        if (systolicInt >= 120 && systolicInt <= 129) {
            statusString = "Elevated";
            currentColor = yellowColor;
        }

        if (systolicInt >= 130 && systolicInt <= 139) {
            statusString = "Hypertension stage 1";
            currentColor = lightOrangeColor;
        }

        if (diastolicInt >= 80 && diastolicInt <= 89) {
            statusString = "Hypertension stage 1";
            currentColor = lightOrangeColor;
        }

        if (systolicInt >= 140 && systolicInt < 180) {
            statusString = "Hypertension stage 2";
            currentColor = darkOrangeColor;
        }

        if (diastolicInt >= 90 && diastolicInt < 120) {
            statusString = "Hypertension stage 2";
            currentColor = darkOrangeColor;
        }

        if (systolicInt >= 180) {
            statusString = "Hypertensive crisis";
            currentColor = redColor;
        }

        if (diastolicInt >= 120) {
            statusString = "Hypertensive crisis";
            currentColor = redColor;
        }

        return statusString;
    }

    private static class ReadingModel {

        private String date, cuffLocation, bodyPosition, systolic,
                diastolic, pulse, weight, spo2,
                glucose, status, pp, pushKey, userUid, map;

        private int color;

        public ReadingModel(String date, String cuffLocation, String bodyPosition, String systolic, String diastolic, String pulse, String weight, String spo2, String glucose, String status, String pp, String pushKey, String userUid, String map, int color) {
            this.date = date;
            this.cuffLocation = cuffLocation;
            this.bodyPosition = bodyPosition;
            this.systolic = systolic;
            this.diastolic = diastolic;
            this.pulse = pulse;
            this.weight = weight;
            this.spo2 = spo2;
            this.glucose = glucose;
            this.status = status;
            this.pp = pp;
            this.pushKey = pushKey;
            this.userUid = userUid;
            this.map = map;
            this.color = color;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getMap() {
            return map;
        }

        public void setMap(String map) {
            this.map = map;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getCuffLocation() {
            return cuffLocation;
        }

        public void setCuffLocation(String cuffLocation) {
            this.cuffLocation = cuffLocation;
        }

        public String getBodyPosition() {
            return bodyPosition;
        }

        public void setBodyPosition(String bodyPosition) {
            this.bodyPosition = bodyPosition;
        }

        public String getSystolic() {
            return systolic;
        }

        public void setSystolic(String systolic) {
            this.systolic = systolic;
        }

        public String getDiastolic() {
            return diastolic;
        }

        public void setDiastolic(String diastolic) {
            this.diastolic = diastolic;
        }

        public String getPulse() {
            return pulse;
        }

        public void setPulse(String pulse) {
            this.pulse = pulse;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getSpo2() {
            return spo2;
        }

        public void setSpo2(String spo2) {
            this.spo2 = spo2;
        }

        public String getGlucose() {
            return glucose;
        }

        public void setGlucose(String glucose) {
            this.glucose = glucose;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPp() {
            return pp;
        }

        public void setPp(String pp) {
            this.pp = pp;
        }

        public String getPushKey() {
            return pushKey;
        }

        public void setPushKey(String pushKey) {
            this.pushKey = pushKey;
        }

        public String getUserUid() {
            return userUid;
        }

        public void setUserUid(String userUid) {
            this.userUid = userUid;
        }

        ReadingModel() {
        }
    }

}