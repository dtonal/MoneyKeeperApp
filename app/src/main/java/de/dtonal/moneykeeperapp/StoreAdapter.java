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
        switch (text) {
            case "Supermarkt":
                icon.setImageResource(R.drawable.ic_shopping_cart);
                layout.setBackgroundResource(R.color.colorSupermarket);
                break;
            case "Discounter":
                icon.setImageResource(R.drawable.ic_discounter);
                layout.setBackgroundResource(R.color.colorDiscounter);
                break;
            case "Drogiere":
                icon.setImageResource(R.drawable.ic_drugstore);
                layout.setBackgroundResource(R.color.colorDrugstore);
                break;
            case "Tiershop":
                icon.setImageResource(R.drawable.ic_petshop);
                layout.setBackgroundResource(R.color.colorPetshop);
                break;
            case "Baumarkt":
                icon.setImageResource(R.drawable.ic_tool);
                layout.setBackgroundResource(R.color.colorTool);
                break;
            case "Möbelhaus":
                icon.setImageResource(R.drawable.ic_furniture);
                layout.setBackgroundResource(R.color.colorFurniture);
                break;
            case "Buchhandlung":
                icon.setImageResource(R.drawable.ic_bookstore);
                layout.setBackgroundResource(R.color.colorBookstore);
                break;
            case "Restaurant":
                icon.setImageResource(R.drawable.ic_restaurant);
                layout.setBackgroundResource(R.color.colorRestaurant);
                break;
            case "Fussball":
                icon.setImageResource(R.drawable.ic_soccer);
                layout.setBackgroundResource(R.color.colorSoccer);
                break;
            case "Nähladen":
                icon.setImageResource(R.drawable.ic_sewing);
                layout.setBackgroundResource(R.color.colorSew);
                break;
            case "Stricken":
                icon.setImageResource(R.drawable.ic_knit);
                layout.setBackgroundResource(R.color.colorKnit);
                break;
            case "Tankstelle":
                icon.setImageResource(R.drawable.ic_gas);
                layout.setBackgroundResource(R.color.colorGas);
                break;
            case "Bäckerei":
                icon.setImageResource(R.drawable.ic_bakery);
                layout.setBackgroundResource(R.color.colorBakery);
                break;
        }
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

    public int getBackgroundResourceForPosition(int position) {

        String text = (String) getItem(position);
        switch (text) {
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
                
        }
        return 0;
    }
}
