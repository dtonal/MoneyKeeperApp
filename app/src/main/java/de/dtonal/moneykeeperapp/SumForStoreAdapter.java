package de.dtonal.moneykeeperapp;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by dtonal on 07.10.16.
 */

public class SumForStoreAdapter extends ArrayAdapter<SumForStore> {

    public SumForStoreAdapter(Context context, ArrayList<SumForStore> sumForStores) {
        super(context, 0, sumForStores);
        this.sort(new Comparator<SumForStore>() {
            @Override
            public int compare(SumForStore o1, SumForStore o2) {
                return Double.compare(o2.getPercent(),o1.getPercent());
            }
        });
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        SumForStore sumForStore = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sum_of_store, parent, false);

        }

        // Lookup view for data population

        TextView tvStore = (TextView) convertView.findViewById(R.id.textStore);

        TextView tvSum = (TextView) convertView.findViewById(R.id.textSum);

        TextView tvPercent = (TextView) convertView.findViewById(R.id.textPercent);

        tvStore.setText(sumForStore.getStore());

        tvSum.setText((DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(sumForStore.getSum())));

        tvPercent.setText((DecimalFormat.getPercentInstance(Locale.GERMANY).format(sumForStore.getPercent()*100)));

        return convertView;

    }

}
