package com.moutamid.bptracker.ui.readings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moutamid.bptracker.CreateReadingActivity;
import com.moutamid.bptracker.R;

public class ReadingsFragment extends Fragment {

    private FloatingActionButton addReadingsButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_readings, container, false);

        addReadingsButton = root.findViewById(R.id.add_reading_fab);
        addReadingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreateReadingActivity.class));
            }
        });
        return root;
    }
}