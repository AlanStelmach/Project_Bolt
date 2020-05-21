package com.example.udrive;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Frag2 extends Fragment {

    // -------- SIGN UP -------- //
    private Button signupb;
    private EditText etname, etsurname, etemail, etpassword, etpnumber;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Handler handler = new Handler();

    public Frag2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag2, container, false);

        etname = (EditText) view.findViewById(R.id.Name);
        etsurname = (EditText) view.findViewById(R.id.Surname);
        etemail = (EditText) view.findViewById(R.id.Email1);
        etpassword = (EditText) view.findViewById(R.id.Password1);
        etpnumber = (EditText) view.findViewById(R.id.Phone);
        signupb = (Button) view.findViewById(R.id.Sign_Up_Button);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();

        signupb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String name = etname.getText().toString();
                String surname = etsurname.getText().toString();
                String email = etemail.getText().toString();
                String pnumber = etpnumber.getText().toString();
                String password = etpassword.getText().toString();

                if(name.isEmpty()) {
                    Toast.makeText(getActivity(),"Please enter name!",Toast.LENGTH_LONG).show();
                    etname.requestFocus();
                }
                else if(surname.isEmpty()) {
                    Toast.makeText(getActivity(),"Please enter surname!",Toast.LENGTH_LONG).show();
                    etsurname.requestFocus();
                }
                else if(email.isEmpty()) {
                    Toast.makeText(getActivity(),"Please enter e-mail!",Toast.LENGTH_LONG).show();
                    etemail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getActivity(),"E-mail form is incorrect!",Toast.LENGTH_LONG).show();
                    etemail.requestFocus();
                }
                else if(password.isEmpty()) {
                    Toast.makeText(getActivity(),"Please enter password!",Toast.LENGTH_LONG).show();
                    etpassword.requestFocus();
                }
                else if(password.length() < 6) {
                    Toast.makeText(getActivity(),"Password is to short!",Toast.LENGTH_LONG).show();
                    etpassword.requestFocus();
                }
                else if(pnumber.isEmpty()) {
                    Toast.makeText(getActivity(),"Please eneter phone number!",Toast.LENGTH_LONG).show();
                    etpnumber.requestFocus();
                }
                else if(pnumber.length() != 9) {
                    Toast.makeText(getActivity(),"Phone number must be 9 decimals long!",Toast.LENGTH_LONG).show();
                    etpnumber.requestFocus();
                }
                else {
                    registerUser();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getActivity(), SignUp.class);
                            startActivity(intent);
                        }
                    },10000);
                }
            }
        });

        return view;
    }

    private void registerUser() {
        final String name = etname.getText().toString();
        final String surname = etsurname.getText().toString();
        final String email = etemail.getText().toString();
        final String pnumber = etpnumber.getText().toString();
        final String password = etpassword.getText().toString();
        final String wallet = "30";
        final String isdriver = "false";

        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            User user = new User(name, surname, email, pnumber, wallet, isdriver);
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful())
                                    {
                                        FirebaseUser user1 = auth.getCurrentUser();
                                        user1.sendEmailVerification().addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful())
                                                {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setCancelable(true);
                                                    builder.setTitle("Verification has been send!");
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
                                        Toast.makeText(getActivity(),"Success!",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(getActivity(),"Error!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

}