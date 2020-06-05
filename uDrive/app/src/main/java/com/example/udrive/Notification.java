package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Notification extends AppCompatActivity {

    private ImageView back;
    private ImageView delete;
    private String checked="";
    private ListView notifyview;
    private int key_pos;
    final ArrayList<String> keys = new ArrayList<>();
    final ArrayList<String> notify = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        back = (ImageView) findViewById(R.id.back5);
        delete = (ImageView) findViewById(R.id.deleting);
        notifyview = (ListView) findViewById(R.id.notify_view);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification.this, CustomerMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked.isEmpty())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Notification.this);
                    builder.setCancelable(true);
                    builder.setTitle("Select notification first!");
                    builder.setMessage("Please select notification, which you want to delete.");
                    builder.show();
                }
                else if(checked.equals("Out of notifications!"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Notification.this);
                    builder.setCancelable(true);
                    builder.setTitle("Sorry dude, can't do that!");
                    builder.setMessage("");
                    builder.show();
                }
                else
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query applesQuery = ref.child("Notifications").child(uid).child("1").child(keys.get(key_pos));
                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeValue();
                            Toast.makeText(Notification.this, "Done!", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        notifyview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                for (int i = 0; i < notifyview.getChildCount(); i++) {
                    if (position == i) {
                        notifyview.getChildAt(i).setBackgroundColor(getColor(R.color.blue));
                        checked = notify.get(position);
                        key_pos = position;
                    } else {
                        notifyview.getChildAt(i).setBackgroundColor(getColor(R.color.white));
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(uid).child("1");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notify.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String var1 = snapshot.getValue(String.class);
                    String var2 = snapshot.getKey();
                    notify.add(var1);
                    keys.add(var2);
                }
                if(notify.size()==0)
                {
                    notify.add("Out of notifications!");
                }
                ArrayAdapter adapter = new ArrayAdapter(Notification.this, R.layout.list_item, R.id.label, notify);
                notifyview.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
