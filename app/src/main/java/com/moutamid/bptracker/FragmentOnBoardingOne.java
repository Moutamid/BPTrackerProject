package com.moutamid.bptracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentOnBoardingOne extends Fragment {

    private View view;

//    public LinearLayout spaceLayout() {
//
//        return view.findViewById(R.id.textonboardingspace);
//
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_on_boarding_one, container, false);

//        ImageView progress = view.findViewById(R.id.welcome_imageView);
//
//        AnimationDrawable frameAnimation = (AnimationDrawable) progress.getDrawable();
//        frameAnimation.setCallback(progress);
//        frameAnimation.setVisible(true, true);
//        frameAnimation.start();

        return view;
    }
}
