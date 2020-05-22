package com.example.udrive;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Frag3 extends Fragment {

    // -------- SIGN IN -------- //
    private TextView password_remainder;
    private EditText email, password;
    private Button facebook, signinb;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    public Frag3() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag3, container, false);

        email = (EditText) view.findViewById(R.id.Email);
        password = (EditText) view.findViewById(R.id.Password);
        signinb = (Button) view.findViewById(R.id.Sign_In_Button);
        facebook = (Button) view.findViewById(R.id.Facebook);
        password_remainder = (TextView) view.findViewById(R.id.password_remainder);
        progressBar = view.findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();

        password_remainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temail = email.getText().toString();
                if(temail.isEmpty())
                {
                    Toast.makeText(getActivity(),"Please enter e-mail!",Toast.LENGTH_LONG).show();
                    email.requestFocus();
                }
                else
                {
                    auth.sendPasswordResetEmail(temail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setCancelable(true);
                                builder.setTitle("E-mail has been send!");
                                builder.setMessage("Please check your e-mail account for more details!");
                                builder.show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

        signinb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temail = email.getText().toString();
                String tpassword = password.getText().toString();

                if(temail.isEmpty())
                {
                    Toast.makeText(getActivity(),"Please enter e-mail!",Toast.LENGTH_LONG).show();
                    email.requestFocus();
                }
                 else if(tpassword.isEmpty())
                {
                    Toast.makeText(getActivity(),"Please enetr password!",Toast.LENGTH_LONG).show();
                    password.requestFocus();
                }
                else
                {
                    logging_in(temail,tpassword);
                }
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    private void logging_in(String email, String password)
    {
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Success!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), ChooseUser.class);
                    startActivity(intent);
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Wrong login or password!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
