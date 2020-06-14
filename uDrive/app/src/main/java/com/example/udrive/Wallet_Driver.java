package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

public class Wallet_Driver extends AppCompatActivity {

    private String yes = "com.example.udrive";
    private String data = "com.example.udrive/status";
    private String value ="";
    private String returnpoint="2";
    private ImageView back;
    private ImageView plus;
    private TextView add_card;
    private TextView udrive_cash;
    ArrayList<String> creditCardsnumber = new ArrayList<>();
    private ListView cardview;
    private String number="";
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet__driver);
        Intent intent = getIntent();
        value = intent.getStringExtra(yes);
        udrive_cash = (TextView) findViewById(R.id.udrive_cash2);
        udrive_cash.setText(value+" PLN");
        plus = (ImageView) findViewById(R.id.plus2);
        add_card = (TextView) findViewById(R.id.add_card2);
        back = (ImageView) findViewById(R.id.back9);
        cardview = (ListView) findViewById(R.id.wallet_view2);
        send = (Button) findViewById(R.id.send_money);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet_Driver.this, DriverMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet_Driver.this, AddingCreditCard.class);
                intent.putExtra(yes,value);
                intent.putExtra(data,returnpoint);
                startActivity(intent);
            }
        });

        add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet_Driver.this, AddingCreditCard.class);
                intent.putExtra(yes,value);
                intent.putExtra(data,returnpoint);
                startActivity(intent);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number.isEmpty())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Wallet_Driver.this);
                    builder.setCancelable(true);
                    builder.setTitle("Select payment method!");
                    builder.setMessage("Please select payment method from list below.");
                    builder.show();
                }
                else if(number.equals("No any credit card"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Wallet_Driver.this);
                    builder.setCancelable(true);
                    builder.setTitle("Add some credit card!");
                    builder.setMessage("Please add some credit card first.");
                    builder.show();
                }
                else {
                    Toast.makeText(Wallet_Driver.this, "Done! Money has been send successfully!", Toast.LENGTH_LONG).show();
                    udrive_cash.setText("0 PLN");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    final HashMap<String, Object> map = new HashMap<>();
                    map.put("wallet", "0");
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).updateChildren(map);
                }
            }
        });

        cardview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                for (int i = 0; i < cardview.getChildCount(); i++) {
                    if (position == i) {
                        cardview.getChildAt(i).setBackgroundColor(getColor(R.color.blue));
                        number = creditCardsnumber.get(position);
                    } else {
                        cardview.getChildAt(i).setBackgroundColor(getColor(R.color.white));
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("CreditCard").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                creditCardsnumber.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String var1 = snapshot.child("number").getValue(String.class);
                    creditCardsnumber.add(var1);
                }
                if(creditCardsnumber.size() == 0)
                {
                    creditCardsnumber.add("No any credit card");
                }
                ArrayAdapter adapter = new ArrayAdapter(Wallet_Driver.this, R.layout.list_item, R.id.label, creditCardsnumber);
                cardview.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}