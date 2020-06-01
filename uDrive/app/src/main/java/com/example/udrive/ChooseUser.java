package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ChooseUser extends AppCompatActivity {

    private TextView greetings;
    private Button customerButton, driverButton;
    private String name;
    private String surname;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
        greetings = (TextView) findViewById(R.id.greetings_text);
        FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = userid.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("Users").child(uid).child("name").getValue(String.class);
                surname = dataSnapshot.child("Users").child(uid).child("surname").getValue(String.class);
                greetings.setText("Hello there "+name+" "+surname+"!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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