package com.example.udrive;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SignUp extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    public TextView textView1, textView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textView1 = findViewById(R.id.Sign_In_Text);
        textView2 = findViewById(R.id.Sign_Up_Text);

        fragmentManager = getSupportFragmentManager();

        if(findViewById(R.id.fragment_container) != null)
        {
            textView1.setEnabled(false);

            if(savedInstanceState != null)
            {
                return;
            }

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Frag3 frag3 = new Frag3();
            fragmentTransaction.add(R.id.fragment_container, frag3,null);
            fragmentTransaction.commit();
        }

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container,new Frag3(), null).addToBackStack(null).commit();
                textView2.setEnabled(true);
                textView1.setEnabled(false);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container,new Frag2(), null).addToBackStack(null).commit();
                textView1.setEnabled(true);
                textView2.setEnabled(false);
            }
        });
    }


}
