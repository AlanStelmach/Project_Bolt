package com.example.udrive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

// -------------- SPLASH SCREEN -------------- //
    private static int counter=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(MainActivity.this, onAppKilled.class));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);
                finish();
            }
        },counter);

    }
}
