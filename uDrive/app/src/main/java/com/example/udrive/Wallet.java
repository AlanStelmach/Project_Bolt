package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Wallet extends AppCompatActivity {

    private ImageView back, plus;
    private TextView add_funds, add_card, udrive_cash;
    private String yes = "com.example.udrive";
    private String chosen_one = "com.example.udrive.creditcard";
    private ListView cardview;
    private String number="";
    final ArrayList<String> creditCardsnumber = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Intent intent = getIntent();
        final String value = intent.getStringExtra(yes);
        back = (ImageView) findViewById(R.id.back3);
        plus = (ImageView) findViewById(R.id.plus);
        add_funds = (TextView) findViewById(R.id.add_funds);
        add_card = (TextView) findViewById(R.id.add_card);
        udrive_cash = (TextView) findViewById(R.id.udrive_cash);
        udrive_cash.setText(value+" PLN");
        cardview = (ListView) findViewById(R.id.wallet_view);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet.this, CustomerMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet.this, AddingCreditCard.class);
                intent.putExtra(yes,value);
                startActivity(intent);
            }
        });

        add_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet.this, AddingCreditCard.class);
                intent.putExtra(yes,value);
                startActivity(intent);
            }
        });

        add_funds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number.isEmpty())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Wallet.this);
                    builder.setCancelable(true);
                    builder.setTitle("Select payment method!");
                    builder.setMessage("Please select payment method from list below.");
                    builder.show();
                }
                else {
                    Intent intent = new Intent(Wallet.this, AddingFunds.class);
                    intent.putExtra(yes, value);
                    intent.putExtra(chosen_one,number);
                    startActivity(intent);
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
                ArrayAdapter adapter = new ArrayAdapter(Wallet.this, R.layout.list_item, R.id.label, creditCardsnumber);
                cardview.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
