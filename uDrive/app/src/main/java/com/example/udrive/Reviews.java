package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Reviews extends AppCompatActivity {

    private ImageView back;
    ArrayList<Rating> ratings = new ArrayList<>();
    private ListView ratingview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ratingview = (ListView) findViewById(R.id.reviews_view);
        back = (ImageView) findViewById(R.id.back10);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Reviews.this, DriverMapActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Rating").child(uid).child("1");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ratings.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String var = snapshot.child("name_surname").getValue(String.class);
                    String var1 = snapshot.child("text").getValue(String.class);
                    Float var2 = snapshot.child("stars").getValue(Float.class);

                    ratings.add(new Rating(var2,var1,var));
                }
                if(ratings.size() == 0)
                {
                    ratings.add(new Rating(0,"No ratings!",""));
                }
                ReviewsList adapter = new ReviewsList(Reviews.this, ratings);
                ratingview.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}