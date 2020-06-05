package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class AddingCreditCard extends AppCompatActivity {

    private ImageView back;
    private EditText credit_card_name, credit_card_number, date1, date2, cvv;
    private String value;
    private String yes = "com.example.udrive";
    private Button save;
    private ProgressBar progressBar;
    private int limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_credit_card);
        Intent intent = getIntent();
        value = intent.getStringExtra(yes);
        back = (ImageView) findViewById(R.id.back6);
        credit_card_name = (EditText) findViewById(R.id.credit_card_name);
        credit_card_number = (EditText) findViewById(R.id.credit_card_number);
        date1 = (EditText) findViewById(R.id.date1);
        date2 = (EditText) findViewById(R.id.date2);
        cvv = (EditText) findViewById(R.id.cvv);
        save = (Button) findViewById(R.id.save);
        progressBar = (ProgressBar) findViewById(R.id.progressBar4);
        progressBar.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddingCreditCard.this, Wallet.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(yes,value);
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = credit_card_name.getText().toString();
                final String number = credit_card_number.getText().toString();
                String month = date1.getText().toString();
                String year = date2.getText().toString();
                final String cvv_number = cvv.getText().toString();
                final String data = month+"/"+year;

                if(name.isEmpty())
                {
                    Toast.makeText(AddingCreditCard.this,"Please enter credit card name!", Toast.LENGTH_LONG).show();
                    credit_card_name.requestFocus();
                }
                else if(number.isEmpty())
                {
                    Toast.makeText(AddingCreditCard.this,"Please enter credit card number!", Toast.LENGTH_LONG).show();
                    credit_card_number.requestFocus();
                }
                else if(number.length()<16)
                {
                    Toast.makeText(AddingCreditCard.this,"Credit card number too short!", Toast.LENGTH_LONG).show();
                    credit_card_number.requestFocus();
                }
                else if(month.isEmpty())
                {
                    Toast.makeText(AddingCreditCard.this,"Please enter month of expire!", Toast.LENGTH_LONG).show();
                    date1.requestFocus();
                }
                else if(Integer.parseInt(month) == 0 || Integer.parseInt(month) > 12)
                {
                    Toast.makeText(AddingCreditCard.this,"Wrong month format!", Toast.LENGTH_LONG).show();
                    date1.requestFocus();
                }
                else if(year.isEmpty())
                {
                    Toast.makeText(AddingCreditCard.this,"Please enter year of expire!", Toast.LENGTH_LONG).show();
                    date2.requestFocus();
                }
                else if(Integer.parseInt(year) < 2020)
                {
                    Toast.makeText(AddingCreditCard.this,"Your card is expired!", Toast.LENGTH_LONG).show();
                    date2.requestFocus();
                }
                else if(cvv_number.isEmpty())
                {
                    Toast.makeText(AddingCreditCard.this,"Please enter CVV number!", Toast.LENGTH_LONG).show();
                    cvv.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid = user.getUid();
                    CreditCard creditCard = new CreditCard(name, number, data, cvv_number);
                    FirebaseDatabase.getInstance().getReference().child("CreditCard").child(uid).push().setValue(creditCard).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseDatabase.getInstance().getReference().child("Notifications").child(uid).child("1").push().setValue("You successfully added new credit card "+name+"!");
                                progressBar.setVisibility(View.GONE);
                                credit_card_name.setText("");
                                credit_card_number.setText("");
                                date1.setText("");
                                date2.setText("");
                                cvv.setText("");
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddingCreditCard.this);
                                builder.setCancelable(true);
                                builder.setTitle("Credit card successfully added!");
                                builder.setMessage("");
                                builder.show();
                            } else {
                                Toast.makeText(AddingCreditCard.this, "I'm sorry, but something went terribly wrong!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }
}