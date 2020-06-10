package com.example.udrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Logout extends AppCompatActivity {

    private Button logoutb;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        logoutb = (Button) findViewById(R.id.logout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        logoutb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signOut();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Logout.this,"Logging out!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Logout.this,SignUp.class));
                finish();
            }
        });
    }
}
