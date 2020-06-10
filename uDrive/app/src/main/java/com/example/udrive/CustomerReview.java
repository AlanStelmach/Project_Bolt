package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class CustomerReview extends AppCompatActivity {

    private ImageView mDriverImageProfile;
    private EditText mCommentText;
    private TextView mNameDriver, mSurnameDriver;
    private Button mSubmitReviev;
    private RatingBar mRatingDriver;
    private float ratingStars;
    private String name_surname;
    private String driver_id;
    private String yes = "com.example.udrive";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_review);
        Intent intent = getIntent();
        driver_id = intent.getStringExtra(yes);
        onStart();
        mDriverImageProfile = (ImageView) findViewById(R.id.driverProfileImage);
        mCommentText = (EditText) findViewById(R.id.commentText);
        mNameDriver = (TextView) findViewById(R.id.nameDriver);
        mSurnameDriver = (TextView) findViewById(R.id.surnameDriver);
        mSubmitReviev = (Button) findViewById(R.id.submitButton);
        mRatingDriver = (RatingBar) findViewById(R.id.ratingForDriver);

        mRatingDriver.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingStars = rating;
            }
        });
        mSubmitReviev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = mCommentText.getText().toString();
                if (comment.isEmpty()) {
                    comment = "Ride was ok!";
                }
                Rating rating = new Rating(ratingStars, comment, name_surname);
                FirebaseDatabase.getInstance().getReference().child("Rating").child(driver_id).child("1").push().setValue(rating).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        return;
                    }
                });
                Intent intent2 = new Intent(CustomerReview.this, CustomerMapActivity.class);
                startActivity(intent2);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Users").child(uid).child("name").getValue(String.class);
                String surname = dataSnapshot.child("Users").child(uid).child("surname").getValue(String.class);
                name_surname = name + " " + surname;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(driver_id);
        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nameDriver = dataSnapshot.child("name").getValue(String.class);
                    mNameDriver.setText(nameDriver);
                    String surnameDriver = dataSnapshot.child("surname").getValue(String.class);
                    mSurnameDriver.setText(surnameDriver);
                    getDriverImage();
            }

            public void getDriverImage() {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference reference = storage.getReference().child("Users_Images").child(driver_id).child("1");
                reference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        mDriverImageProfile.setImageBitmap(bitmap);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}