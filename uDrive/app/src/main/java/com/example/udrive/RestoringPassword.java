package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RestoringPassword extends AppCompatActivity {
    private EditText email;
    private Button sent;
    private ImageView back;
    private FirebaseAuth auth;
    private String temail ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restoring_password);
        email = (EditText) findViewById(R.id.RestoringPassword);
        sent = (Button) findViewById(R.id.SentButton);
        back = (ImageView) findViewById(R.id.back1);

        auth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestoringPassword.this,SignUp.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temail = email.getText().toString();
                if(temail.isEmpty())
                {
                    Toast.makeText(RestoringPassword.this,"Please enter e-mail!",Toast.LENGTH_LONG).show();
                    email.requestFocus();
                }
                else
                {
                    auth.sendPasswordResetEmail(temail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                email.setText("");
                                AlertDialog.Builder builder = new AlertDialog.Builder(RestoringPassword.this);
                                builder.setCancelable(true);
                                builder.setTitle("E-mail has been send!");
                                builder.setMessage("Please check your e-mail account for more details!");
                                builder.show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RestoringPassword.this);
                                builder.setCancelable(true);
                                builder.setTitle("Error!");
                                builder.setMessage("Sorry for that! :c");
                                builder.show();
                            }
                        }
                    });
                }
            }
        });
    }
}