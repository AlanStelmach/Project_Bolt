package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class History extends AppCompatActivity {

    private ImageView back;
    private ListView historyview;
    final ArrayList<HistoryItem> historyItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        back = (ImageView) findViewById(R.id.back4);
        historyview = (ListView) findViewById(R.id.history_view);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History.this, CustomerMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("History").child(uid).child("1");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historyItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String var1 = snapshot.child("current_location").getValue(String.class);
                    String var2 = snapshot.child("destiny_location").getValue(String.class);
                    String var3 = snapshot.child("price").getValue(String.class);
                    String var4 = snapshot.child("status").getValue(String.class);
                    historyItems.add(new HistoryItem(var1, var2, var3, var4));
                }
                if(historyItems.size() == 0)
                {
                    historyItems.add(new HistoryItem("You don't have any travel history!", "","",""));
                }
                HistoryList adapter = new HistoryList(History.this, historyItems);
                historyview.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
