package com.example.udrive;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Frag3 extends Fragment {

    // -------- SIGN IN -------- //
    private TextView password_remainder;
    private EditText email, password;
    private Button facebook, signinb;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private ImageView fb_iamge;

    public Frag3() {}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag3, container, false);

        fb_iamge = (ImageView) view.findViewById(R.id.fb_image);
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
                Intent intent = new Intent(getActivity(), RestoringPassword.class);
                startActivity(intent);
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
                    Toast.makeText(getActivity(),"Please enter password!",Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
                startActivity(intent);
            }
        });

        fb_iamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
                startActivity(intent);
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
                FirebaseUser user = auth.getCurrentUser();
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        if(user.isEmailVerified()) {
                            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), ChooseUser.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else
                        {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setCancelable(true);
                            builder.setTitle("Verification first!");
                            builder.setMessage("We can't let you log in without verification! Please check your e-mail account for more details!");
                            builder.show();
                        }
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