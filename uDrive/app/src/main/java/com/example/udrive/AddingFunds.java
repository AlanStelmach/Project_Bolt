package com.example.udrive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class AddingFunds extends AppCompatActivity {

    private ProgressBar progressBar;
    private String value;
    private String yes = "com.example.udrive";
    private Button add_funds;
    private EditText value_of_funds;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        value = intent.getStringExtra(yes);
        setContentView(R.layout.activity_adding_funds);
        progressBar = (ProgressBar) findViewById(R.id.progressBar5);
        back = (ImageView) findViewById(R.id.back7);
        add_funds = (Button) findViewById(R.id.add_button);
        value_of_funds = (EditText) findViewById(R.id.value_of_funds);
        progressBar.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddingFunds.this, Wallet.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(yes,value);
                startActivity(intent);
            }
        });

        add_funds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String funds = value_of_funds.getText().toString();
                if(funds.isEmpty())
                {
                    Toast.makeText(AddingFunds.this, "Please eneter value of funds!", Toast.LENGTH_LONG).show();
                    value_of_funds.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid = user.getUid();
                    final HashMap<String, Object> map = new HashMap<>();
                    int result = Integer.parseInt(funds) + Integer.parseInt(value);
                    value = String.valueOf(result);
                    map.put("wallet", String.valueOf(result));
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).updateChildren(map);
                    value_of_funds.setText("");
                    Toast.makeText(AddingFunds.this, "Success! Enjoy your funds!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}