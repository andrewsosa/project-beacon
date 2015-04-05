package com.andrewsosa.beacon;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by andrewsosa on 3/23/15.
 */
public class DrawerItemAdapter<T> extends ArrayAdapter{




    Context context;
    int drawableResourceId;
    int textResourceId;
    int[] icons;
    String[] items;

    public DrawerItemAdapter(Context context, int layout, int textViewResourceId, String[] objects,
                             int drawableResourceId, int[] icons) {
        super(context, layout, textViewResourceId, objects);

        this.context = context;
        this.drawableResourceId = drawableResourceId;
        this.icons = icons;
        this.items = objects;
        this.textResourceId = textViewResourceId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        ImageView icon;
        //TextView text;

        /*try {
            text = (TextView) view.findViewById(textResourceId);
            icon = (ImageView) view.findViewById(drawableResourceId);
        } catch (ClassCastException e) {
            Log.e("DrawerItemAdapter", "You must supply a resource ID for an Image");
            throw new IllegalStateException(
                    "DrawerItemAdapter requires the resource ID to be an ImageView", e);
        }*/

        icon = (ImageView) view.findViewById(drawableResourceId);


        Drawable image = null;
        try {
            image = context.getResources().getDrawable(icons[position]);
            //image.setColorFilter(R.color.primaryColor, PorterDuff.Mode.MULTIPLY);
        } catch (Exception e) {
            Log.e("Beacon", "Drawable not found for position " + position);
        }

        if (image != null && icon !=null) {
            icon.setImageDrawable(image);

        }

        if (icon == null) {
            Log.d("Beacon", "icon not found in view.");
        } else if (image == null) {
            Log.d("Beacon", "image is null.");
        }

        return view;
    }


    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }
}
