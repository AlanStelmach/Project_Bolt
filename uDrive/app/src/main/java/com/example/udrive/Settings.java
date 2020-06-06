package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Set;

public class Settings extends AppCompatActivity {

    private EditText edit_profile_name, edit_profile_surname, edit_profile_pnumber;
    private TextView profile_email;
    private Button edit_data;
    private ImageView profilepicture;
    private TextView profile_name;
    private ImageView back;
    private ProgressBar progressBar;
    private Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        back = (ImageView) findViewById(R.id.back8);
        edit_profile_name = (EditText) findViewById(R.id.edit_profile_name);
        edit_profile_surname = (EditText) findViewById(R.id.edit_profile_surname);
        edit_profile_pnumber = (EditText) findViewById(R.id.edit_profile_pnumber);
        profile_email = (TextView) findViewById(R.id.profile_email);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profilepicture = (ImageView) findViewById(R.id.profilepicture);
        edit_data = (Button) findViewById(R.id.edit_data);
        progressBar = (ProgressBar) findViewById(R.id.progressBar6);
        progressBar.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, CustomerMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        edit_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_profile_name.getText().toString();
                String surname = edit_profile_surname.getText().toString();
                String pnumber = edit_profile_pnumber.getText().toString();

                if(name.isEmpty())
                {
                    Toast.makeText(Settings.this, "Please enter new name!", Toast.LENGTH_LONG).show();
                    edit_profile_name.requestFocus();
                }
                else  if(surname.isEmpty())
                {
                    Toast.makeText(Settings.this, "Please enter new surname!", Toast.LENGTH_LONG).show();
                    edit_profile_surname.requestFocus();
                }
                else if(pnumber.isEmpty())
                {
                    Toast.makeText(Settings.this, "Please enter new phone number!", Toast.LENGTH_LONG).show();
                    edit_profile_pnumber.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("name",name);
                    hashMap.put("surname",surname);
                    hashMap.put("pnumber",pnumber);
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).updateChildren(hashMap);
                    Toast.makeText(Settings.this, "Success!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Users").child(uid).child("name").getValue(String.class);
                String surname = dataSnapshot.child("Users").child(uid).child("surname").getValue(String.class);
                String email = dataSnapshot.child("Users").child(uid).child("email").getValue(String.class);
                String pnumber = dataSnapshot.child("Users").child(uid).child("pnumber").getValue(String.class);

                edit_profile_name.setText(name);
                edit_profile_surname.setText(surname);
                edit_profile_pnumber.setText(pnumber);
                profile_email.setText(email);
                profile_name.setText(name+" "+surname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageuri = data.getData();
        }
    }

}