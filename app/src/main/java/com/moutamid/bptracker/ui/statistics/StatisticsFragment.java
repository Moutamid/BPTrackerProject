package com.moutamid.bptracker.ui.statistics;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.bptracker.R;
import com.moutamid.bptracker.Utils;
import com.moutamid.bptracker.ui.readings.ReadingsFragment;

import java.util.ArrayList;
import java.util.Collections;

public class StatisticsFragment extends Fragment {
    private static final String TAG = "StatisticsFragment";

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ArrayList<ReadingModel> readingsArrayList = new ArrayList<>();

    /*
       Systolic Min, Max, Average
       Diastolic ...
       Pulse ...
       PP ...
       MAP ...
       Weight ...
       SpO2 ...
       Glucose ...
     */

    private ArrayList<Integer> systolicIntsList = new ArrayList<>();
    private ArrayList<Integer> diastolicIntsList = new ArrayList<>();
    private ArrayList<Integer> pulseIntsList = new ArrayList<>();
    private ArrayList<Integer> ppIntsList = new ArrayList<>();
    private ArrayList<Integer> mapIntsList = new ArrayList<>();
    private ArrayList<Integer> weightIntsList = new ArrayList<>();
    private ArrayList<Integer> spo2IntsList = new ArrayList<>();
    private ArrayList<Integer> glucoseIntsList = new ArrayList<>();

    private String systolicMin, systolicMax, systolicAverage,
            diastolicMin, diastolicMax, diastolicAverage,
            pulseMin, pulseMax, pulseAverage,
            ppMin, ppMax, ppAverage,
            mapMin, mapMax, mapAverage,
            weightMin = "0", weightMax = "0", weightAverage = "0",
            spo2Min = "0", spo2Max = "0", spo2Average = "0",
            glucoseMin = "0", glucoseMax = "0", glucoseAverage = "0";

    private TextView systolicMinTextView,
            systolicMaxTextView,
            systolicAverageTextView,
            diastolicMinTextView,
            diastolicMaxTextView,
            diastolicAverageTextView,
            pulseMinTextView,
            pulseMaxTextView,
            pulseAverageTextView,
            ppMinTextView,
            ppMaxTextView,
            ppAverageTextView,
            mapMinTextView,
            mapMaxTextView,
            mapAverageTextView,
            weightMinTextView,
            weightMaxTextView,
            weightAverageTextView,
            spo2MinTextView,
            spo2MaxTextView,
            spo2AverageTextView,
            glucoseMinTextView,
            glucoseMaxTextView,
            glucoseAverageTextView,
            systolicAverageTvTop,
            diastolicAverageTvTop;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_statistics, container, false);

        if (getActivity() == null) {
            return root;
        }

        initViews();

        String profileKey = new Utils().getStoredString(getActivity(), "currentProfileKey");

        databaseReference.child("readings").child(mAuth.getCurrentUser().getUid())
                .child(profileKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (getActivity() == null) {
                            Log.d(TAG, "onDataChange: " + "activity is null");
                            return;
                        }

                        if (!snapshot.exists()) {
//                            if (!getActivity().isDestroyed())
//                            progressDialog.dismiss();
                            return;
                        }
                        readingsArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            readingsArrayList.add(dataSnapshot.getValue(ReadingModel.class));
                        }

                        extractReadingsLists();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
