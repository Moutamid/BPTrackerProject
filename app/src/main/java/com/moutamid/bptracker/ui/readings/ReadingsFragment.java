package com.moutamid.bptracker.ui.readings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.bptracker.CreateReadingActivity;
import com.moutamid.bptracker.R;
import com.moutamid.bptracker.Utils;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;
import static com.moutamid.bptracker.R.id.readings_recycler_view;

public class ReadingsFragment extends Fragment {

    private FloatingActionButton addReadingsButton;
    private ArrayList<ReadingModel> readingsArrayList = new ArrayList<>();

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;
    private View root;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_readings, container, false);

        addReadingsButton = root.findViewById(R.id.add_reading_fab);
        addReadingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreateReadingActivity.class));
            }
        });

        if (getActivity() == null) {
            return root;
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        progressDialog.show();

        String profileKey = new Utils().getStoredString(getActivity(), "currentProfileKey");

        databaseReference.child("readings").child(mAuth.getCurrentUser().getUid())
                .child(profileKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            if (!getActivity().isDestroyed())
                                progressDialog.dismiss();
                            return;
                        }
                        readingsArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            readingsArrayList.add(dataSnapshot.getValue(ReadingModel.class));
                        }

                        initRecyclerView();
                        if (!getActivity().isDestroyed())
                            progressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (!getActivity().isDestroyed())
                            progressDialog.dismiss();
                        Toast.makeText(getActivity(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        return root;
    }

    private void initRecyclerView() {

        conversationRecyclerView = root.findViewById(readings_recycler_view);
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        //        LinearLayoutManager layoutManagerUserFriends = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //    int numberOfColumns = 3;
        //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
        //  recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

        //    if (adapter.getItemCount() != 0) {

        //        noChatsLayout.setVisibility(View.GONE);
        //        chatsRecyclerView.setVisibility(View.VISIBLE);

        //    }

    }

    /*public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }*/

    private class RecyclerViewAdapterMessages extends Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {
        String consonant = new Utils().getStoredString(getActivity(), "weight");

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_readings, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
            ReadingModel model = readingsArrayList.get(position);

            //            LinearLayout extraLayout;

            holder.glucoseTv.setText(model.getGlucose() + "mmolL");
            holder.spo2Tv.setText(model.getSpo2() + "% SpO2");
            holder.weightTv.setText(model.getWeight() + consonant);
            holder.mapTv.setText("MAP: " + model.getMap() + " mmHg");
            holder.ppTv.setText("PP: " + model.getPp() + " mmHg");
            holder.positionTv.setText(model.getBodyPosition());
            holder.cuffTv.setText(model.getCuffLocation()+" * ");
            holder.pressureStatusTv.setText(model.getStatus());
            holder.pulseTv.setText(model.getPulse() + "BPM");
            holder.diasTv.setText(model.getDiastolic() + "mmHg");
            holder.sysTv.setText(model.getSystolic() + "/");
            holder.dateText.setText(model.getDate());

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CreateReadingActivity.class);
                    intent.putExtra("key", model.getPushKey());

                    startActivity(intent);
                }
            });

            holder.colorLayout.setBackgroundColor(model.getColor());
        }

        @Override
        public int getItemCount() {
            if (readingsArrayList == null)
                return 0;
            return readingsArrayList.size();
        }

        public class ViewHolderRightMessage extends ViewHolder {

            RelativeLayout colorLayout, parentLayout;
            LinearLayout extraLayout;
            TextView dateText, sysTv, diasTv, pulseTv,
                    pressureStatusTv, cuffTv, positionTv,
                    ppTv, mapTv, weightTv, spo2Tv, glucoseTv;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                colorLayout = v.findViewById(R.id.color_layout_readings);
                parentLayout = v.findViewById(R.id.parent_layout_readings);
                extraLayout = v.findViewById(R.id.extra_layout);
                dateText = v.findViewById(R.id.date_text_viewReading);
                sysTv = v.findViewById(R.id.systolic_text_viewReading);
                diasTv = v.findViewById(R.id.diastolic_text_viewReading);
                pulseTv = v.findViewById(R.id.pulse_text_viewReading);
                pressureStatusTv = v.findViewById(R.id.pressureStatus_text_viewReading);
                cuffTv = v.findViewById(R.id.cuffLocation_text_viewReading);
                positionTv = v.findViewById(R.id.bodyPosition_text_viewReading);
                ppTv = v.findViewById(R.id.pp_text_viewReading);
                mapTv = v.findViewById(R.id.map_text_viewReading);
                weightTv = v.findViewById(R.id.weight_text_viewReading);
                spo2Tv = v.findViewById(R.id.spo2_text_viewReading);
                glucoseTv = v.findViewById(R.id.glucose_text_viewReading);

            }
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

}