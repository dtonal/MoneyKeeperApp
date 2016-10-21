package de.dtonal.moneykeeperapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import de.dtonal.moneykeeperapp.R;
import de.dtonal.moneykeeperapp.model.SumForStore;

/**
 * Created by dtonal on 07.10.16.
 */

public class SumForStoreAdapter extends ArrayAdapter<SumForStore> {

    public SumForStoreAdapter(Context context, ArrayList<SumForStore> sumForStores) {
        super(context, 0, sumForStores);
        this.sort(new Comparator<SumForStore>() {
            @Override
            public int compare(SumForStore o1, SumForStore o2) {
                int retValue = 0;
                try {
                    retValue = Double.compare(o2.getPercent(),o1.getPercent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(retValue == 0)
                {
                    retValue = o1.getStore().compareTo(o2.getStore());
                }
                return retValue;
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

        LinearLayout layoutSumOfStore = (LinearLayout) convertView.findViewById(R.id.layoutSumOfStore);

        tvStore.setText(sumForStore.getStore());

        tvSum.setText((DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(sumForStore.getSum())));

        tvPercent.setText((DecimalFormat.getPercentInstance(Locale.GERMANY).format(sumForStore.getPercent())));

        layoutSumOfStore.setBackgroundResource(StoreAdapter.getBackgroundResourceForName(sumForStore.getStore()));

        return convertView;

    }

}
