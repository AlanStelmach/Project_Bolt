package com.example.udrive;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class Frag3 extends Fragment {


    public Frag3() {}
    public Button buttonSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag3, container, false);

        buttonSignIn = view.findViewById(R.id.Sign_In_Button);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {           //Po kliknięciu w przycisk SignIn przechodzi do Location Activity
            @Override
            public void onClick(View v) {
                openLocationActivity();
            }
        });
        return view;
    }

    private void openLocationActivity() {
        Intent intent = new Intent(getActivity(),Location.class);
        startActivity(intent);
    }

}
