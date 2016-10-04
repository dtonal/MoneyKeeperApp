package de.dtonal.moneykeeperapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by dtonal on 04.10.16.
 */

public class CostsAdapter extends ArrayAdapter<Cost> {
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");


    public CostsAdapter(Context context, ArrayList<Cost> costs) {
        super(context, 0, costs);
    }



    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        Cost cost = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_cost, parent, false);

        }

        // Lookup view for data population

        TextView tvCreatedAtDate = (TextView) convertView.findViewById(R.id.textCreatedAtDate);

        TextView tvCreatedAtTime = (TextView) convertView.findViewById(R.id.textCreatedAtTime);

        TextView tvPrice = (TextView) convertView.findViewById(R.id.textPrice);

        TextView tvStore = (TextView) convertView.findViewById(R.id.textStore);

        TextView tvUser = (TextView) convertView.findViewById(R.id.textUser);

        // Populate the data into the template view using the data object

        tvCreatedAtDate.setText(dateFormat.format(cost.getCreatedAt()));

        tvCreatedAtTime.setText(timeFormat.format(cost.getCreatedAt()));

        tvPrice.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(cost.getPrice()));

        tvStore.setText(cost.getStore());

        tvUser.setText(cost.getUserId().toString());

        return convertView;

    }
}
