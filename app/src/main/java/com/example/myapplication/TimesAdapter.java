package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TimesAdapter extends ArrayAdapter<String> {
    public TimesAdapter(Context context, List<String> times) {
        super(context, 0, times);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String time = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_time, parent, false);
        }
        TextView textViewTime = convertView.findViewById(R.id.text_view_time);
        textViewTime.setText(time);
        return convertView;
    }
}
