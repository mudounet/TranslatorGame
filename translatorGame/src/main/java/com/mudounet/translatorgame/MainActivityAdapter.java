package com.mudounet.translatorgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by gmanciet on 19/12/2016.
 */

public class MainActivityAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public MainActivityAdapter(Context context, String[] values) {
        super(context, R.layout.activity_main_adapter, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_main_adapter, parent, false);

        TextView firstLineView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondLineView = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        firstLineView.setText(values[position]);
        secondLineView.setText(values[position]);

        // Change the icon :
        //imageView.setImageResource(R.drawable.no);
        return rowView;
    }
}