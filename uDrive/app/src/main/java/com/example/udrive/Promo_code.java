package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;

public class Promo_code extends AppCompatActivity {

    private ImageView back;
    private ProgressBar progressBar;
    private Button check_code;
    private EditText promo_code;
    private int value;
    private int var_wallet;
    private String yes = "com.example.udrive";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_code);
        Intent intent = getIntent();
        String message = intent.getStringExtra(yes);
        var_wallet = Integer.parseInt(message);
        back = (ImageView) findViewById(R.id.back2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        check_code = (Button) findViewById(R.id.check_code);
        promo_code = (EditText) findViewById(R.id.promo_code);
        progressBar.setVisibility(View.GONE);

        check_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String promocode = promo_code.getText().toString();
                final ArrayList<Promo> promos = new ArrayList<>();
                if(promocode.isEmpty())
                {
                    Toast.makeText(Promo_code.this, "Please enter promo code!",Toast.LENGTH_LONG).show();
                    promo_code.requestFocus();
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Promo");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            promos.clear();
                            boolean check = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String var1 = snapshot.child("name").getValue(String.class);
                                Integer var2 = snapshot.child("value").getValue(Integer.class);
                                promos.add(new Promo(var1,var2));
                            }
                            for (int i = 0; i < promos.size(); i++) {
                                if (promocode.equals(promos.get(i).getPromo_name())) {
                                    check = true;
                                    value = promos.get(i).getValue();
                                    break;
                                }
                            }
                            if (check) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final String uid = user.getUid();
                                final HashMap<String, Object> map = new HashMap<>();
                                int result = var_wallet+value;
                                map.put("wallet", String.valueOf(result));
                                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).updateChildren(map);
                                promo_code.setText("");
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Promo_code.this, "You get extra money to your use!", Toast.LENGTH_LONG).show();
                            }
                            else
                                {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Promo_code.this, "Promo code not found!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Promo_code.this, CustomerMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}