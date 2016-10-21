package de.dtonal.moneykeeperapp.adapter;

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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import de.dtonal.moneykeeperapp.R;
import de.dtonal.moneykeeperapp.model.Cost;

/**
 * Created by dtonal on 04.10.16.
 */

public class CostsAdapter extends ArrayAdapter<Cost> {
    DateFormat dateFormat = new SimpleDateFormat("EEE dd.MM.yyyy", Locale.GERMANY);
    private SparseBooleanArray mSelectedItemsIds;
    private CompoundButton.OnCheckedChangeListener mListener;

    public CostsAdapter(Context context, ArrayList<Cost> costs) {
        super(context, 0, costs);
        mSelectedItemsIds = new SparseBooleanArray();
        mListener = new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mSelectedItemsIds.put((Integer)buttonView.getTag(), isChecked);
                else
                    mSelectedItemsIds.delete((Integer)buttonView.getTag());
                notifyDataSetChanged();
            }
        };
        this.sort(new Comparator<Cost>() {
            @Override
            public int compare(Cost o1, Cost o2) {
                return o2.getCreatedAt().compareTo(o1.getCreatedAt());
            }
        });
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

        TextView tvPrice = (TextView) convertView.findViewById(R.id.textPrice);

        // Populate the data into the template view using the data object
        CheckBox checkCost = (CheckBox) convertView.findViewById(R.id.checkCost);
        checkCost.setTag(Integer.valueOf(position));
        checkCost.setOnCheckedChangeListener(mListener);

        ImageView imageStore = (ImageView) convertView.findViewById(R.id.imageStore);
        RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.layoutCost);

        switch (cost.getStore())
        {
            case "Supermarkt":
                imageStore.setImageResource(R.drawable.ic_shopping_cart);
                rl.setBackgroundResource(R.color.colorSupermarket);
                break;
            case "Discounter":
                imageStore.setImageResource(R.drawable.ic_discounter);
                rl.setBackgroundResource(R.color.colorDiscounter);
                break;
            case "Drogiere":
                imageStore.setImageResource(R.drawable.ic_drugstore);
                rl.setBackgroundResource(R.color.colorDrugstore);
                break;
            case "Tiershop":
                imageStore.setImageResource(R.drawable.ic_petshop);
                rl.setBackgroundResource(R.color.colorPetshop);
                break;
            case "Baumarkt":
                imageStore.setImageResource(R.drawable.ic_tool);
                rl.setBackgroundResource(R.color.colorTool);
                break;
            case "Möbelhaus":
                imageStore.setImageResource(R.drawable.ic_furniture);
                rl.setBackgroundResource(R.color.colorFurniture);
                break;
            case "Buchhandlung":
                imageStore.setImageResource(R.drawable.ic_bookstore);
                rl.setBackgroundResource(R.color.colorBookstore);
                break;
            case "Restaurant":
                imageStore.setImageResource(R.drawable.ic_restaurant);
                rl.setBackgroundResource(R.color.colorRestaurant);
                break;
            case "Fussball":
                imageStore.setImageResource(R.drawable.ic_soccer);
                rl.setBackgroundResource(R.color.colorSoccer);
                break;
            case "Nähladen":
                imageStore.setImageResource(R.drawable.ic_sewing);
                rl.setBackgroundResource(R.color.colorSew);
                break;
            case "Stricken":
                imageStore.setImageResource(R.drawable.ic_knit);
                rl.setBackgroundResource(R.color.colorKnit);
                break;
            case "Tankstelle":
                imageStore.setImageResource(R.drawable.ic_gas);
                rl.setBackgroundResource(R.color.colorGas);
                break;
            case "Bäckerei":
                imageStore.setImageResource(R.drawable.ic_bakery);
                rl.setBackgroundResource(R.color.colorBakery);
                break;
        }

        tvCreatedAtDate.setText(dateFormat.format(cost.getCreatedAt()));

        tvPrice.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(cost.getPrice()));


        return convertView;

    }


    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
