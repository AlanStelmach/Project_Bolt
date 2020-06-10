package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUp extends AppCompatActivity {

    // ------------ SIGN IN / SIGN UP SCREEN ------------ //
    private static FragmentManager fragmentManager;
    private TextView textView1, textView2, underchar1, underchar2;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if(user != null) {
                if (user.isEmailVerified()) {
                    if(user != null) {
                        Intent intent = new Intent(SignUp.this, ChooseUser.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }

            textView1 = findViewById(R.id.Sign_In_Text);
            textView2 = findViewById(R.id.Sign_Up_Text);
            underchar1 = findViewById(R.id.underchar1);
            underchar2 = findViewById(R.id.underchar2);

            fragmentManager = getSupportFragmentManager();

            if (findViewById(R.id.fragment_container) != null) {
                textView1.setEnabled(false);
                underchar2.setVisibility(View.GONE);

                if (savedInstanceState != null) {
                    return;
                }

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Frag3 frag3 = new Frag3();
                fragmentTransaction.add(R.id.fragment_container, frag3, null);
                fragmentTransaction.commit();
            }

            textView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new Frag3(), null).addToBackStack(null).commit();
                    textView2.setEnabled(true);
                    textView1.setEnabled(false);
                    underchar1.setVisibility(View.VISIBLE);
                    underchar2.setVisibility(View.GONE);
                }
            });

            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new Frag2(), null).addToBackStack(null).commit();
                    textView1.setEnabled(true);
                    textView2.setEnabled(false);
                    underchar2.setVisibility(View.VISIBLE);
                    underchar1.setVisibility(View.GONE);
                }
            });

    }
}
