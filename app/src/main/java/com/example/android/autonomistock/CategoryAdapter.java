package com.example.android.autonomistock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kartheek on 14/4/18.
 */

public class CategoryAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CategoryHolder> mCategories;

    public CategoryAdapter(Context c, ArrayList<CategoryHolder> mCategories) {
        mContext = c;
        this.mCategories = mCategories;
    }

    public int getCount() {
        return mCategories.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView = new View(mContext);
        TextView mCategroyText;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            gridView = inflater.inflate(R.layout.gridview,null);
        }
            else {
            gridView = (View) convertView;

        }
        mCategroyText = (TextView) gridView.findViewById(R.id.category);
        mCategroyText.setText(mCategories.get(position).getCategoryName());
        return gridView;
    }
}