//                        if (!getActivity().isDestroyed())
//                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        setDateLayoutClickListener();

        return root;
    }

    private void initViews() {

        systolicMinTextView = root.findViewById(R.id.systolic_min);
        systolicMaxTextView = root.findViewById(R.id.systolic_max);
        systolicAverageTextView = root.findViewById(R.id.systolic_average);
        diastolicMinTextView = root.findViewById(R.id.diastolic_min);
        diastolicMaxTextView = root.findViewById(R.id.diastolic_max);
        diastolicAverageTextView = root.findViewById(R.id.diastolic_average);
        pulseMinTextView = root.findViewById(R.id.pulse_min);
        pulseMaxTextView = root.findViewById(R.id.pulse_max);
        pulseAverageTextView = root.findViewById(R.id.pulse_average);
        ppMinTextView = root.findViewById(R.id.pp_min);
        ppMaxTextView = root.findViewById(R.id.pp_max);
        ppAverageTextView = root.findViewById(R.id.pp_average);
        mapMinTextView = root.findViewById(R.id.map_min);
        mapMaxTextView = root.findViewById(R.id.map_max);
        mapAverageTextView = root.findViewById(R.id.map_average);
        weightMinTextView = root.findViewById(R.id.weight_min);
        weightMaxTextView = root.findViewById(R.id.weight_max);
        weightAverageTextView = root.findViewById(R.id.weight_average);
        spo2MinTextView = root.findViewById(R.id.spo2_min);
        spo2MaxTextView = root.findViewById(R.id.spo2_max);
        spo2AverageTextView = root.findViewById(R.id.spo2_average);
        glucoseMinTextView = root.findViewById(R.id.glucose_min);
        glucoseMaxTextView = root.findViewById(R.id.glucose_max);
        glucoseAverageTextView = root.findViewById(R.id.glucose_average);
        systolicAverageTvTop = root.findViewById(R.id.systolic_average_top);
        diastolicAverageTvTop = root.findViewById(R.id.diastolic_average_top);
    }

    private void extractReadingsLists() {

        systolicIntsList.clear();
        diastolicIntsList.clear();
        pulseIntsList.clear();
        ppIntsList.clear();
        mapIntsList.clear();
        weightIntsList.clear();
        spo2IntsList.clear();
        glucoseIntsList.clear();

//        systolicIntsList.add(0);
//        diastolicIntsList.add(0);
//        pulseIntsList.add(0);
//        ppIntsList.add(0);
//        mapIntsList.add(0);
//        weightIntsList.add(0);
//        spo2IntsList.add(0);
//        glucoseIntsList.add(0);

        // EXTRACT GLUCOSE LISTS
        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            if (!readingsArrayList.get(i).getGlucose().equals("null"))
                glucoseIntsList.add(Integer.parseInt(readingsArrayList.get(i).getGlucose()));
        }

        // EXTRACT SPO2 LISTS
        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            if (!readingsArrayList.get(i).getSpo2().equals("null"))
                spo2IntsList.add(Integer.parseInt(readingsArrayList.get(i).getSpo2()));
        }

        // EXTRACT WEIGHT LISTS
        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            if (!readingsArrayList.get(i).getWeight().equals("null"))
                weightIntsList.add(Integer.parseInt(readingsArrayList.get(i).getWeight()));
        }

        // EXTRACT MAP LISTS
        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            mapIntsList.add(Integer.parseInt(readingsArrayList.get(i).getMap()));
        }

        // EXTRACT PP LISTS
        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            ppIntsList.add(Integer.parseInt(readingsArrayList.get(i).getPp()));
        }

        // EXTRACT PULSE LISTS
        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            pulseIntsList.add(Integer.parseInt(readingsArrayList.get(i).getPulse()));
        }

        // EXTRACT DIASTOLIC LISTS
        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            diastolicIntsList.add(Integer.parseInt(readingsArrayList.get(i).getDiastolic()));
        }

        // EXTRACT SYSTOLIC LISTS
        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            systolicIntsList.add(Integer.parseInt(readingsArrayList.get(i).getSystolic()));
        }

        getMinMaxAverageIntegers();

    }

    private void getMinMaxAverageIntegers() {

        systolicMin = String.valueOf(Collections.min(systolicIntsList));
        diastolicMin = String.valueOf(Collections.min(diastolicIntsList));
        pulseMin = String.valueOf(Collections.min(pulseIntsList));
        ppMin = String.valueOf(Collections.min(ppIntsList));
        mapMin = String.valueOf(Collections.min(mapIntsList));

        if (!weightIntsList.isEmpty())
            weightMin = String.valueOf(Collections.min(weightIntsList));

        if (!spo2IntsList.isEmpty())
            spo2Min = String.valueOf(Collections.min(spo2IntsList));

        if (!glucoseIntsList.isEmpty())
            glucoseMin = String.valueOf(Collections.min(glucoseIntsList));

        systolicMax = String.valueOf(Collections.max(systolicIntsList));
        diastolicMax = String.valueOf(Collections.max(diastolicIntsList));
        pulseMax = String.valueOf(Collections.max(pulseIntsList));
        ppMax = String.valueOf(Collections.max(ppIntsList));
        mapMax = String.valueOf(Collections.max(mapIntsList));

        if (!weightIntsList.isEmpty())
            weightMax = String.valueOf(Collections.max(weightIntsList));

        if (!spo2IntsList.isEmpty())
            spo2Max = String.valueOf(Collections.max(spo2IntsList));

        if (!glucoseIntsList.isEmpty())
            glucoseMax = String.valueOf(Collections.max(glucoseIntsList));

        systolicAverage = getAverageOfList(systolicIntsList);
        diastolicAverage = getAverageOfList(diastolicIntsList);
        pulseAverage = getAverageOfList(pulseIntsList);
        ppAverage = getAverageOfList(ppIntsList);
        mapAverage = getAverageOfList(mapIntsList);
        weightAverage = getAverageOfList(weightIntsList);
        spo2Average = getAverageOfList(spo2IntsList);
        glucoseAverage = getAverageOfList(glucoseIntsList);

        setValuesOnViews();

    }

    private void setValuesOnViews() {

        systolicMinTextView.setText(systolicMin);
        systolicMaxTextView.setText(systolicMax);
        systolicAverageTextView.setText(systolicAverage);
        diastolicMinTextView.setText(diastolicMin);
        diastolicMaxTextView.setText(diastolicMax);
        diastolicAverageTextView.setText(diastolicAverage);
        pulseMinTextView.setText(pulseMin);
        pulseMaxTextView.setText(pulseMax);
        pulseAverageTextView.setText(pulseAverage);
        ppMinTextView.setText(ppMin);
        ppMaxTextView.setText(ppMax);
        ppAverageTextView.setText(ppAverage);
        mapMinTextView.setText(mapMin);
        mapMaxTextView.setText(mapMax);
        mapAverageTextView.setText(mapAverage);
        weightMinTextView.setText(weightMin);
        weightMaxTextView.setText(weightMax);
        weightAverageTextView.setText(weightAverage);
        spo2MinTextView.setText(spo2Min);
        spo2MaxTextView.setText(spo2Max);
        spo2AverageTextView.setText(spo2Average);
        glucoseMinTextView.setText(glucoseMin);
        glucoseMaxTextView.setText(glucoseMax);
        glucoseAverageTextView.setText(glucoseAverage);

        systolicAverageTvTop.setText(systolicAverage);
        diastolicAverageTvTop.setText(diastolicAverage);

        TextView totalReadings = root.findViewById(R.id.total_readings);
        totalReadings.setText(readingsArrayList.size() + "");

        setPressureStageStatus(Integer.parseInt(systolicAverage), Integer.parseInt(diastolicAverage));

        TextView classificationTv = root.findViewById(R.id.classification_textview);
        classificationTv.setText(
                new Utils().getStoredString(getActivity(), "classification")
        );

    }

    private String getAverageOfList(ArrayList<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return String.valueOf(Math.round(sum.doubleValue() / marks.size()));
        } else {
            return "0";
        }

    }

    private void setPressureStageStatus(int systolicInt, int diastolicInt) {
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

        RelativeLayout colorLayout1 = root.findViewById(R.id.average_status_color);
        TextView averageStatusTv = root.findViewById(R.id.average_status_textview);

        colorLayout1.setBackgroundColor(currentColor);
        averageStatusTv.setText(statusString);

    }

    private int greenColor = -10044566;
    private int yellowColor = -4520;
    private int lightOrangeColor = -13784;
    private int darkOrangeColor = -22746;
    private int redColor = -1092784;
    private int currentColor = greenColor;

    private void setDateLayoutClickListener() {
        root.findViewById(R.id.date_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getActivity() == null)
                    return;

                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.getMenuInflater().inflate(R.menu.subject_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ProgressDialog progressDialog;
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                TextView dateTv = root.findViewById(R.id.date_text_view);
                                dateTv.setText(item.getTitle().toString());
                            }
                        }, 1500);

                        return false;
                    }
                });
                popupMenu.show();

            }
        });
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