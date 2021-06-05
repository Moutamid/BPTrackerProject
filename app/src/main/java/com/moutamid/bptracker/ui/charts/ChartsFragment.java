package com.moutamid.bptracker.ui.charts;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.bptracker.ColumnChartFragment;
import com.moutamid.bptracker.PieChartFragment;
import com.moutamid.bptracker.R;
import com.moutamid.bptracker.Utils;
import com.moutamid.bptracker.ui.statistics.StatisticsFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartsFragment extends Fragment {
    private static final String TAG = "ChartsFragment";

    private View root;

    private ImageView pieBtn, columnBtn;

    private ViewPagerFragmentAdapter adapter;
    private ViewPager viewPager;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ArrayList<ReadingModel> readingsArrayList = new ArrayList<>();

    private ArrayList<Integer> systolicIntsList = new ArrayList<>();
    private ArrayList<Integer> diastolicIntsList = new ArrayList<>();
    private ArrayList<Integer> pulseIntsList = new ArrayList<>();
    private ArrayList<Integer> ppIntsList = new ArrayList<>();
    private ArrayList<Integer> mapIntsList = new ArrayList<>();
    private ArrayList<Integer> weightIntsList = new ArrayList<>();
    private ArrayList<Integer> spo2IntsList = new ArrayList<>();
    private ArrayList<Integer> glucoseIntsList = new ArrayList<>();
    private ArrayList<String> statusStringsList = new ArrayList<>();

    private int systolicAverage,
            diastolicAverage,
            pulseAverage,
            statusNormalAverage,
            statusElevatedAverage,
            statusHypertensionStage1,
            statusHypertensionStage2,
            statusHypertensiveCrisisAverage;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_charts, container, false);

        if (getActivity() == null) {
            root.findViewById(R.id.no_data_text_view_charts).setVisibility(View.VISIBLE);
            return root;
        }

        String profileKey = new Utils().getStoredString(getActivity(), "currentProfileKey");

        databaseReference.child("readings").child(mAuth.getCurrentUser().getUid())
                .child(profileKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (getActivity() == null) {
                            root.findViewById(R.id.no_data_text_view_charts).setVisibility(View.VISIBLE);
                            Log.d(TAG, "onDataChange: " + "activity is null");
                            return;
                        }

                        if (!snapshot.exists()) {

                            root.findViewById(R.id.no_data_text_view_charts).setVisibility(View.VISIBLE);
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
                        root.findViewById(R.id.no_data_text_view_charts).setVisibility(View.VISIBLE);

//                        if (!getActivity().isDestroyed())
//                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        return root;
    }

    private void extractReadingsLists() {

        systolicIntsList.clear();
        diastolicIntsList.clear();
        pulseIntsList.clear();
//        ppIntsList.clear();
//        mapIntsList.clear();
//        weightIntsList.clear();
//        spo2IntsList.clear();
//        glucoseIntsList.clear();
        statusStringsList.clear();

//        systolicIntsList.add(0);
//        diastolicIntsList.add(0);
//        pulseIntsList.add(0);
//        ppIntsList.add(0);
//        mapIntsList.add(0);
//        weightIntsList.add(0);
//        spo2IntsList.add(0);
//        glucoseIntsList.add(0);

//        // EXTRACT GLUCOSE LISTS
//        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
//            if (!readingsArrayList.get(i).getGlucose().equals("null"))
//                glucoseIntsList.add(Integer.parseInt(readingsArrayList.get(i).getGlucose()));
//        }
//
//        // EXTRACT SPO2 LISTS
//        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
//            if (!readingsArrayList.get(i).getSpo2().equals("null"))
//                spo2IntsList.add(Integer.parseInt(readingsArrayList.get(i).getSpo2()));
//        }
//
//        // EXTRACT WEIGHT LISTS
//        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
//            if (!readingsArrayList.get(i).getWeight().equals("null"))
//                weightIntsList.add(Integer.parseInt(readingsArrayList.get(i).getWeight()));
//        }
//
//        // EXTRACT MAP LISTS
//        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
//            mapIntsList.add(Integer.parseInt(readingsArrayList.get(i).getMap()));
//        }
//
//        // EXTRACT PP LISTS
//        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
//            ppIntsList.add(Integer.parseInt(readingsArrayList.get(i).getPp()));
//        }

        // EXTRACT PULSE LISTS

        for (int i = 0; i <= readingsArrayList.size() - 1; i++) {
            statusStringsList.add(readingsArrayList.get(i).getStatus());
        }

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
//
//        systolicMin = String.valueOf(Collections.min(systolicIntsList));
//        diastolicMin = String.valueOf(Collections.min(diastolicIntsList));
//        pulseMin = String.valueOf(Collections.min(pulseIntsList));
//        ppMin = String.valueOf(Collections.min(ppIntsList));
//        mapMin = String.valueOf(Collections.min(mapIntsList));
//
//        if (!weightIntsList.isEmpty())
//            weightMin = String.valueOf(Collections.min(weightIntsList));
//
//        if (!spo2IntsList.isEmpty())
//            spo2Min = String.valueOf(Collections.min(spo2IntsList));
//
//        if (!glucoseIntsList.isEmpty())
//            glucoseMin = String.valueOf(Collections.min(glucoseIntsList));
//
//        systolicMax = String.valueOf(Collections.max(systolicIntsList));
//        diastolicMax = String.valueOf(Collections.max(diastolicIntsList));
//        pulseMax = String.valueOf(Collections.max(pulseIntsList));
//        ppMax = String.valueOf(Collections.max(ppIntsList));
//        mapMax = String.valueOf(Collections.max(mapIntsList));
//
//        if (!weightIntsList.isEmpty())
//            weightMax = String.valueOf(Collections.max(weightIntsList));
//
//        if (!spo2IntsList.isEmpty())
//            spo2Max = String.valueOf(Collections.max(spo2IntsList));
//
//        if (!glucoseIntsList.isEmpty())
//            glucoseMax = String.valueOf(Collections.max(glucoseIntsList));

        systolicAverage = getAverageOfList(systolicIntsList);
        diastolicAverage = getAverageOfList(diastolicIntsList);
        pulseAverage = getAverageOfList(pulseIntsList);
        statusNormalAverage = Collections.frequency(statusStringsList, "Normal");
        statusElevatedAverage = Collections.frequency(statusStringsList, "Elevated");
        statusHypertensionStage1 = Collections.frequency(statusStringsList, "Hypertension stage 1");
        statusHypertensionStage2 = Collections.frequency(statusStringsList, "Hypertension stage 2");
        statusHypertensiveCrisisAverage = Collections.frequency(statusStringsList, "Hypertensive crisis");

        storeInt("systolicAverage", systolicAverage);
        storeInt("diastolicAverage", diastolicAverage);
        storeInt("pulseAverage", pulseAverage);
        storeInt("statusNormalAverage", statusNormalAverage);
        storeInt("statusElevatedAverage", statusElevatedAverage);
        storeInt("statusHypertensionStage1", statusHypertensionStage1);
        storeInt("statusHypertensionStage2", statusHypertensionStage2);
        storeInt("statusHypertensiveCrisisAverage", statusHypertensiveCrisisAverage);

        workOnViewPagers();

//        ppAverage = getAverageOfList(ppIntsList);
//        mapAverage = getAverageOfList(mapIntsList);
//        weightAverage = getAverageOfList(weightIntsList);
//        spo2Average = getAverageOfList(spo2IntsList);
//        glucoseAverage = getAverageOfList(glucoseIntsList);
//
//        setValuesOnViews();

    }

    private void storeInt(String name, int value) {
        if (getActivity() != null)
            new Utils().storeInteger(getActivity(), name, value);
    }

    private int getAverageOfList(ArrayList<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            long longSum = Math.round(sum.doubleValue() / marks.size());
            return (int) longSum;
        } else {
            return 0;
        }


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

    private void workOnViewPagers() {
        viewPager = root.findViewById(R.id.view_pager_charts);

        adapter = new ViewPagerFragmentAdapter(getActivity().getSupportFragmentManager());

        setupViewPager(viewPager);

        pieBtn = root.findViewById(R.id.pieBtn);
        columnBtn = root.findViewById(R.id.column_btn);

        pieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
                pieBtn.setBackgroundResource(R.drawable.bg_charts_options);
                columnBtn.setBackgroundResource(0);
            }
        });

        columnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
                pieBtn.setBackgroundResource(0);
                columnBtn.setBackgroundResource(R.drawable.bg_charts_options);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {

        // Adding Fragments to Adapter
        adapter.addFragment(new PieChartFragment());
        adapter.addFragment(new ColumnChartFragment());

        // Setting Adapter To ViewPager
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        pieBtn.setBackgroundResource(R.drawable.bg_charts_options);
                        columnBtn.setBackgroundResource(0);
                        break;
                    case 1:
                        pieBtn.setBackgroundResource(0);
                        columnBtn.setBackgroundResource(R.drawable.bg_charts_options);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    public static class NonSwipableViewPager extends ViewPager {


        public NonSwipableViewPager(@NonNull Context context) {
            super(context);
        }

        public NonSwipableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            return false;
        }

        public void setMyScroller() {

            try {
                Class<?> viewpager = ViewPager.class;
                Field scroller = viewpager.getDeclaredField("mScroller");
                scroller.setAccessible(true);
                scroller.set(this, new MyScroller(getContext()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public class MyScroller extends Scroller {

            public MyScroller(Context context) {
                super(context, new DecelerateInterpolator());
            }

            @Override
            public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                super.startScroll(startX, startY, dx, dy, 350);
            }
        }
    }

    public static class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        public ViewPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

}