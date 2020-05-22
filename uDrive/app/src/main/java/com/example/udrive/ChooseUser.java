package com.example.udrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ChooseUser extends AppCompatActivity {

    private Button customerButton, driverButton;

    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        customerButton = (Button) findViewById(R.id.customerButton);
        driverButton = (Button) findViewById(R.id.driverButton);

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference createCustomer = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId);
               createCustomer.setValue(true);
               Intent intent = new Intent(ChooseUser.this, CustomerMapActivity.class);
               startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference createDriver = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId);
                createDriver.setValue(true);
                Intent intent = new Intent(ChooseUser.this, DriverMapActivity.class);
                startActivity(intent);
            }
        });
    }
}
