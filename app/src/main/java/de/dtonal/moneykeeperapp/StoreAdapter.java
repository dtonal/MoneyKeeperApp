package de.dtonal.moneykeeperapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dtonal on 06.10.16.
 */

public class StoreAdapter extends ArrayAdapter  {

    public StoreAdapter(Context context, List<String> list) {
        super(context, 0, list);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        String text = (String) getItem(position);
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_stores, parent, false);

        }
        ImageView icon = (ImageView) convertView.findViewById(R.id.imageView);
        TextView names = (TextView) convertView.findViewById(R.id.textView);
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layoutStore);
        names.setText(text);
        icon.setImageResource(getImageResourceForName(text));
        layout.setBackgroundResource(getBackgroundResourceForName(text));
        return convertView;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public static int getBackgroundResourceForName(String storeAsText) {
        switch (storeAsText) {
            case "Supermarkt":
                return R.color.colorSupermarket;
                
            case "Discounter":
                return (R.color.colorDiscounter);
                
            case "Drogiere":
                return (R.color.colorDrugstore);
                
            case "Tiershop":
                return (R.color.colorPetshop);
                
            case "Baumarkt":
                return (R.color.colorTool);
                
            case "Möbelhaus":
                return (R.color.colorFurniture);
                
            case "Buchhandlung":
                return (R.color.colorBookstore);
                
            case "Restaurant":
                return (R.color.colorRestaurant);
                
            case "Fussball":
                return (R.color.colorSoccer);
                
            case "Nähladen":
                return (R.color.colorSew);
                
            case "Stricken":
                return (R.color.colorKnit);
                
            case "Tankstelle":
                return (R.color.colorGas);
                
            case "Bäckerei":
                return (R.color.colorBakery);

            case "Internet":
                return (R.color.colorInternet);

            case "Bioladen":
                return (R.color.colorEcostore);


        }
        return 0;
    }


    public static int getImageResourceForName(String storeAsText) {
        switch (storeAsText) {
            case "Supermarkt":
                return R.drawable.ic_shopping_cart;

            case "Discounter":
                return R.drawable.ic_discounter;

            case "Drogiere":
                return R.drawable.ic_drugstore;

            case "Tiershop":
                return R.drawable.ic_petshop;

            case "Baumarkt":
                return R.drawable.ic_tool;

            case "Möbelhaus":
                return R.drawable.ic_furniture;

            case "Buchhandlung":
                return R.drawable.ic_bookstore;

            case "Restaurant":
                return R.drawable.ic_restaurant;

            case "Fussball":
                return R.drawable.ic_soccer;

            case "Nähladen":
                return R.drawable.ic_sewing;

            case "Stricken":
                return R.drawable.ic_knit;

            case "Tankstelle":
                return R.drawable.ic_gas;

            case "Bäckerei":
                return R.drawable.ic_bakery;

            case "Internet":
                return R.drawable.ic_internet;

            case "Bioladen":
                return R.drawable.ic_ecostore;

        }
        return 0;
    }

    public int getBackgroundResourceForName(int position) {
         return getBackgroundResourceForName((String)getItem(position));
    }
}
