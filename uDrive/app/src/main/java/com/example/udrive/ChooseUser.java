package com.example.udrive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;

public class ChooseUser extends AppCompatActivity {

    private TextView greetings;
    private Button customerButton, driverButton;
    private String name;
    private String surname;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onStart();
        setContentView(R.layout.activity_choose_user);
        greetings = (TextView) findViewById(R.id.greetings_text);
        customerButton = (Button) findViewById(R.id.customerButton);
        driverButton = (Button) findViewById(R.id.driverButton);
        FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = userid.getUid();

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference createCustomer = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(uid);
                createCustomer.setValue(true);
                createCustomer.child("name").setValue(name);
                createCustomer.child("surname").setValue(surname);
               Intent intent = new Intent(ChooseUser.this, CustomerMapActivity.class);
               startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatabaseReference createDriver = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(uid);
            createDriver.setValue(true);
            createDriver.child("name").setValue(name);
            createDriver.child("surname").setValue(surname);
            Intent intent = new Intent(ChooseUser.this, DriverMapActivity.class);
            startActivity(intent);
        }
    });
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
        }

    protected void onStart(){
        super.onStart();
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
    }
}