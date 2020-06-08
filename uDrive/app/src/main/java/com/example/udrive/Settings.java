package com.example.udrive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class Settings extends AppCompatActivity {

    private EditText edit_profile_name, edit_profile_surname, edit_profile_pnumber;
    private TextView profile_email;
    private Button edit_data;
    private ImageView profilepicture;
    private TextView profile_name;
    private ImageView back;
    private ProgressBar progressBar;
    private Uri imageuri;
    private static final int IMAGE_REQUEST = 1;
    private String returnpoint = "com.example.udrive";
    private String returnoption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        returnoption = intent.getStringExtra(returnpoint);
        back = (ImageView) findViewById(R.id.back8);
        edit_profile_name = (EditText) findViewById(R.id.edit_profile_name);
        edit_profile_surname = (EditText) findViewById(R.id.edit_profile_surname);
        edit_profile_pnumber = (EditText) findViewById(R.id.edit_profile_pnumber);
        profile_email = (TextView) findViewById(R.id.profile_email);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profilepicture = (ImageView) findViewById(R.id.profilepicture);
        edit_data = (Button) findViewById(R.id.edit_data);
        progressBar = (ProgressBar) findViewById(R.id.progressBar6);
        progressBar.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(returnoption.equals("1")) {
                    Intent intent = new Intent(Settings.this, CustomerMapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else  if(returnoption.equals("2"))
                {
                    Intent intent = new Intent(Settings.this, DriverMapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        edit_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_profile_name.getText().toString();
                String surname = edit_profile_surname.getText().toString();
                String pnumber = edit_profile_pnumber.getText().toString();

                if(name.isEmpty())
                {
                    Toast.makeText(Settings.this, "Please enter new name!", Toast.LENGTH_LONG).show();
                    edit_profile_name.requestFocus();
                }
                else  if(surname.isEmpty())
                {
                    Toast.makeText(Settings.this, "Please enter new surname!", Toast.LENGTH_LONG).show();
                    edit_profile_surname.requestFocus();
                }
                else if(pnumber.isEmpty())
                {
                    Toast.makeText(Settings.this, "Please enter new phone number!", Toast.LENGTH_LONG).show();
                    edit_profile_pnumber.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("name",name);
                    hashMap.put("surname",surname);
                    hashMap.put("pnumber",pnumber);
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).updateChildren(hashMap);
                    if(returnoption.equals("1"))
                    {
                        final HashMap<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("surname", surname);
                        FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(uid).updateChildren(map);
                    }
                    else if(returnoption.equals("2"))
                    {
                        final HashMap<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("surname", surname);
                        FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(uid).updateChildren(map);
                    }
                    Toast.makeText(Settings.this, "Success!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Users").child(uid).child("name").getValue(String.class);
                String surname = dataSnapshot.child("Users").child(uid).child("surname").getValue(String.class);
                String email = dataSnapshot.child("Users").child(uid).child("email").getValue(String.class);
                String pnumber = dataSnapshot.child("Users").child(uid).child("pnumber").getValue(String.class);

                edit_profile_name.setText(name);
                edit_profile_surname.setText(surname);
                edit_profile_pnumber.setText(pnumber);
                profile_email.setText(email);
                profile_name.setText(name+" "+surname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("Users_Images").child(uid).child("1");
        reference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilepicture.setImageBitmap(bitmap);
            }
        });
    }

    private void FileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageuri = data.getData();
            UploadImage();
        }
    }

    private String getFileExtension (Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        if(imageuri != null)
        {
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("Users_Images").child(uid).child("1");
            fileRef.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            if(url.isEmpty()) {
                                progressDialog.dismiss();
                                Toast.makeText(Settings.this, "Error! Please try again!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(Settings.this, "Image uploaded successfully!", Toast.LENGTH_LONG).show();
                                onStart();
                            }
                        }
                    });
                }
            });
        }
    }
}