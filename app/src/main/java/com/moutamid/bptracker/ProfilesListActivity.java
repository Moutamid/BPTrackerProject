package com.moutamid.bptracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;
import static com.moutamid.bptracker.R.id.elastic;
import static com.moutamid.bptracker.R.id.profile_recycler_view;

public class ProfilesListActivity extends AppCompatActivity {
    private Utils utils = new Utils();
    private ImageView addProfileBtn;
    private ArrayList<ProfileModel> profilesArrayList = new ArrayList<>();

    private Context context = ProfilesListActivity.this;
    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    private boolean isSelectProfile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles_list);

        if (getIntent().hasExtra("select")) {
            isSelectProfile = true;

            TextView textView = findViewById(R.id.action_bar_text_view_profiles);
            textView.setText("Select profile");
            findViewById(R.id.add_profile_image_view).setVisibility(View.GONE);
        }

        addProfileBtn = findViewById(R.id.add_profile_image_view);
        addProfileBtn.setOnClickListener(addProfileBtnListener());

        progressDialog = new ProgressDialog(ProfilesListActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        databaseReference.child("profiles").child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            return;
                        }

                        profilesArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            profilesArrayList.add(dataSnapshot.getValue(ProfileModel.class));
                        }

                        initRecyclerView();

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfilesListActivity.this, error.toException().getMessage()
                                , Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void initRecyclerView() {

        conversationRecyclerView = findViewById(profile_recycler_view);
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
//        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);
    }

    private class RecyclerViewAdapterMessages extends Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_profile_list, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {

            holder.name.setText(profilesArrayList.get(position).getName());
            holder.nameLetter.setText(profilesArrayList.get(position).getName());
            holder.bgLayout.setBackgroundColor(profilesArrayList.get(position).getProfileColor());

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isSelectProfile) {

                        utils.storeString(context, "currentProfileKey",
                                profilesArrayList.get(position).getPushKey());

                        utils.storeString(context, "currentProfileName",
                                profilesArrayList.get(position).getName());

                        utils.storeInteger(context, "currentProfileColor",
                                profilesArrayList.get(position).getProfileColor());

                        Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show();

                        finish();
                        return;
                    }

                    Intent intent = new Intent(ProfilesListActivity.this, CreateProfileActivity.class);
                    intent.putExtra("key", profilesArrayList.get(position).getPushKey());
                    intent.putExtra("edit", true);
                    startActivity(intent);
                }
            });

            if (isSelectProfile) {
                holder.deleteButton.setVisibility(View.GONE);
                return;
            }

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    utils.showDialog(ProfilesListActivity.this,
                            "Are you sure?",
                            "Do you really want to delete this profile?",
                            "Yes",
                            "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    databaseReference
                                            .child("profiles")
                                            .child(auth.getCurrentUser().getUid())
                                            .child(profilesArrayList.get(position).getPushKey())
                                            .removeValue();

                                    databaseReference
                                            .child("readings")
                                            .child(auth.getCurrentUser().getUid())
                                            .child(profilesArrayList.get(position).getPushKey())
                                            .removeValue();

//TODO: ALSO DELETE ALL THE OTHER CHILDS IN THE READINGS SECTION
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

        @Override
        public int getItemCount() {
            if (profilesArrayList == null)
                return 0;
            return profilesArrayList.size();
        }

        public class ViewHolderRightMessage extends ViewHolder {

            TextView name, nameLetter;
            RelativeLayout parentLayout, bgLayout;
            ImageView deleteButton;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                name = v.findViewById(R.id.name_profile_list);
                nameLetter = v.findViewById(R.id.profile_text_view_profile_list);
                parentLayout = v.findViewById(R.id.parent_layout_profiles_list);
                bgLayout = v.findViewById(R.id.profile_bg_profile_list);
                deleteButton = v.findViewById(R.id.delete_profile_btn);

            }
        }

    }

    private View.OnClickListener addProfileBtnListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ProfilesListActivity.this, CreateProfileActivity.class));

            }
        };
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