package com.example.udrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Wallet extends AppCompatActivity {

    private ImageView back, plus;
    private TextView add_funds, add_card, udrive_cash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        back = (ImageView) findViewById(R.id.back3);
        plus = (ImageView) findViewById(R.id.plus);
        add_funds = (TextView) findViewById(R.id.add_funds);
        add_card = (TextView) findViewById(R.id.add_card);
        udrive_cash = (TextView) findViewById(R.id.udrive_cash);

        Bundle bundle = getIntent().getExtras();

        if(bundle.getString("data") != null)
        {
            String value = bundle.getString("data");
            udrive_cash.setText(value+" PLN");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wallet.this, CustomerMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
