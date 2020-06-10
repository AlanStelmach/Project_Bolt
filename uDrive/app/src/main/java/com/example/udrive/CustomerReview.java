package com.example.udrive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class CustomerReview extends AppCompatActivity {

    private ImageView mDriverImageProfile;
    private EditText mCommentText;
    private TextView mNameDriver, mSurnameDriver;
    private Button mSubmitReviev;
    private RatingBar mRatingDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_review);

        mDriverImageProfile = (ImageView) findViewById(R.id.driverProfileImage);
        mCommentText = (EditText) findViewById(R.id.commentText);
        mNameDriver = (TextView) findViewById(R.id.nameDriver);
        mSurnameDriver = (TextView) findViewById(R.id.surnameDriver);
        mSubmitReviev = (Button) findViewById(R.id.submitButton);
        mRatingDriver = (RatingBar) findViewById(R.id.ratingForDriver);

    }
}