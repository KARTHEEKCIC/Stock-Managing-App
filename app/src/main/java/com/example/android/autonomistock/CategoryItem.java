package com.example.android.autonomistock;

/**
 * Created by kartheek on 15/4/18.
 */

public class CategoryItem {
    private int mAvailability;
    private String mItemName;
    private double mQuantity;
    private int mItemId;

    CategoryItem(int mAvailability, String mItemName, int mItemId) {
        this.mAvailability = mAvailability;
        this.mItemName = mItemName;
        this.mItemId = mItemId;
        mQuantity = 0;
    }

    String getmItemName() {
        return mItemName;
    }

    int getmAvailability() {
        return mAvailability;
    }

    double getmQuantity() { return mQuantity; }

    int getmItemId() { return mItemId; }

    void setmAvailability(int mAvailability) {
        this.mAvailability = mAvailability;
    }

    void setmItemName(String mItemName) {
        this.mItemName = mItemName;
    }

    void setmQuantity(double mQuantity) { this.mQuantity = mQuantity; }

    void setmItemId(int mItemId) { this.mItemId =  mItemId; }

}
