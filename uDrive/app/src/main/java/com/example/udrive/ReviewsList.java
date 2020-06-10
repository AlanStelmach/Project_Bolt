package com.example.udrive;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ReviewsList extends ArrayAdapter<Rating> {

    private Activity context;
    private ArrayList<Rating> ratings;

    public ReviewsList(Activity context, ArrayList<Rating> ratings)
    {
        super(context, R.layout.history_layout, ratings);
        this.context = context;
        this.ratings = ratings;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rating = inflater.inflate(R.layout.rating_layout, null, true);
        TextView name_surname = (TextView) rating.findViewById(R.id.name_surname_rating);
        TextView text = (TextView) rating.findViewById(R.id.comment_rating);
        RatingBar ratingBar = (RatingBar) rating.findViewById(R.id.ratingbar_rating);

        Rating rating1 = ratings.get(position);
        if(rating1.getText().equals("No ratings!"))
        {
            name_surname.setText(rating1.getName_surname());
            text.setText(rating1.getText());
            ratingBar.setRating(rating1.getStars());
        }
        else {
            name_surname.setText(rating1.getName_surname());
            text.setText(rating1.getText());
            ratingBar.setRating(rating1.getStars());
        }

        return rating;
    }
}
