package com.moutamid.bptracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class CreateProfileActivity extends AppCompatActivity {
    private static final String TAG = "CreateProfileActivity";

    private Utils utils = new Utils();

    private int profileColorInt = -1294214;

    private Context context = CreateProfileActivity.this;

    private TextInputEditText nameEditText, genderEditText, dobEditText,
            emailNameEditText, emailAddressEditText;

    private String nameStr, genderStr, dobStr, emailNameStr, emailAddressStr;

    private AppCompatCheckBox diabetesCheckBox, weightCheckBox, oxygenCheckBox,
            bloodGlucoseCheckBox;

    private boolean isDiabetes = false, measureWeight = false, measureOxygen = false,
            measureGlucose = false;

    private TextView saveBtn;
    private RelativeLayout colorLayout;

    private ProgressDialog progressDialog;

    private String key;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        progressDialog = new ProgressDialog(CreateProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        colorLayout = findViewById(R.id.profile_color_layout);

        nameEditText = findViewById(R.id.name_textview_create_profile);
        genderEditText = findViewById(R.id.gender_create_profile);
        dobEditText = findViewById(R.id.date_of_birth_create_profile);
        emailNameEditText = findViewById(R.id.email_name_create_profile);
        emailAddressEditText = findViewById(R.id.email_address_create_profile);

        diabetesCheckBox = findViewById(R.id.diabetes_check_box_create_profile);
        weightCheckBox = findViewById(R.id.weight_check_box_create_profile);
        oxygenCheckBox = findViewById(R.id.oxygen_saturation_checkBox_create_profile);
        bloodGlucoseCheckBox = findViewById(R.id.blood_glucose_checkBox_create_profile);

        diabetesCheckBox.setOnCheckedChangeListener(setCheckBoxChangedListener("d"));
        weightCheckBox.setOnCheckedChangeListener(setCheckBoxChangedListener("w"));
        oxygenCheckBox.setOnCheckedChangeListener(setCheckBoxChangedListener("o"));
        bloodGlucoseCheckBox.setOnCheckedChangeListener(setCheckBoxChangedListener("g"));

        saveBtn = findViewById(R.id.save_btn_create_profile);
        saveBtn.setOnClickListener(saveBtnCLickListener());
        setCLickListenerOnColorLayout();

        if (getIntent().hasExtra("key")) {
            key = getIntent().getStringExtra("key");

            progressDialog.show();

            databaseReference.child("profiles")
                    .child(auth.getCurrentUser().getUid())
                    .child(key).addListenerForSingleValueEvent(previousValueEventListener());

        }

    }

    private ValueEventListener previousValueEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    key = "";
                    progressDialog.dismiss();
                }

                ProfileModel profileModel = snapshot.getValue(ProfileModel.class);

                setValuesOnViews(profileModel);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                key = "";
                progressDialog.dismiss();
                Toast.makeText(context, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void setValuesOnViews(ProfileModel profileModel) {

        isDiabetes = profileModel.isDiabetes();
        measureWeight = profileModel.isShouldMeasureWeight();
        measureOxygen = profileModel.isShouldMeasureOxygen();
        measureGlucose = profileModel.isShouldMeasureGlucose();
        nameEditText.setText(profileModel.getName());
        genderEditText.setText(profileModel.getGender());
        dobEditText.setText(profileModel.getDob());

        diabetesCheckBox.setChecked(profileModel.isDiabetes());
        weightCheckBox.setChecked(profileModel.isShouldMeasureWeight());
        oxygenCheckBox.setChecked(profileModel.isShouldMeasureOxygen());
        bloodGlucoseCheckBox.setChecked(profileModel.isShouldMeasureGlucose());

        colorLayout.setBackgroundColor(profileModel.getProfileColor());

        progressDialog.dismiss();
    }

    private View.OnClickListener saveBtnCLickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameStr = nameEditText.getText().toString();
                genderStr = genderEditText.getText().toString();
                dobStr = dobEditText.getText().toString();
                emailNameStr = emailNameEditText.getText().toString();
                emailAddressStr = emailAddressEditText.getText().toString();

                if (nameStr == null || nameStr.isEmpty()) {
                    Toast.makeText(CreateProfileActivity.this, "Please enter a name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (emailNameStr == null || emailNameStr.isEmpty()) {
                    emailNameStr = "null";
                }
                if (emailAddressStr.isEmpty()) {
                    emailAddressStr = "null";
                }

                progressDialog.show();

                if (key == null || key.isEmpty()) {
                    key = databaseReference.child("profiles")
                            .child(auth.getCurrentUser().getUid())
                            .push()
                            .getKey();
                }

                ProfileModel model = new ProfileModel();

                model.setUserUid(auth.getCurrentUser().getUid());
                model.setPushKey(key);
                model.setName(nameStr);
                model.setProfileColor(profileColorInt);
                model.setDiabetes(isDiabetes);
                model.setShouldMeasureWeight(measureWeight);
                model.setShouldMeasureOxygen(measureOxygen);
                model.setShouldMeasureGlucose(measureGlucose);

                if (genderStr == null || genderStr.isEmpty()) {
                    model.setGender("null");
                } else {
                    model.setGender(genderStr);
                }
                if (dobStr == null || dobStr.isEmpty()) {
                    model.setDob("null");
                } else {
                    model.setDob(dobStr);
                }

                databaseReference.child("profiles")
                        .child(auth.getCurrentUser().getUid())
                        .child(key)
                        .setValue(model)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    utils.storeString(context, "gmailName", emailNameStr);
                                    utils.storeString(context, "gmailAddress", emailAddressStr);

                                    if (!utils.getStoredBoolean(
                                            context, "isFirstProfileAdded")) {
                                        utils.storeBoolean(context, "isFirstProfileAdded", true);
                                    }
// TODO: USE THIS KEY IN ALL FRAGMENTS TO RETRIEVE DATA OF A CERTAIN PROFILE

                                    if (!getIntent().hasExtra("edit")) {

                                        utils.storeString(context, "currentProfileKey", key);
                                        utils.storeString(context, "currentProfileName", nameStr);
                                        utils.storeInteger(context, "currentProfileColor", profileColorInt);
                                    }

                                    progressDialog.dismiss();
                                    Toast.makeText(CreateProfileActivity.this, "Profile created!", Toast.LENGTH_SHORT).show();

                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(CreateProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener setCheckBoxChangedListener(String value) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean booleanValue) {

                switch (value) {

                    case "d":
                        isDiabetes = booleanValue;
                        break;
                    case "w":
                        measureWeight = booleanValue;
                        break;
                    case "o":
                        measureOxygen = booleanValue;
                        break;
                    case "g":
                        measureGlucose = booleanValue;
                        break;

                }

            }
        };
    }

    private void setCLickListenerOnColorLayout() {
        colorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialColorPickerDialog
                        .Builder(CreateProfileActivity.this)
//                        .setTitle("Pick Theme")
                        .setColorShape(ColorShape.SQAURE)
                        .setColorSwatch(ColorSwatch._400)
                        .setDefaultColor(R.color.red_400)
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int color, @NotNull String colorHex) {
                                // Handle Color Selection
                                profileColorInt = color;
                                colorLayout.setBackgroundColor(profileColorInt);
                                Log.d(TAG, "onColorSelected: " + color);
                            }
                        })
                        .show();

            }
        });
    }

    private static class ProfileModel {

        private String name, gender, dob, userUid, pushKey;
        private boolean diabetes, shouldMeasureWeight, shouldMeasureOxygen,
                shouldMeasureGlucose;
        private int profileColor;

        public ProfileModel(String name, String gender, String dob, String userUid, String pushKey, boolean diabetes, boolean shouldMeasureWeight, boolean shouldMeasureOxygen, boolean shouldMeasureGlucose, int profileColor) {
            this.name = name;
            this.gender = gender;
            this.dob = dob;
            this.userUid = userUid;
            this.pushKey = pushKey;
            this.diabetes = diabetes;
            this.shouldMeasureWeight = shouldMeasureWeight;
            this.shouldMeasureOxygen = shouldMeasureOxygen;
            this.shouldMeasureGlucose = shouldMeasureGlucose;
            this.profileColor = profileColor;
        }

        public int getProfileColor() {
            return profileColor;
        }

        public void setProfileColor(int profileColor) {
            this.profileColor = profileColor;
        }

        public String getUserUid() {
            return userUid;
        }

        public void setUserUid(String userUid) {
            this.userUid = userUid;
        }

        public String getPushKey() {
            return pushKey;
        }

        public void setPushKey(String pushKey) {
            this.pushKey = pushKey;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public boolean isDiabetes() {
            return diabetes;
        }

        public void setDiabetes(boolean diabetes) {
            this.diabetes = diabetes;
        }

        public boolean isShouldMeasureWeight() {
            return shouldMeasureWeight;
        }

        public void setShouldMeasureWeight(boolean shouldMeasureWeight) {
            this.shouldMeasureWeight = shouldMeasureWeight;
        }

        public boolean isShouldMeasureOxygen() {
            return shouldMeasureOxygen;
        }

        public void setShouldMeasureOxygen(boolean shouldMeasureOxygen) {
            this.shouldMeasureOxygen = shouldMeasureOxygen;
        }

        public boolean isShouldMeasureGlucose() {
            return shouldMeasureGlucose;
        }

        public void setShouldMeasureGlucose(boolean shouldMeasureGlucose) {
            this.shouldMeasureGlucose = shouldMeasureGlucose;
        }

        ProfileModel() {
        }
    }
}