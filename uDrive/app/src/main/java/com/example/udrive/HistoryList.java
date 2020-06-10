package com.example.udrive;

import android.app.Activity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HistoryList extends ArrayAdapter<HistoryItem> {

    private Activity context;
    private ArrayList<HistoryItem> historyItemArrayList;

    public HistoryList(Activity context, ArrayList<HistoryItem> historyItemArrayList)
    {
        super(context, R.layout.history_layout, historyItemArrayList);
        this.context = context;
        this.historyItemArrayList = historyItemArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View history_item = inflater.inflate(R.layout.history_layout, null, true);

        TextView current_location = (TextView) history_item.findViewById(R.id.current_location);
        TextView destiny_location = (TextView) history_item.findViewById(R.id.destiny_location);
        TextView price = (TextView) history_item.findViewById(R.id.price);
        TextView status = (TextView) history_item.findViewById(R.id.status);

        HistoryItem historyItem = historyItemArrayList.get(position);

        if(historyItem.getCurrent_location().equals("You don't have any travel history!"))
        {
            current_location.setText(historyItem.getCurrent_location());
            destiny_location.setText(historyItem.getDestiny_location());
            price.setText(historyItem.getPrice());
            status.setText(historyItem.getStatus());
        }
        else {
            current_location.setText("Current location: " + historyItem.getCurrent_location());
            destiny_location.setText("Destiny location: " + historyItem.getDestiny_location());
            price.setText("Price: " + historyItem.getPrice());
            status.setText("Status: " + historyItem.getStatus());
        }

        return history_item;
    }
}
